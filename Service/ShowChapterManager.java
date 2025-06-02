package Service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import Model.Chapter;
import Model.TvShow;

public class ShowChapterManager {
    private FileManager<Chapter> chapterFileManager;
    private FileManager<TvShow> showFileManager;
    public ArvoreBMais<ShowChapterPair> relationshipTree; // Made public for diagnostic access

    public void testTreeInsertion() {
        try {
            System.out.println("=== Tree Insertion Test ===");
            ShowChapterPair testPair = new ShowChapterPair(999, 999);
            boolean result = relationshipTree.create(testPair);
            System.out.println("Test insertion result: " + (result ? "SUCCESS" : "FAILURE"));
            
            // Try to read it back
            ArrayList<ShowChapterPair> readResult = relationshipTree.read(testPair);
            System.out.println("Test read result: " + readResult.size() + " pairs found");
            
            // Clean up test data
            relationshipTree.delete(testPair);
            System.out.println("=== End Tree Test ===");
        } catch (Exception e) {
            System.err.println("Error in tree test: " + e.getMessage());
        }
    }

    public ShowChapterManager(FileManager<TvShow> showFileManager, FileManager<Chapter> chapterFileManager) {
        this.showFileManager = showFileManager;
        this.chapterFileManager = chapterFileManager;
        
        try {
            // Define data path
            String dataPath = "dados";
            String indexFile = dataPath + "/showChapter.db";
            
            // Check if index file exists
            java.io.File idxFile = new java.io.File(indexFile);
            boolean newFile = !idxFile.exists();

            // Initialize B+ tree for relationships
            Constructor<ShowChapterPair> constructor = ShowChapterPair.class.getConstructor();
            this.relationshipTree = new ArvoreBMais<>(
                    constructor,
                    4, // Tree order
                    indexFile
            );

            // If file is new, initialize with dummy record
            if (newFile) {
                try {
                    System.out.println("Initializing new index file with dummy record...");
                    ShowChapterPair dummyPair = new ShowChapterPair(0, 0);
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
            System.err.println("ERROR initializing ShowChapterManager: " + e.getMessage());
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

            // Go through all chapters and rebuild tree
            int lastChapterId = chapterFileManager.ultimoId();
            for (int i = 1; i <= lastChapterId; i++) {
                Chapter ep = chapterFileManager.read(i);
                if (ep != null && ep.getIdSerie() > 0) {
                    try {
                        ShowChapterPair pair = new ShowChapterPair(ep.getIdSerie(), ep.getId());
                        relationshipTree.create(pair);
                        totalRecords++;
                    } catch (Exception e) {
                        System.err.println("Error inserting chapter " + i + " in index: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Index rebuild failed: " + e.getMessage());
        }
    }
    
    // Add relationship between show and chapter
    public boolean addRelationship(int showId, int chapterId) throws Exception {
        // Check if show exists
        TvShow show = showFileManager.read(showId);
        if (show == null) {
            System.err.println("ERROR: Show ID " + showId + " not found when adding relationship");
            return false;
        }

        // Check if chapter exists
        Chapter chapter = chapterFileManager.read(chapterId);
        if (chapter == null) {
            System.err.println("ERROR: Chapter ID " + chapterId + " not found when adding relationship");
            return false;
        }

        // Update show ID in chapter if necessary
        if (chapter.getIdSerie() != showId) {
            chapter.setIdSerie(showId);
            chapterFileManager.update(chapter);
        }

        try {
            // First check if relationship already exists in tree
            ShowChapterPair searchPair = new ShowChapterPair(showId, chapterId);
            ArrayList<ShowChapterPair> pairs = relationshipTree.read(searchPair);
            boolean alreadyExists = false;

            // Check if exact pair already exists
            for (ShowChapterPair pair : pairs) {
                if (pair.getIdSerie() == showId && pair.getIdEpisodio() == chapterId) {
                    alreadyExists = true;
                    break;
                }
            }

            if (alreadyExists) {
                System.out.println("Relationship between show " + showId + " and chapter " + chapterId + " already exists");
                return true;
            }

            // Try to insert up to 3 times if it fails
            boolean result = false;
            for (int attempts = 1; attempts <= 3 && !result; attempts++) {
                System.out.println("Insertion attempt " + attempts + " for show " + showId + " and chapter " + chapterId);
                
                ShowChapterPair pair = new ShowChapterPair(showId, chapterId);
                
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
                    ArrayList<ShowChapterPair> testRead = relationshipTree.read(pair);
                    boolean found = false;
                    for (ShowChapterPair p2 : testRead) {
                        if (p2.getIdSerie() == pair.getIdSerie() && p2.getIdEpisodio() == pair.getIdEpisodio()) {
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
                        relationshipTree.create(new ShowChapterPair(showId, chapterId));
                        
                        // Check if now element was inserted
                        testRead = relationshipTree.read(pair);
                        found = false;
                        for (ShowChapterPair p2 : testRead) {
                            if (p2.getIdSerie() == pair.getIdSerie() && p2.getIdEpisodio() == pair.getIdEpisodio()) {
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
    
    // Remove relationship between show and chapter
    public boolean removeRelationship(int showId, int chapterId) throws Exception {
        ShowChapterPair pair = new ShowChapterPair(showId, chapterId);
        return relationshipTree.delete(pair);
    }
    
    // Check if show has chapters
    public boolean showHasChapters(int showId) throws Exception {
        boolean result = false;
        
        try {
            // Attempt 1: Use B+ tree
            System.out.println("Checking if show ID=" + showId + " has chapters via B+ tree");
            ShowChapterPair searchPair = new ShowChapterPair(showId, -1);
            ArrayList<ShowChapterPair> pairs = relationshipTree.read(searchPair);
            
            System.out.println("B+ tree returned " + pairs.size() + " pairs for show ID=" + showId);
            
            // Check if any pair with showId was found
            for (ShowChapterPair pair : pairs) {
                if (pair.getIdSerie() == showId) {
                    result = true;
                    break;
                }
            }
            
            // If no chapters found by tree, try alternative method
            if (!result) {
                System.out.println("No chapters found by B+ tree, trying direct search...");
                result = verifyChaptersByDirectSearch(showId);
            }
            
        } catch (Exception e) {
            System.err.println("Error checking chapters for show " + showId + ": " + e.getMessage());
            // In case of error, use alternative method
            return verifyChaptersByDirectSearch(showId);
        }
        
        return result;
    }
    
    // Alternative method to check chapters by direct search
    private boolean verifyChaptersByDirectSearch(int showId) throws Exception {
        int lastId = chapterFileManager.ultimoId();
        for (int i = 1; i <= lastId; i++) {
            Chapter chapter = chapterFileManager.read(i);
            if (chapter != null && chapter.getIdSerie() == showId) {
                return true;
            }
        }
        return false;
    }
    
    // Get all chapters of a show
    public ArrayList<Chapter> getShowChapters(int showId) throws Exception {
        ArrayList<Chapter> chapters = new ArrayList<>();
        
        try {
            // Main approach: Use B+ tree to search chapters
            ShowChapterPair searchPair = new ShowChapterPair(showId, -1);
            ArrayList<ShowChapterPair> pairs = relationshipTree.read(searchPair);
                        
            // For each pair found, retrieve chapter
            for (ShowChapterPair pair : pairs) {
                if (pair.getIdSerie() == showId) { // Check if show ID matches
                    Chapter ep = chapterFileManager.read(pair.getIdEpisodio());
                    if (ep != null) {
                        chapters.add(ep);
                    }
                } else {
                    // Since B+ tree returns all elements greater or equal,
                    // we can stop when showId is different
                    break;
                }
            }
            
            // If no chapters found by tree, try alternative method
            if (chapters.isEmpty()) {
                
                // Linear search: check all chapters directly in file
                int lastId = chapterFileManager.ultimoId();
                for (int i = 1; i <= lastId; i++) {
                    Chapter ep = chapterFileManager.read(i);
                    if (ep != null && ep.getIdSerie() == showId) {
                        chapters.add(ep);
                        
                        // Try to fix B+ tree by adding missing relationship
                        addRelationship(showId, ep.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR searching for chapters: " + e.getMessage());
            
            // In case of error, try alternative method
            int lastId = chapterFileManager.ultimoId();
            for (int i = 1; i <= lastId; i++) {
                Chapter ep = chapterFileManager.read(i);
                if (ep != null && ep.getIdSerie() == showId) {
                    chapters.add(ep);
                }
            }
        }
        
        return chapters;
    }
    
    // Get chapters of specific season
    public ArrayList<Chapter> getChaptersBySeason(int showId, int season) throws Exception {
        ArrayList<Chapter> allChapters = getShowChapters(showId);
        ArrayList<Chapter> seasonChapters = new ArrayList<>();
        
        for (Chapter ep : allChapters) {
            if (ep.getTemporada() == season) {
                seasonChapters.add(ep);
            }
        }
        
        return seasonChapters;
    }
    
    // Count number of chapters per season of a show
    public HashMap<Integer, Integer> countChaptersBySeason(int showId) throws Exception {
        HashMap<Integer, Integer> count = new HashMap<>();
        ArrayList<Chapter> chapters = getShowChapters(showId);
        
        for (Chapter ep : chapters) {
            int season = ep.getTemporada();
            count.put(season, count.getOrDefault(season, 0) + 1);
        }
        
        return count;
    }
    
    // Organize chapters by season
    public HashMap<Integer, ArrayList<Chapter>> organizeChaptersBySeason(int showId) throws Exception {
        HashMap<Integer, ArrayList<Chapter>> result = new HashMap<>();
        ArrayList<Chapter> chapters = getShowChapters(showId);
        
        for (Chapter ep : chapters) {
            int season = ep.getTemporada();
            if (!result.containsKey(season)) {
                result.put(season, new ArrayList<>());
            }
            result.get(season).add(ep);
        }
        
        return result;
    }
    
    // Update indices after chapter creation, update or removal
    public void updateIndicesAfterOperation(Chapter chapter, String operation) throws Exception {
        int showId = chapter.getIdSerie();
        int chapterId = chapter.getId();
        
        switch (operation.toLowerCase()) {
            case "create":
            case "update":
                boolean result = addRelationship(showId, chapterId);
                if (!result) {
                    System.err.println("WARNING: Failed to add relationship between show " + showId + 
                                     " and chapter " + chapterId + " to B+ tree");
                }
                break;
            case "delete":
                removeRelationship(showId, chapterId);
                break;
        }
    }
    
    // Get total number of chapters in a show
    public int getTotalChapters(int showId) throws Exception {
        return getShowChapters(showId).size();
    }
    
    // Get total number of seasons in a show
    public int getTotalSeasons(int showId) throws Exception {
        HashMap<Integer, Integer> chaptersBySeason = countChaptersBySeason(showId);
        return chaptersBySeason.size();
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
    
    // Search chapter by name (partial or complete)
    public ArrayList<Chapter> searchChapterByName(String partialName) throws Exception {
        ArrayList<Chapter> foundChapters = new ArrayList<>();
        int lastId = chapterFileManager.ultimoId();
        
        // Convert to lowercase for case-insensitive search
        String searchTerm = partialName.toLowerCase();
        
        for (int i = 1; i <= lastId; i++) {
            Chapter chapter = chapterFileManager.read(i);
            if (chapter != null && chapter.getNome().toLowerCase().contains(searchTerm)) {
                foundChapters.add(chapter);
            }
        }
        
        return foundChapters;
    }
    
    // Search chapters by name in specific show
    public ArrayList<Chapter> searchChapterByNameInShow(String partialName, int showId) throws Exception {
        ArrayList<Chapter> foundChapters = new ArrayList<>();
        ArrayList<Chapter> showChapters = getShowChapters(showId);
        
        // Convert to lowercase for case-insensitive search
        String searchTerm = partialName.toLowerCase();
        
        for (Chapter chapter : showChapters) {
            if (chapter.getNome().toLowerCase().contains(searchTerm)) {
                foundChapters.add(chapter);
            }
        }
        
        return foundChapters;
    }
}
