package View;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import Model.Chapter;

public class ChapterView {
    public Scanner sc = new Scanner(System.in);

    public ChapterView(Scanner sc) {
        this.sc = sc;
    }

    public String readChapterTitle() {
        System.out.print("Enter chapter title: ");
        return sc.nextLine();
    }

    public int readChapterId() {
        System.out.print("Enter chapter ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        return id;
    }

    public Chapter createChapter(int showId) {
        System.out.println("\nChapter Creation:");

        System.out.print("Title: ");
        String title = sc.nextLine();

        int seasonNumber = readPositiveInt("Season number");

        LocalDate releaseDate = readDate("Release date (dd/MM/yyyy)");

        long duration = readPositiveLong("Duration (in minutes)");

        int chapterNumber = readPositiveInt("Chapter number");

        // Confirm creation
        System.out.println("\n=== Confirm Data ===");
        System.out.println("Title: " + title);
        System.out.println("Season: " + seasonNumber);
        System.out.println("Chapter number: " + chapterNumber);
        System.out.println("Release date: " + releaseDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Show ID: " + showId);

        System.out.print("\nConfirm creation? (Y/N): ");
        String response = sc.nextLine().toUpperCase();

        if (response.equals("Y") || response.equals("S")) {
            return new Chapter(title, seasonNumber, releaseDate, duration, showId, chapterNumber);
        } else {
            System.out.println("Creation canceled.");
            return null;
        }
    }

    public Chapter updateChapter(int showId, Chapter chapter) {
        System.out.println("\nChapter Update:");
        displayChapter(chapter);

        System.out.print("\nNew title (ENTER to keep current): ");
        String title = sc.nextLine();
        if (!title.isBlank()) chapter.setTitle(title);

        System.out.print("New season number (ENTER to keep current): ");
        String seasonStr = sc.nextLine();
        if (!seasonStr.isBlank()) {
            try { 
                chapter.setSeasonNumber(Integer.parseInt(seasonStr)); 
            }
            catch (Exception ex) { 
                System.out.println("Invalid value. Maintained."); 
            }
        }

        System.out.print("New release date (dd/MM/yyyy) (ENTER to keep current): ");
        String dateStr = sc.nextLine();
        if (!dateStr.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                chapter.setReleaseDate(date);
            } catch (Exception ex) {
                System.out.println("Invalid date. Maintained.");
            }
        }

        System.out.print("New duration (in minutes) (ENTER to keep current): ");
        String durStr = sc.nextLine();
        if (!durStr.isBlank()) {
            try { 
                chapter.setDuration(Long.parseLong(durStr)); 
            }
            catch (Exception ex) { 
                System.out.println("Invalid value. Maintained."); 
            }
        }

        System.out.print("New chapter number (ENTER to keep current): ");
        String numStr = sc.nextLine();
        if (!numStr.isBlank()) {
            try { 
                chapter.setChapterNumber(Integer.parseInt(numStr)); 
            }
            catch (Exception ex) { 
                System.out.println("Invalid value. Maintained."); 
            }
        }

        System.out.print("\nConfirm changes? (Y/N): ");
        String response = sc.nextLine().toUpperCase();
        if (response.equals("Y") || response.equals("S")) {
            return chapter;
        } else {
            System.out.println("Update canceled.");
            return null;
        }
    }

    public void displaySearchResults(ArrayList<Chapter> chapters) {
        if (chapters.isEmpty()) {
            System.out.println("\nNo chapters found.");
            return;
        }

        System.out.println("\n=== Chapters Found ===");
        for (Chapter chapter : chapters) {
            displayChapter(chapter);
        }
    }

    public void displayChapter(Chapter chapter) {
        if (chapter == null) {
            System.out.println("Chapter not found.");
            return;
        }

        System.out.println("\n---------------------------");
        System.out.println("ID...............: " + chapter.getChapterId());
        System.out.println("Title............: " + chapter.getTitle());
        System.out.println("Season...........: " + chapter.getSeasonNumber());
        System.out.println("Chapter number...: " + chapter.getChapterNumber());
        System.out.println("Release date.....: " + chapter.getReleaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Duration.........: " + chapter.getDuration() + " min");
        System.out.println("Show ID..........: " + chapter.getAssociatedShowId());
        System.out.println("---------------------------");
    }

    public void displayChapterList(ArrayList<Chapter> chapters) {
        if (chapters.isEmpty()) {
            System.out.println("\nNo chapters found.");
            return;
        }

        System.out.println("\n=== Chapter List ===");
        for (Chapter chapter : chapters) {
            displayChapter(chapter);
        }
    }

    public int selectChapterFromResults(ArrayList<Chapter> chapters) {
        if (chapters.isEmpty()) return -1;

        if (chapters.size() == 1) {
            System.out.println("\nChapter automatically selected: " + chapters.get(0).getTitle());
            return chapters.get(0).getChapterId();
        }

        System.out.print("\nEnter the ID of the chapter you want to select (0 to cancel): ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean exists = chapters.stream().anyMatch(ch -> ch.getChapterId() == id);

        if (!exists && id != 0) {
            System.out.println("Invalid ID. Try again.");
            return selectChapterFromResults(chapters);
        }

        return id;
    }

    // ----------------------
    // Helper functions
    // ----------------------

    private int readPositiveInt(String message) {
        int value;
        do {
            System.out.print(message + ": ");
            while (!sc.hasNextInt()) {
                System.out.print("Enter a valid integer. " + message + ": ");
                sc.next();
            }
            value = sc.nextInt();
            sc.nextLine();
            if (value <= 0) {
                System.out.println("Value must be positive.");
            }
        } while (value <= 0);
        return value;
    }

    private long readPositiveLong(String message) {
        long value;
        do {
            System.out.print(message + ": ");
            while (!sc.hasNextLong()) {
                System.out.print("Enter a valid number. " + message + ": ");
                sc.next();
            }
            value = sc.nextLong();
            sc.nextLine();
            if (value <= 0) {
                System.out.println("Value must be positive.");
            }
        } while (value <= 0);
        return value;
    }

    private LocalDate readDate(String message) {
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean ok = false;
        do {
            System.out.print(message + ": ");
            String input = sc.nextLine();
            try {
                date = LocalDate.parse(input, formatter);
                ok = true;
            } catch (Exception e) {
                System.out.println("Invalid date! Use format dd/MM/yyyy.");
            }
        } while (!ok);
        return date;
    }
}
