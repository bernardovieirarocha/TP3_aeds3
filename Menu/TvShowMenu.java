package Menu;

import java.util.ArrayList;
import java.util.Scanner;

import Model.Cast;
import Model.Chapter;
import Model.TvShow;
import Service.FileManager;
import Service.IndiceInvertido;
import Service.ShowCastManager;
import Service.ShowChapterManager;
import View.CastView;
import View.TvShowView;

public class TvShowMenu {
    public Scanner sc = new Scanner(System.in);
    FileManager<TvShow> showFileManager;
    FileManager<Chapter> chapterFileManager;
    FileManager<Cast> castFileManager;
    TvShowView showView;
    CastView castView;
    ShowChapterManager showChapterRelationship;
    ShowCastManager showCastRelationship;
    IndiceInvertido indice;

    public TvShowMenu(Scanner sc, FileManager<TvShow> showFileManager, FileManager<Chapter> chapterFileManager, FileManager<Cast> castFileManager) throws Exception {
        this.sc = sc;
        this.showFileManager = showFileManager;
        this.chapterFileManager = chapterFileManager;
        this.castFileManager = castFileManager;
        this.showView = new TvShowView(sc);
        this.castView = new CastView(sc);
        this.showChapterRelationship = new ShowChapterManager(showFileManager, chapterFileManager);
        this.showCastRelationship = new ShowCastManager(showFileManager, castFileManager);
        this.indice = new IndiceInvertido();
    }

    public void displayTvShowMenu() throws Exception {
        int option;
        do {
            System.out.println("\n\nStreamFlix 2.0");
            System.out.println("---------------");
            System.out.println("> Home > TV Shows");
            System.out.println("\n1 - Search");
            System.out.println("2 - Add");
            System.out.println("3 - Update");
            System.out.println("4 - Delete");
            System.out.println("5 - View show cast");
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
                case 1 -> searchShowByTitle();
                case 2 -> addTvShow();
                case 3 -> updateShowById();
                case 4 -> deleteShowByTitle();
                case 5 -> viewShowCast();
                case 6 -> linkCastToShow();
                case 7 -> unlinkCastFromShow();
                case 0 -> {}
                default -> System.out.println("Invalid option!");
            }

        } while (option != 0);
    }

    public void searchShowByTitle() throws Exception {
        String searchTerm = showView.readShowTitle();

        // TODO: Update index to work with TvShow directly
        // ElementoLista[] elements = indice.buscarSeriesPorIndice(searchTerm);

        ArrayList<TvShow> shows = new ArrayList<>();

        // Temporary: Show all shows since index is disabled
        int maxId = showFileManager.ultimoId();
        for (int i = 1; i <= maxId; i++) {
            TvShow show = showFileManager.read(i);
            if (show != null && show.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
                shows.add(show);
            }
        }

        showView.displaySearchResults(shows);
    }

    public void addTvShow() throws Exception {
        TvShow show = showView.createTvShow();
        if (show == null) {
            System.out.println("Could not create TV show!");
            return;
        }
        int id = showFileManager.create(show);
        show.setShowId(id);
        // TODO: Update index to work with TvShow directly
        // indice.inserirSerie(show);
        System.out.println("TV show created successfully! ID: " + id);
    }

    public void updateShowById() throws Exception {
        searchShowByTitle();
        System.out.print("\nEnter the ID of the show you want to update (0 to cancel): ");
        int id = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(id);
        if (show == null) {
            System.out.println("TV show not found.");
            return;
        }

        TvShow updatedShow = showView.updateTvShow(show);
        if (updatedShow == null) {
            System.out.println("Canceling changes.");
            return;
        }

        updatedShow.setShowId(id);
        boolean result = showFileManager.update(updatedShow);
        if (result) {
            // TODO: Update index to work with TvShow directly
            // indice.atualizarSerie(show.getTitle(), updatedShow);
            System.out.println("TV show updated successfully!");
        } else {
            System.out.println("System error while updating TV show.");
        }
    }

    public void deleteShowByTitle() throws Exception {
        searchShowByTitle();
        System.out.print("\nEnter the ID of the show you want to delete (0 to cancel): ");
        int id = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(id);
        if (show == null) {
            System.out.println("TV show not found.");
            return;
        }

        deleteShow(id, show);
    }

    public void deleteShow(int id, TvShow show) throws Exception {
        if (showChapterRelationship.showHasChapters(id)) {
            System.out.println("Cannot delete! There are chapters linked to this show.");
            int chapterCount = showChapterRelationship.getTotalChapters(id);
            System.out.println("Total linked chapters: " + chapterCount);
            return;
        }

        System.out.print("\nConfirm deletion? (Y/N) ");
        char response = sc.next().charAt(0);
        sc.nextLine();

        if (response == 'Y' || response == 'y' || response == 'S' || response == 's') {
            boolean result = showFileManager.delete(id);
            if (result) {
                indice.excluirSerie(show.getTitle(), id);
                System.out.println("TV show deleted successfully!");
            } else {
                System.out.println("Error deleting TV show.");
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    public void viewShowCast() throws Exception {
        searchShowByTitle();
        System.out.print("\nEnter the ID of the show to view cast (0 to cancel): ");
        int selectedId = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(selectedId);
        if (show == null) {
            System.out.println("TV show not found.");
            return;
        }

        ArrayList<Cast> castList = showCastRelationship.getShowCast(selectedId);
        castView.displaySearchResults(castList);
    }

    public void linkCastToShow() throws Exception {
        searchShowByTitle();
        System.out.print("\nEnter the ID of the show to link cast (0 to cancel): ");
        int selectedShowId = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(selectedShowId);
        if (show == null) {
            System.out.println("TV show not found!");
            return;
        }

        String searchTerm = castView.readActorName();
        ArrayList<Cast> castList = showCastRelationship.searchCastByName(searchTerm);

        castView.displaySearchResults(castList);
        if (castList.isEmpty()) return;

        System.out.print("\nEnter the ID of the cast member to link to the show (0 to cancel): ");
        int selectedCastId = sc.nextInt();
        sc.nextLine();

        Cast cast = castFileManager.read(selectedCastId);
        if (cast == null) {
            System.out.println("Cast member not found!");
            return;
        }

        showChapterRelationship.removeRelationship(cast.getAssociatedShowId(), cast.getCastId());
        showView.linkShowToCast(selectedCastId, selectedShowId);
        cast.setAssociatedShowId(show.getShowId());

        boolean result = castFileManager.update(cast);
        if (result) {
            showCastRelationship.updateIndicesAfterOperation(cast, "update");
            System.out.println("Cast member successfully linked to TV show!");
        } else {
            System.out.println("Error linking cast member to TV show!");
        }
    }

    public void unlinkCastFromShow() throws Exception {
        searchShowByTitle();
        System.out.print("\nEnter the ID of the show to unlink cast (0 to cancel): ");
        int selectedShowId = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(selectedShowId);
        if (show == null) {
            System.out.println("TV show not found!");
            return;
        }

        String searchTerm = castView.readActorName();
        ArrayList<Cast> castList = showCastRelationship.searchCastByName(searchTerm);

        castView.displaySearchResults(castList);
        if (castList.isEmpty()) return;

        System.out.print("\nEnter the ID of the cast member to unlink from the show (0 to cancel): ");
        int selectedCastId = sc.nextInt();
        sc.nextLine();

        Cast cast = castFileManager.read(selectedCastId);
        if (cast == null) {
            System.out.println("Cast member not found!");
            return;
        }

        if (cast.getAssociatedShowId() != show.getShowId()) {
            System.out.println("The cast member is not linked to this show!");
            return;
        }

        showChapterRelationship.removeRelationship(cast.getAssociatedShowId(), cast.getCastId());
        show.setAssociatedCastId(0);

        castView.unlinkCastFromShow(cast.getCastId(), show.getShowId());
        boolean result = showFileManager.update(show);

        if (result) {
            showCastRelationship.updateIndicesAfterOperation(cast, "update");
            System.out.println("Cast member successfully unlinked from TV show!");
        } else {
            System.out.println("Error unlinking cast member from TV show!");
        }
    }

}
