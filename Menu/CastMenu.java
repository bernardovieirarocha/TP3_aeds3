package Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import Model.Cast;
import Model.ElementoLista;
import Model.TvShow;
import Service.FileManager;
import Service.IndiceInvertido;
import Service.ShowCastManager;
import View.CastView;
import View.TvShowView;

public class CastMenu {
    public Scanner sc = new Scanner(System.in);
    FileManager<Cast> castFileManager;
    FileManager<TvShow> showFileManager;
    CastView castView;
    TvShowView showView;
    ShowCastManager showCastRelationship;
    IndiceInvertido indice;

    public CastMenu(Scanner sc, FileManager<Cast> castFileManager, FileManager<TvShow> showFileManager, IndiceInvertido indice) throws Exception {
        this.sc = sc;
        this.castFileManager = castFileManager;
        this.showFileManager = showFileManager;
        this.castView = new CastView(sc);
        this.showView = new TvShowView(sc);
        this.showCastRelationship = new ShowCastManager(showFileManager, castFileManager);
        this.indice = indice;
    }

    public void displayCastMenu() throws Exception {
        int option;
        do {
            System.out.println("\n\nStreamFlix 2.0");
            System.out.println("---------------");
            System.out.println("> Home > Cast");
            System.out.println("\n1 - Search cast member");
            System.out.println("2 - Add cast member");
            System.out.println("3 - Update cast member");
            System.out.println("4 - Delete cast member");
            System.out.println("5 - List cast members");
            System.out.println("6 - Link cast to show");
            System.out.println("7 - Unlink cast from show");
            System.out.println("0 - Back");

            System.out.print("\nOption: ");
            try {
                option = Integer.valueOf(sc.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1 -> searchCastByName();
                case 2 -> addCast();
                case 3 -> updateCast();
                case 4 -> deleteCast();
                case 5 -> listCastMembers();
                case 6 -> linkCastToShow();
                case 7 -> unlinkCastFromShow();
                case 0 -> {}
                default -> System.out.println("Invalid option!");
            }

        } while (option != 0);
    }

    public void searchCastByName() throws Exception {
        String searchTerm = castView.readActorName();
        ElementoLista[] elements = indice.buscarAtoresPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<Cast> castMembers = new ArrayList<>();

        for (ElementoLista el : elements) {
            Cast cast = castFileManager.read(el.getId());
            if (cast != null) {
                castMembers.add(cast);
            }
        }

        System.out.println("\n===== DEBUG: Inverted Index Results for Cast =====");
        for (ElementoLista el : elements) {
            System.out.println("ID: " + el.getId() + " | Weight: " + el.getFrequencia());
        }

        castView.displaySearchResults(castMembers);

        if (!castMembers.isEmpty()) {
            System.out.print("\nDo you want to select a cast member to see more details? (Y/N): ");
            String response = sc.nextLine().toUpperCase();

            if (response.equals("Y")) {
                int selectedId = castView.selectCastFromResults(castMembers);

                if (selectedId > 0) {
                    Cast selectedCast = castFileManager.read(selectedId);

                    ArrayList<TvShow> shows = showCastRelationship.getCastShows(selectedId);
                    castView.displayShowSearchResults(shows);

                    castView.displayCast(selectedCast);
                }
            }
        }
    }

    public void addCast() throws Exception {
        Cast cast = castView.createCast(0);
        if (cast == null) {
            System.out.println("Error adding cast member!");
            return;
        }
        int id = castFileManager.create(cast);
        cast.setId(id);
        indice.inserirAtor(convertToOldFormat(cast));
        System.out.println("Cast member added successfully! ID: " + id);
    }

    public void updateCast() throws Exception {
        String searchTerm = castView.readActorName();
        ElementoLista[] elements = indice.buscarAtoresPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<Cast> castMembers = new ArrayList<>();

        for (ElementoLista el : elements) {
            Cast cast = castFileManager.read(el.getId());
            if (cast != null) {
                castMembers.add(cast);
            }
        }

        castView.displaySearchResults(castMembers);
        if (castMembers.isEmpty()) return;

        System.out.print("\nEnter the ID of the cast member you want to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        Cast currentCast = castFileManager.read(id);
        if (currentCast == null) {
            System.out.println("Cast member not found!");
            return;
        }

        Cast updatedCast = castView.updateCast(0, currentCast);
        if (updatedCast == null) {
            System.out.println("Operation canceled.");
            return;
        }

        updatedCast.setId(id);
        boolean result = castFileManager.update(updatedCast);

        if (result) {
            indice.atualizarAtor(currentCast.getNome(), convertToOldFormat(updatedCast));
            System.out.println("Cast member updated successfully!");
        } else {
            System.out.println("Error updating cast member!");
        }
    }

    public void deleteCast() throws Exception {
        String searchTerm = castView.readActorName();
        ElementoLista[] elements = indice.buscarAtoresPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<Cast> castMembers = new ArrayList<>();

        for (ElementoLista el : elements) {
            Cast cast = castFileManager.read(el.getId());
            if (cast != null) {
                castMembers.add(cast);
            }
        }

        castView.displaySearchResults(castMembers);
        if (castMembers.isEmpty()) return;

        System.out.print("\nEnter the ID of the cast member you want to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        Cast cast = castFileManager.read(id);
        if (cast == null) {
            System.out.println("Cast member not found!");
            return;
        }

        System.out.print("\nConfirm deletion? (Y/N): ");
        String resp = sc.nextLine().toUpperCase();
        if (!resp.equals("Y")) {
            System.out.println("Operation canceled.");
            return;
        }

        boolean result = castFileManager.delete(id);
        if (result) {
            indice.excluirAtor(cast.getNome(), id);
            System.out.println("Cast member deleted successfully!");
        } else {
            System.out.println("Error deleting cast member.");
        }
    }

    public void listCastMembers() throws Exception {
        System.out.println("\nRegistered cast members:");
        int lastId = castFileManager.ultimoId();
        boolean hasCast = false;

        for (int i = 1; i <= lastId; i++) {
            Cast cast = castFileManager.read(i);
            if (cast != null) {
                System.out.println("ID: " + cast.getId() + " | Name: " + cast.getNome());
                hasCast = true;
            }
        }

        if (!hasCast) {
            System.out.println("No cast members registered.");
        }
    }

    public void linkCastToShow() throws Exception {
        System.out.print("Enter the cast member ID to link: ");
        int castId = sc.nextInt();
        sc.nextLine();

        Cast cast = castFileManager.read(castId);
        if (cast == null) {
            System.out.println("Cast member not found!");
            return;
        }

        System.out.print("Enter the show ID to link: ");
        int showId = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(showId);
        if (show == null) {
            System.out.println("TV show not found!");
            return;
        }

        cast.setIDSerie(show.getId());
        boolean result = castFileManager.update(cast);

        if (result) {
            System.out.println("Cast member linked to show successfully!");
        } else {
            System.out.println("Error linking cast member!");
        }
    }

    public void unlinkCastFromShow() throws Exception {
        System.out.print("Enter the cast member ID to unlink: ");
        int castId = sc.nextInt();
        sc.nextLine();

        Cast cast = castFileManager.read(castId);
        if (cast == null) {
            System.out.println("Cast member not found!");
            return;
        }

        if (cast.getIDSerie() == 0) {
            System.out.println("Cast member is not linked to any show.");
            return;
        }

        cast.setIDSerie(0);
        boolean result = castFileManager.update(cast);

        if (result) {
            System.out.println("Cast member unlinked from show successfully!");
        } else {
            System.out.println("Error unlinking cast member!");
        }
    }

    // Helper method to create an Ator object from Cast for index compatibility
    private Model.Ator convertToOldFormat(Cast cast) {
        // This is a temporary conversion method to maintain compatibility with the existing index
        // In a complete refactor, the index would also be updated to work with Cast directly
        Model.Ator tempAtor = new Model.Ator();
        tempAtor.setId(cast.getId());
        tempAtor.setNome(cast.getNome());
        tempAtor.setGenero(cast.getGenero());
        tempAtor.setIDSerie(cast.getIDSerie());
        return tempAtor;
    }
}
