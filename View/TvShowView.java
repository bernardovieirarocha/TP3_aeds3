package View;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import Model.TvShow;

public class TvShowView {
    public Scanner sc = new Scanner(System.in);

    public TvShowView(Scanner sc) {
        this.sc = sc;
    }

    public TvShow createTvShow() throws Exception {
        System.out.println("TV Show Creation: ");
        String title = "";
        LocalDate releaseYear = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String synopsis = "";
        int synopsisLength = 0;
        String platform = "";
        int seasonCount = 0;
        int castId = 0;
        boolean validData = false;

        System.out.println("Title:");
        title = sc.nextLine();

        do {
            validData = false;
            System.out.println("Enter release date in format (DD/MM/YYYY): ");
            String dateStr = sc.nextLine();
            try {
                releaseYear = LocalDate.parse(dateStr, formatter);
                validData = true;
            } catch (Exception e) {
                System.err.println("Invalid date! Use format DD/MM/YYYY.");
            }
        } while (!validData);

        do {
            validData = false;
            System.out.println("Enter synopsis: ");
            synopsis = sc.nextLine();
            if (!synopsis.isEmpty()) {
                validData = true;
                synopsisLength = synopsis.length();
            } else {
                System.err.println("Error creating synopsis.");
            }
        } while (!validData);

        do {
            System.out.println("Number of seasons:");
            seasonCount = sc.nextInt();
            if (seasonCount <= 0) {
                System.err.println("Season count must be a positive integer.");
            }
        } while (seasonCount <= 0);

        do {
            validData = false;
            System.out.println(
                    "Choose streaming platform: \n 1) Netflix \n 2) Amazon Prime Video \n 3) Max \n 4) Disney Plus \n 5) Globo Play \n 6) Star Plus");
            int platformChoice = 0;
            platformChoice = sc.nextInt();
            sc.nextLine();
            if (platformChoice == 1) {
                platform = "Netflix";
                validData = true;
            }
            if (platformChoice == 2) {
                platform = "Amazon Prime Video";
                validData = true;
            }
            if (platformChoice == 3) {
                platform = "Max";
                validData = true;
            }
            if (platformChoice == 4) {
                platform = "Disney Plus";
                validData = true;
            }
            if (platformChoice == 5) {
                platform = "Globo Play";
                validData = true;
            }
            if (platformChoice == 6) {
                platform = "Star Plus";
                validData = true;
            }
        } while (!validData);

        System.out.print("\nConfirm TV show creation? (Y/N) ");
        char response = sc.next().charAt(0);

        if (response == 'Y' || response == 'y' || response == 'S' || response == 's') {
            try {
                TvShow show = new TvShow(title, releaseYear, synopsis, synopsisLength, platform, seasonCount, castId);
                return show;
            } catch (Exception e) {
                System.out.println("System error. Could not create TV show!");
                return null;
            }
        } else {
            System.out.println("TV show creation canceled.");
            return null;
        }
    }

    public TvShow updateTvShow(TvShow show) {
        System.out.println("\nTV Show Update: ");

        // Update title
        System.out.print("\nNew title (leave blank to keep current): ");
        String newTitle = sc.nextLine();
        if (!newTitle.isEmpty()) {
            show.setTitle(newTitle);
        }

        // Update release year
        System.out.print("\nRelease date (DD/MM/YYYY) (leave blank to keep current): ");
        String newDateStr = sc.nextLine();
        if (!newDateStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate newDate = LocalDate.parse(newDateStr, formatter);
                show.setReleaseYear(newDate);
            } catch (Exception e) {
                System.err.println("Invalid date. Value maintained.");
            }
        }

        // Update synopsis
        System.out.println("\nEnter new synopsis (leave blank to keep current): ");
        String newSynopsis = sc.nextLine();
        if (!newSynopsis.isEmpty()) {
            try {
                show.setSynopsis(newSynopsis);
            } catch (Exception e) {
                System.err.println("Error updating synopsis! Synopsis maintained.");
            }
        }

        // Update season count
        System.out.print("\nNumber of seasons (leave blank to keep current): ");
        String seasonStr = sc.nextLine();
        if (!seasonStr.isEmpty()) {
            try {
                int newSeasonCount = Integer.parseInt(seasonStr);
                show.setSeasonCount(newSeasonCount);
            } catch (NumberFormatException e) {
                System.err.println("Invalid quantity! Value maintained.");
            }
        }

        // Update platform
        System.out.print("\nChoose platform (Choose option 0 to keep current platform): ");
        boolean validData = false;
        String currentPlatform = show.getPlatform();
        do {
            validData = false;
            System.out.println(
                    "Choose streaming platform: \n 0) Keep current \n 1) Netflix \n 2) Amazon Prime Video \n 3) Max \n 4) Disney Plus \n 5) Globo Play \n 6) Star Plus");
            int platformChoice = 0;
            platformChoice = sc.nextInt();
            sc.nextLine();
            if (platformChoice == 0) {
                validData = true;
            }
            if (platformChoice == 1) {
                currentPlatform = "Netflix";
                validData = true;
            }
            if (platformChoice == 2) {
                currentPlatform = "Amazon Prime Video";
                validData = true;
            }
            if (platformChoice == 3) {
                currentPlatform = "Max";
                validData = true;
            }
            if (platformChoice == 4) {
                currentPlatform = "Disney Plus";
                validData = true;
            }
            if (platformChoice == 5) {
                currentPlatform = "Globo Play";
                validData = true;
            }
            if (platformChoice == 6) {
                currentPlatform = "Star Plus";
                validData = true;
            }
            show.setPlatform(currentPlatform);
        } while (!validData);

        // Confirm changes
        System.out.print("\nConfirm changes? (Y/N) ");
        char response = sc.next().charAt(0);
        if (response == 'Y' || response == 'y' || response == 'S' || response == 's') {
            return show;
        } else {
            return null;
        }
    }

    public int readShowId(){
        System.out.println("Enter TV show ID:");
        int id = sc.nextInt();
        sc.nextLine(); // Clear buffer
        return id;
    }

    public String readShowTitle(){
        System.out.println("Enter desired TV show title: ");
        String title = sc.nextLine();
        return title;
    }

    public void displayTvShow(TvShow show) {
        if (show != null) {
            System.out.println("\nTV Show Details:");
            System.out.println("------------------------------------");
            System.out.printf("Show title.....................: %s%n", show.getTitle());
            System.out.printf("Number of seasons..............: %d%n", show.getSeasonCount());
            System.out.printf("Synopsis.......................: %s%n", show.getSynopsis());
            System.out.printf("Streaming platform.............: %s%n", show.getPlatform());
            System.out.printf("Release date...................: %s%n",
                    show.getReleaseYear().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("------------------------------------");
        }
    }

    public void displayFoundShows(ArrayList<TvShow> shows){
        if(shows.isEmpty()){
            System.out.println("No TV shows found!");
            return;
        }

        System.out.println("TV shows found: ");
        System.out.println("Number of shows found: " + shows.size());

        for(TvShow show : shows){
            System.out.println("ID: " + show.getShowId() + " | " + show.getTitle() + " | (" + show.getReleaseYear() + ") | Platform: " + show.getPlatform());
        }
    }

    public void displaySearchResults(ArrayList<TvShow> shows) {
        if (shows.isEmpty()) {
            System.out.println("\nNo TV shows found with the provided term.");
            return;
        }
        
        System.out.println("\n=== TV Shows Found ===");
        System.out.println("Total: " + shows.size() + " show(s)");
        
        for (TvShow show : shows) {
            System.out.println("\nID: " + show.getShowId() + " | " + show.getTitle() + " (" + show.getReleaseYear() + ")");
            System.out.println("Platform: " + show.getPlatform());
        }
    }
    
    public void linkShowToCast(int castId, int showId){
        System.out.println("Link Cast to TV Show: ");
        System.out.println("Cast ID: " + castId);
        System.out.println("Show ID: " + showId);

        // Confirm creation
        System.out.print("\nConfirm cast linking to show? (Y/N) ");
        String response = sc.nextLine().toUpperCase();
        if (response.isEmpty() || !(response.equals("Y") || response.equals("N") || response.equals("S"))) {
            System.out.println("Invalid response. Please enter 'Y' for Yes or 'N' for No.");
            return;
        }

        if(response.equals("Y") || response.equals("S")) {
            System.out.println("Cast successfully linked to TV show!");
        } else{
            System.out.println("Cast linking to TV show canceled.");
        }
    }

    public void unlinkShowFromCast(int castId, int showId){
        System.out.println("Unlink Cast from TV Show: ");
        System.out.println("Cast ID: " + castId);
        System.out.println("Show ID: " + showId);

        // Confirm unlinking
        System.out.print("\nConfirm cast unlinking from show? (Y/N) ");
        String response = sc.nextLine().toUpperCase();
        if (response.isEmpty() || !(response.equals("Y") || response.equals("N") || response.equals("S"))) {
            System.out.println("Invalid response. Please enter 'Y' for Yes or 'N' for No.");
            return;
        }

        if(response.equals("Y") || response.equals("S")) {
            System.out.println("Cast successfully unlinked from TV show!");
        } else{
            System.out.println("Cast unlinking from TV show canceled.");
        }
    }
}
