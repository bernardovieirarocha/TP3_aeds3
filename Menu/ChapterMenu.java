package Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import Model.Chapter;
import Model.ElementoLista;
import Model.TvShow;
import Service.FileManager;
import Service.IndiceInvertido;
import Service.ShowChapterManager;
import View.ChapterView;
import View.TvShowView;

public class ChapterMenu {

    public Scanner sc = new Scanner(System.in);
    FileManager<Chapter> chapterFileManager;
    FileManager<TvShow> showFileManager;
    ChapterView chapterView;
    TvShowView showView;
    ShowChapterManager showChapterRelationship;
    IndiceInvertido indice;

    public ChapterMenu(Scanner sc, FileManager<Chapter> chapterFileManager, FileManager<TvShow> showFileManager) throws Exception {
        this.sc = sc;
        this.chapterFileManager = chapterFileManager;
        this.showFileManager = showFileManager;
        this.chapterView = new ChapterView(sc);
        this.showView = new TvShowView(sc);
        this.showChapterRelationship = new ShowChapterManager(showFileManager, chapterFileManager);
        this.indice = new IndiceInvertido();
    }

    public void displayChapterMenu() throws Exception {
        int option;
        do {
            System.out.println("\n\nStreamFlix 2.0");
            System.out.println("---------------");
            System.out.println("> Home > Chapters");
            System.out.println("\n1 - List TV shows");
            System.out.println("2 - Manage show chapters");
            System.out.println("3 - Search chapter");
            System.out.println("0 - Back");

            System.out.print("\nOption: ");
            try {
                option = Integer.valueOf(sc.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1 -> listShows();
                case 2 -> manageChaptersByShowTitle();
                case 3 -> searchChapterByTitle();
                case 0 -> {}
                default -> System.out.println("Invalid option!");
            }

        } while (option != 0);
    }

    public void searchChapterByTitle() throws Exception {
        String searchTerm = chapterView.readChapterTitle();

        ElementoLista[] elements = indice.buscarEpisodiosPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<Chapter> chapters = new ArrayList<>();

        for (ElementoLista el : elements) {
            Chapter chapter = chapterFileManager.read(el.getId());
            if (chapter != null) {
                chapters.add(chapter);
            }
        }

        chapterView.displaySearchResults(chapters);
    }

    public void listShows() throws Exception {
        System.out.println("\nAvailable TV shows:");
        int lastId = showFileManager.ultimoId();
        boolean hasShows = false;

        for (int i = 1; i <= lastId; i++) {
            TvShow show = showFileManager.read(i);
            if (show != null) {
                System.out.println("ID: " + show.getId() + " | Title: " + show.getTitle());
                hasShows = true;
            }
        }

        if (!hasShows) {
            System.out.println("No TV shows registered.");
        }
    }

    public void manageChaptersByShowTitle() throws Exception {
        String searchTerm = showView.readShowTitle();

        ElementoLista[] elements = indice.buscarSeriesPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<TvShow> shows = new ArrayList<>();

        for (ElementoLista el : elements) {
            TvShow show = showFileManager.read(el.getId());
            if (show != null) {
                shows.add(show);
            }
        }

        showView.displaySearchResults(shows);

        if (shows.isEmpty()) return;

        System.out.print("\nEnter the show ID to manage its chapters (0 to cancel): ");
        int selectedId = sc.nextInt();
        sc.nextLine();

        TvShow show = showFileManager.read(selectedId);
        if (show == null) {
            System.out.println("TV show not found.");
            return;
        }

        manageShowChapters(show.getId());
    }

    private void manageShowChapters(int showId) throws Exception {
        TvShow show = showFileManager.read(showId);
        if (show == null) {
            System.out.println("TV show not found.");
            return;
        }

        System.out.println("Managing chapters for show: " + show.getTitle());

        int option;
        do {
            System.out.println("\nStreamFlix 1.0\n-----------\n> Home > Chapters > " + show.getTitle());
            System.out.println("1) Add ");
            System.out.println("2) Search ");
            System.out.println("3) Update ");
            System.out.println("4) Delete ");
            System.out.println("5) List all chapters");
            System.out.println("0) Return to previous menu");
            System.out.print("Choose an option: ");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1 -> addChapter(showId);
                case 2 -> searchChapterInShowById(showId);
                case 3 -> updateChapterByTitle(showId);
                case 4 -> deleteChapterByTitle(showId);
                case 5 -> listShowChapters(showId);
                default -> {
                    if (option != 0) {
                        System.out.println("Invalid option!");
                    }
                }
            }
        } while (option != 0);
    }

    public void addChapter(int showId) throws Exception {
        Chapter chapter = chapterView.createChapter(showId);
        if (chapter == null) {
            System.out.println("Operation canceled.");
            return;
        }

        int id = chapterFileManager.create(chapter);
        chapter.setId(id);
        indice.inserirEpisodio(convertToOldFormat(chapter));

        System.out.println("Chapter created successfully! ID: " + id);
    }

    public void updateChapterByTitle(int showId) throws Exception {
        String searchTerm = chapterView.readChapterTitle();

        ElementoLista[] elements = indice.buscarEpisodiosPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<Chapter> chapters = new ArrayList<>();

        for (ElementoLista el : elements) {
            Chapter chapter = chapterFileManager.read(el.getId());
            if (chapter != null && chapter.getAssociatedShowId() == showId) {
                chapters.add(chapter);
            }
        }

        chapterView.displaySearchResults(chapters);
        if (chapters.isEmpty()) return;

        System.out.print("\nEnter the ID of the chapter you want to update (0 to cancel): ");
        int selectedId = sc.nextInt();
        sc.nextLine();

        Chapter currentChapter = chapterFileManager.read(selectedId);
        if (currentChapter == null) {
            System.out.println("Chapter not found.");
            return;
        }

        Chapter updatedChapter = chapterView.updateChapter(showId, currentChapter);
        if (updatedChapter == null) {
            System.out.println("Canceled.");
            return;
        }

        indice.atualizarEpisodio(currentChapter.getTitle(), convertToOldFormat(updatedChapter));

        updatedChapter.setChapterId(selectedId);
        chapterFileManager.update(updatedChapter);

        System.out.println("Chapter updated successfully!");
    }

    public void deleteChapterByTitle(int showId) throws Exception {
        String searchTerm = chapterView.readChapterTitle();

        ElementoLista[] elements = indice.buscarEpisodiosPorIndice(searchTerm);

        Arrays.sort(elements, Comparator.comparing(ElementoLista::getFrequencia).reversed());

        ArrayList<Chapter> chapters = new ArrayList<>();

        for (ElementoLista el : elements) {
            Chapter chapter = chapterFileManager.read(el.getId());
            if (chapter != null && chapter.getAssociatedShowId() == showId) {
                chapters.add(chapter);
            }
        }

        chapterView.displaySearchResults(chapters);
        if (chapters.isEmpty()) return;

        System.out.print("\nEnter the ID of the chapter you want to delete (0 to cancel): ");
        int selectedId = sc.nextInt();
        sc.nextLine();

        Chapter chapter = chapterFileManager.read(selectedId);
        if (chapter == null) {
            System.out.println("Chapter not found.");
            return;
        }

        indice.excluirEpisodio(chapter.getTitle(), selectedId);
        chapterFileManager.delete(selectedId);

        System.out.println("Chapter deleted successfully!");
    }

    private void listShowChapters(int showId) throws Exception {
        ArrayList<Chapter> chapters = showChapterRelationship.getShowChapters(showId);

        if (chapters.isEmpty()) {
            System.out.println("\nNo chapters registered.");
            return;
        }

        System.out.println("\n===== Show Chapters =====");
        for (Chapter chapter : chapters) {
            System.out.println("ID: " + chapter.getChapterId() + " | Title: " + chapter.getTitle());
        }
    }

    public void searchChapterInShowById(int showId) throws Exception {
        int id = chapterView.readChapterId();
        Chapter chapter = chapterFileManager.read(id);
        if (chapter != null) {
            System.out.println("\nChapter: " + chapter.getTitle());
        } else {
            System.out.println("\nChapter not found.");
        }
    }

    // Helper method to create an Episodio object from Chapter for index compatibility
    private Model.Episodio convertToOldFormat(Chapter chapter) {
        // This is a temporary conversion method to maintain compatibility with the existing index
        // In a complete refactor, the index would also be updated to work with Chapter directly
        Model.Episodio tempEpisodio = new Model.Episodio();
        tempEpisodio.setId(chapter.getChapterId());
        tempEpisodio.setNome(chapter.getTitle());
        tempEpisodio.setTemporada(chapter.getSeasonNumber());
        tempEpisodio.setDataLancamento(chapter.getReleaseDate());
        tempEpisodio.setDuracao(chapter.getDuration());
        tempEpisodio.setIdSerie(chapter.getAssociatedShowId());
        tempEpisodio.setNumeroEpisodio(chapter.getChapterNumber());
        return tempEpisodio;
    }
}
