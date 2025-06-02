package View;

import java.util.ArrayList;
import java.util.Scanner;

import Model.Cast;
import Model.TvShow;

public class CastView {
    public Scanner sc = new Scanner(System.in);

    public CastView(Scanner sc) {
        this.sc = sc;
    }

    // -------------------------------
    // Link and Unlink Operations
    // -------------------------------
    public void linkCastToShow(int castId, int showId) {
        System.out.println("Link Cast to TV Show: ");
        System.out.println("Cast ID: " + castId);
        System.out.println("Show ID: " + showId);

        System.out.print("\nConfirm cast linking to show? (Y/N) ");
        String response = sc.nextLine().toUpperCase();
        if (response.equals("Y") || response.equals("S")) {
            System.out.println("Cast successfully linked to TV show!");
        } else {
            System.out.println("Cast linking to TV show canceled.");
        }
    }

    public void unlinkCastFromShow(int castId, int showId) {
        System.out.println("Unlink Cast from TV Show: ");
        System.out.println("Cast ID: " + castId);
        System.out.println("Show ID: " + showId);

        System.out.print("\nConfirm cast unlinking from show? (Y/N) ");
        String response = sc.nextLine().toUpperCase();
        if (response.equals("Y") || response.equals("S")) {
            System.out.println("Cast successfully unlinked from TV show!");
        } else {
            System.out.println("Cast unlinking from TV show canceled.");
        }
    }

    // -------------------------------
    // CRUD - Creation
    // -------------------------------
    public Cast createCast(int showId) {
        System.out.println("Cast Creation:");
        System.out.print("Actor Name: ");
        String actorName = sc.nextLine();

        char gender;
        do {
            System.out.print("Gender (M/F): ");
            gender = sc.nextLine().toUpperCase().charAt(0);
            if (gender != 'M' && gender != 'F') {
                System.out.println("Invalid gender. Enter 'M' or 'F'.");
            }
        } while (gender != 'M' && gender != 'F');

        System.out.println("\nConfirm Cast Data:");
        System.out.println("Actor Name: " + actorName);
        System.out.println("Show ID: " + showId);
        System.out.println("Gender: " + gender);

        System.out.print("Confirm creation? (Y/N): ");
        String response = sc.nextLine().toUpperCase();
        if (response.equals("Y") || response.equals("S")) {
            return new Cast(actorName, gender, showId);
        } else {
            System.out.println("Cast creation canceled.");
            return null;
        }
    }

    // -------------------------------
    // CRUD - Update
    // -------------------------------
    public Cast updateCast(int showId, Cast cast) {
        System.out.println("Cast Update:");
        displayCast(cast);

        System.out.print("New actor name (Enter to keep current): ");
        String newName = sc.nextLine();
        if (!newName.isEmpty()) {
            cast.setActorName(newName);
        }

        System.out.print("New gender (M/F or Enter to keep current): ");
        String genderInput = sc.nextLine().toUpperCase();
        if (!genderInput.isEmpty()) {
            char newGender = genderInput.charAt(0);
            if (newGender == 'M' || newGender == 'F') {
                cast.setGender(newGender);
            } else {
                System.out.println("Invalid gender. Keeping previous value.");
            }
        }

        System.out.println("\nConfirm new data:");
        displayCast(cast);

        System.out.print("Confirm changes? (Y/N): ");
        char response = sc.next().toUpperCase().charAt(0);
        sc.nextLine();
        if (response == 'Y' || response == 'S') {
            return cast;
        } else {
            System.out.println("Changes canceled.");
            return null;
        }
    }

    // -------------------------------
    // Data Input
    // -------------------------------
    public String readActorName() {
        System.out.print("Enter actor name: ");
        return sc.nextLine();
    }

    public int readCastId() {
        System.out.print("Enter cast ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        return id;
    }

    // -------------------------------
    // Display Results
    // -------------------------------
    public void displaySearchResults(ArrayList<Cast> castList) {
        if (castList.isEmpty()) {
            System.out.println("\nNo cast members found with the provided term.");
            return;
        }

        System.out.println("\n=== Cast Members Found ===");
        System.out.println("Total: " + castList.size() + " cast member(s).");

        for (Cast cast : castList) {
            System.out.println("\nID: " + cast.getCastId() + " | Name: " + cast.getActorName() + " | Gender: " + cast.getGender());
        }
    }

    public void displayShowSearchResults(ArrayList<TvShow> shows) {
        if (shows.isEmpty()) {
            System.out.println("No TV shows found.");
            return;
        }

        System.out.println("\n=== TV Shows Found ===");
        System.out.println("Total: " + shows.size() + " show(s).");

        for (TvShow show : shows) {
            System.out.println("\nID: " + show.getShowId() + " | Title: " + show.getTitle());
        }
    }

    public void displayCastList(ArrayList<Cast> castList) {
        if (castList.isEmpty()) {
            System.out.println("No cast members found.");
            return;
        }

        System.out.println("\n=== Cast List ===");
        for (Cast cast : castList) {
            System.out.println("ID: " + cast.getCastId() + " | Name: " + cast.getActorName() + " | Gender: " + cast.getGender());
        }
    }

    public void displayCast(Cast cast) {
        System.out.println("\n=== Cast Member Data ===");
        System.out.println("ID: " + cast.getCastId());
        System.out.println("Actor Name: " + cast.getActorName());
        System.out.println("Gender: " + cast.getGender());
    }

    // -------------------------------
    // ID Selection from Search Results
    // -------------------------------
    public int selectCastFromResults(ArrayList<Cast> castList) {
        if (castList.isEmpty()) {
            return -1;
        }

        if (castList.size() == 1) {
            System.out.println("\nCast member automatically selected: " + castList.get(0).getActorName());
            return castList.get(0).getCastId();
        }

        System.out.print("\nEnter the ID of the cast member you want to select (0 to cancel): ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean idExists = false;
        for (Cast cast : castList) {
            if (cast.getCastId() == id) {
                idExists = true;
                break;
            }
        }

        if (!idExists) {
            System.out.println("Invalid ID! Please select an ID from the displayed list.");
            return selectCastFromResults(castList);
        }

        return id;
    }
}
