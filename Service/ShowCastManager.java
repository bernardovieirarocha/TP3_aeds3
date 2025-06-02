package Service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import Model.Cast;
import Model.TvShow;

public class ShowCastManager {
    private FileManager<Cast> castFileManager;
    private FileManager<TvShow> showFileManager;
    public ArvoreBMais<ShowCastPair> relationshipTree; // Made public for diagnostic access

    public void testTreeInsertion() {
        try {
            System.out.println("=== Tree Insertion Test ===");
            ShowCastPair testPair = new ShowCastPair(999, 999);
            boolean result = relationshipTree.create(testPair);
            System.out.println("Test insertion result: " + (result ? "SUCCESS" : "FAILURE"));
            
            // Try to read it back
            ArrayList<ShowCastPair> readResult = relationshipTree.read(testPair);
            System.out.println("Test read result: " + readResult.size() + " pairs found");
            
            // Clean up test data
            relationshipTree.delete(testPair);
            System.out.println("=== End Tree Test ===");
        } catch (Exception e) {
            System.err.println("Error in tree test: " + e.getMessage());
        }
    }

    public ShowCastManager(FileManager<TvShow> showFileManager, FileManager<Cast> castFileManager) {
        this.showFileManager = showFileManager;
        this.castFileManager = castFileManager;
        
        try {
            // Define data path
            String dataPath = "dados";
            String indexFile = dataPath + "/showCast.db";
            
            // Check if index file exists
            java.io.File idxFile = new java.io.File(indexFile);
            boolean newFile = !idxFile.exists();

            // Initialize B+ tree for relationships
            Constructor<ShowCastPair> constructor = ShowCastPair.class.getConstructor();
            this.relationshipTree = new ArvoreBMais<>(
                    constructor,
                    4, // Tree order
                    indexFile
            );

            // If file is new, initialize with dummy record
            if (newFile) {
                try {
                    System.out.println("Initializing new index file with dummy record...");
                    ShowCastPair dummyPair = new ShowCastPair(0, 0);
                    relationshipTree.create(dummyPair);
                    relationshipTree.delete(dummyPair);
                    System.out.println("Index initialization complete.");
                } catch (Exception e) {
                    System.err.println("Warning: Could not initialize index: " + e.getMessage());
                }
            }

            // Verify tree integrity
            verifyTreeIntegrity();

        } catch (Exception e) {
            System.err.println("ERROR initializing ShowCastManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verifyTreeIntegrity() {
        try {
            boolean isEmpty = relationshipTree.empty();
            System.out.println("Tree integrity check - Empty: " + isEmpty);
        } catch (Exception e) {
            System.err.println("Error checking tree integrity: " + e.getMessage());
            // Try to reconstruct index if there's an integrity issue
            rebuildIndex();
        }
    }

    // Method to rebuild complete index
    private void rebuildIndex() {
        try {
            int totalRecords = 0;

            // Go through all cast members and rebuild tree
            int lastCastId = castFileManager.ultimoId();
            for (int i = 1; i <= lastCastId; i++) {
                Cast cast = castFileManager.read(i);
                if (cast != null && cast.getIDSerie() > 0) {
                    try {
                        ShowCastPair pair = new ShowCastPair(cast.getIDSerie(), cast.getId());
                        relationshipTree.create(pair);
                        totalRecords++;
                    } catch (Exception e) {
                        System.err.println("Error inserting cast " + i + " in index: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Index rebuild failed: " + e.getMessage());
        }
    }
    
    // Add relationship between show and cast member
    public boolean addRelationship(int showId, int castId) throws Exception {
        // Check if show exists
        TvShow show = showFileManager.read(showId);
        if (show == null) {
            System.err.println("ERROR: Show ID " + showId + " not found when adding relationship");
            return false;
        }

        // Check if cast member exists
        Cast cast = castFileManager.read(castId);
        if (cast == null) {
            System.err.println("ERROR: Cast member ID " + castId + " not found when adding relationship");
            return false;
        }

        // Update show ID in cast member if necessary
        if (cast.getIDSerie() != showId) {
            cast.setIDSerie(showId);
            castFileManager.update(cast);
        }

        try {
            // First check if relationship already exists in tree
            ShowCastPair searchPair = new ShowCastPair(showId, castId);
            ArrayList<ShowCastPair> pairs = relationshipTree.read(searchPair);
            boolean alreadyExists = false;

            // Check if exact pair already exists
            for (ShowCastPair pair : pairs) {
                if (pair.getIdSerie() == showId && pair.getIdAtor() == castId) {
                    alreadyExists = true;
                    break;
                }
            }

            if (alreadyExists) {
                System.out.println("Relationship between show " + showId + " and cast " + castId + " already exists");
                return true;
            }

            // Try to insert up to 3 times if it fails
            boolean result = false;
            for (int attempts = 1; attempts <= 3 && !result; attempts++) {
                System.out.println("Insertion attempt " + attempts + " for show " + showId + " and cast " + castId);
                
                ShowCastPair pair = new ShowCastPair(showId, castId);
                
                try {
                    // Check if tree is empty before trying to insert
                    boolean treeEmpty = false;
                    try {
                        treeEmpty = relationshipTree.empty();
                        if (treeEmpty && attempts == 1) {
                            // If empty on first attempt, try to rebuild index
                            rebuildIndex();
                        }
                    } catch (Exception e) {
                        System.err.println("Error checking if tree is empty: " + e.getMessage());
                    }

                        // Try to insert element
                        result = relationshipTree.create(pair);
                        System.out.println("Attempt " + attempts + ": " + (result ? "Success" : "Failure"));

                    
                    // Verify insertion actually worked by doing direct read
                    ArrayList<ShowCastPair> testRead = relationshipTree.read(pair);
                    boolean found = false;
                    for (ShowCastPair p2 : testRead) {
                        if (p2.getIdSerie() == pair.getIdSerie() && p2.getIdAtor() == pair.getIdAtor()) {
                            found = true;
                            break;
                        }
                    }
                    System.out.println("Insertion verification: " + (found ? "Pair found after insertion" : "Pair NOT found after insertion!"));
                    
                    // If not found even after successful insertion, something is wrong with persistence
                    if (result && !found) {
                        System.out.println("ALERT: B+ tree returned success on insertion, but element was not found after.");
                        System.out.println("This may indicate a persistence or tree corruption problem.");
                        
                        // Force insertion again just as test
                        System.out.println("Trying forced insertion for diagnosis...");
                        relationshipTree.create(new ShowCastPair(showId, castId));
                        
                        // Check if now element was inserted
                        testRead = relationshipTree.read(pair);
                        found = false;
                        for (ShowCastPair p2 : testRead) {
                            if (p2.getIdSerie() == pair.getIdSerie() && p2.getIdAtor() == pair.getIdAtor()) {
                                found = true;
                                break;
                            }
                        }
                        System.out.println("After forced insertion: " + (found ? "SUCCESS" : "PERSISTENT FAILURE"));
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error on attempt " + attempts + ": " + e.getMessage());
                    if (attempts == 3) {
                        e.printStackTrace();
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("ERROR adding relationship: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove relationship between show and cast member
    public boolean removeRelationship(int showId, int castId) throws Exception {
        ShowCastPair pair = new ShowCastPair(showId, castId);
        return relationshipTree.delete(pair);
    }
    
    // Check if show has cast members
    public boolean showHasCast(int showId) throws Exception {
        boolean result = false;
        
        try {
            // Attempt 1: Use B+ tree
            System.out.println("Checking if show ID=" + showId + " has cast via B+ tree");
            ShowCastPair searchPair = new ShowCastPair(showId, -1);
            ArrayList<ShowCastPair> pairs = relationshipTree.read(searchPair);
            
            System.out.println("B+ tree returned " + pairs.size() + " pairs for show ID=" + showId);
            
            // Check if any pair with showId was found
            for (ShowCastPair pair : pairs) {
                if (pair.getIdSerie() == showId) {
                    result = true;
                    break;
                }
            }
            
            // If no cast found by tree, try alternative method
            if (!result) {
                System.out.println("No cast found by B+ tree, trying direct search...");
                result = verifyCastByDirectSearch(showId);
            }
            
        } catch (Exception e) {
            System.err.println("Error checking cast for show " + showId + ": " + e.getMessage());
            // In case of error, use alternative method
            return verifyCastByDirectSearch(showId);
        }
        
        return result;
    }
    
    // Alternative method to check cast by direct search
    private boolean verifyCastByDirectSearch(int showId) throws Exception {
        int lastId = castFileManager.ultimoId();
        for (int i = 1; i <= lastId; i++) {
            Cast cast = castFileManager.read(i);
            if (cast != null && cast.getIDSerie() == showId) {
                return true;
            }
        }
        return false;
    }
    
    // Get all shows of a cast member
    public ArrayList<TvShow> getCastShows(int castId) throws Exception{
        ArrayList<TvShow> shows = new ArrayList<>();
        
        try {
            // Go through all shows and check which ones have this cast member
            int lastId = showFileManager.ultimoId();
            for (int i = 1; i <= lastId; i++) {
                TvShow show = showFileManager.read(i);
                if (show != null && show.getId() == castId) {
                    shows.add(show);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching shows for cast " + castId + ": " + e.getMessage());
        }

        return shows;
    }

    // Get all cast members of a show
    public ArrayList<Cast> getShowCast(int showId) throws Exception {
        ArrayList<Cast> cast = new ArrayList<>();
        
        try {
            // Main approach: Use B+ tree to search cast
            ShowCastPair searchPair = new ShowCastPair(showId, -1);
            ArrayList<ShowCastPair> pairs = relationshipTree.read(searchPair);
                        
            // For each pair found, retrieve cast member
            for (ShowCastPair pair : pairs) {
                if (pair.getIdSerie() == showId) { // Check if show ID matches
                    Cast castMember = castFileManager.read(pair.getIdAtor());
                    if (castMember != null) {
                        cast.add(castMember);
                    }
                    
                    // Try to fix B+ tree by adding missing relationship
                    addRelationship(showId, castMember.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR searching for cast: " + e.getMessage());
            
            // In case of error, try alternative method
            int lastId = castFileManager.ultimoId();
            for (int i = 1; i <= lastId; i++) {
                Cast castMember = castFileManager.read(i);
                if (castMember != null && castMember.getIDSerie() == showId) {
                    cast.add(castMember);
                }
            }
        }
        
        return cast;
    }
    
    // Update indices after cast creation, update or removal
    public void updateIndicesAfterOperation(Cast cast, String operation) throws Exception {
        int showId = cast.getIDSerie();
        int castId = cast.getId();
        
        switch (operation.toLowerCase()) {
            case "create":
            case "update":
                boolean result = addRelationship(showId, castId);
                if (!result) {
                    System.err.println("WARNING: Failed to add relationship between show " + showId + 
                                     " and cast " + castId + " to B+ tree");
                }
                break;
            case "delete":
                removeRelationship(showId, castId);
                break;
        }
    }
    
    // Get total number of cast members in a show
    public int getTotalCast(int showId) throws Exception {
        return getShowCast(showId).size();
    }
    
    
    // Search show by name (partial or complete)
    public ArrayList<TvShow> searchShowByName(String partialName) throws Exception {
        ArrayList<TvShow> foundShows = new ArrayList<>();
        int lastId = showFileManager.ultimoId();
        
        // Convert to lowercase for case-insensitive search
        String searchTerm = partialName.toLowerCase();
        
        for (int i = 1; i <= lastId; i++) {
            TvShow show = showFileManager.read(i);
            if (show != null && show.getNome().toLowerCase().contains(searchTerm)) {
                foundShows.add(show);
            }
        }
        
        return foundShows;
    }
    
    // Search cast by name (partial or complete)
    public ArrayList<Cast> searchCastByName(String partialName) throws Exception {
        ArrayList<Cast> foundCast = new ArrayList<>();
        int lastId = castFileManager.ultimoId();
        
        // Convert to lowercase for case-insensitive search
        String searchTerm = partialName.toLowerCase();
        
        for (int i = 1; i <= lastId; i++) {
            Cast cast = castFileManager.read(i);
            if (cast != null && cast.getNome().toLowerCase().contains(searchTerm)) {
                foundCast.add(cast);
            }
        }
        
        return foundCast;
    }
    
    // Search cast by name in specific show
    public ArrayList<Cast> searchCastByNameInShow(String partialName, int showId) throws Exception {
        ArrayList<Cast> foundCast = new ArrayList<>();
        ArrayList<Cast> showCast = getShowCast(showId);
        
        // Convert to lowercase for case-insensitive search
        String searchTerm = partialName.toLowerCase();
        
        for (Cast cast : showCast) {
            if (cast.getNome().toLowerCase().contains(searchTerm)) {
                foundCast.add(cast);
            }
        }
        
        return foundCast;
    }
}
