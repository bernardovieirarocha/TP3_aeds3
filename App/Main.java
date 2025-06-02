package App;

import java.util.Scanner;

import Menu.CastMenu;
import Menu.ChapterMenu;
import Menu.TvShowMenu;
import Model.Cast;
import Model.Chapter;
import Model.TvShow;
import Service.FileManager;
import Service.IndiceInvertido;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static FileManager<Chapter> chapterFileManager;
    static FileManager<TvShow> showFileManager;
    static FileManager<Cast> castFileManager;

    public static void main(String[] args) throws Exception {
        Scanner sc;

        try {
            sc = new Scanner(System.in);
            int option;

            // Initialize file managers with new refactored classes
            chapterFileManager = new FileManager<>("Chapters", Chapter.class.getConstructor());
            showFileManager = new FileManager<>("TvShows", TvShow.class.getConstructor());
            castFileManager = new FileManager<>("Cast", Cast.class.getConstructor());

            // Initialize controllers
            IndiceInvertido indice = new IndiceInvertido();

            TvShowMenu tvShowMenu = new TvShowMenu(sc, showFileManager, chapterFileManager, castFileManager);
            ChapterMenu chapterMenu = new ChapterMenu(sc, chapterFileManager, showFileManager);
            CastMenu castMenu = new CastMenu(sc, castFileManager, showFileManager, indice);

            do {

                System.out.println("\nPUCFLIX 3.0");
                System.out.println("---------------");
                System.out.println("> Home");
                System.out.println("1 - TV Shows");
                System.out.println("2 - Chapters");
                System.out.println("3 - Cast");
                System.out.println("0 - Exit");
                System.out.print("\nOption: ");
                try {
                    option = Integer.valueOf(sc.nextLine());
                } catch (NumberFormatException e) {
                    option = -1;
                }

                switch (option) {
                    case 1:
                        tvShowMenu.displayTvShowMenu();
                        break;
                    case 2:
                        chapterMenu.displayChapterMenu();
                        break;
                    case 3:
                        castMenu.displayCastMenu();
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }

            } while (option != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
