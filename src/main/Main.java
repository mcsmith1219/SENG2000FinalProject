package main;
import catalog.ArrayListCatalog;
import catalog.CatalogBehaviour;
import java.util.List;
import java.util.Scanner;
import record.RecordGame;

//*
// Main is the entry point of the video game catalog manager application. It provides a command-line interface for users to interact with the catalog, allowing them to add, search, update, delete, and display games in various sorted orders. 
// The class uses a CatalogBehaviour implementation (ArrayListCatalog) to manage the collection of RecordGame objects. 
// 
// Classes: Main
// Methods: main, printGames
//*

public class Main {
    public static void main(String[] args) {
        // Initialize the scanner for user input and the catalog to store games //
        Scanner scanner = new Scanner(System.in);
        CatalogBehaviour catalog = new ArrayListCatalog();

        // Main loop to display the menu and handle user choices //
        boolean running = true;
        while (running) {
            System.out.println("\n=== Video Game Catalog Manager ===");
            System.out.println("1. Add game");
            System.out.println("2. Search game");
            System.out.println("3. Update personal rating");
            System.out.println("4. Delete game");
            System.out.println("5. Display all games");
            System.out.println("6. Display games sorted by name");
            System.out.println("7. Display games sorted by release date");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

        // Handle user choices using a switch statement to perform the corresponding actions based on the selected menu option. //
        switch (choice) {
                // Case 1: Add a new game to the catalog by prompting the user for game details and creating a RecordGame object. The game is then added to the catalog using the addGame method. If the game already exists, a message is displayed.
                case "1":
                    System.out.print("Game name: ");
                    String name = scanner.nextLine();
                    System.out.print("Release date (YYYY-MM-DD): ");
                    String releaseDate = scanner.nextLine();
                    System.out.print("Personal rating: ");
                    double personalRating = Double.parseDouble(scanner.nextLine());
                    System.out.print("Number of ratings: ");
                    int numberOfRatings = Integer.parseInt(scanner.nextLine());
                    System.out.print("IGN rating: ");
                    double ignRating = Double.parseDouble(scanner.nextLine());
                    System.out.print("Synopsis: ");
                    String synopsis = scanner.nextLine();
                    RecordGame game = new RecordGame(name, releaseDate, personalRating,
                            numberOfRatings, ignRating, synopsis);
                    if (catalog.addGame(game)) {
                        System.out.println("Game added.");
                    } else {
                        System.out.println("Game already exists.");
                    }
                    break;

                // Case 2: Search for a game in the catalog by prompting the user for the game name. The searchByName method is called to retrieve the game, and if found, its details are displayed. If the game is not found, a message is shown.
                case "2":
                    System.out.print("Enter game name: ");
                    RecordGame found = catalog.searchByName(scanner.nextLine());
                    if (found != null) {
                        System.out.println(found);
                    } else {
                        System.out.println("Game not found.");
                    }
                    break;

                // Case 3: Update the personal rating of an existing game by prompting the user for the game name and the new rating. The updatePersonalRating method is called to perform the update, and a message is displayed indicating whether the update was successful or if the game was not found.
                case "3":
                    System.out.print("Enter game name: ");
                    String updateName = scanner.nextLine();
                    System.out.print("Enter new personal rating: ");
                    double newRating = Double.parseDouble(scanner.nextLine());
                    if (catalog.updatePersonalRating(updateName, newRating)) {
                        System.out.println("Rating updated.");
                    } else {
                        System.out.println("Game not found.");
                    }
                    break;

                // Case 4: Delete a game from the catalog by prompting the user for the game name. The deleteGame method is called to remove the game, and a message is displayed indicating whether the deletion was successful or if the game was not found.
                case "4":
                    System.out.print("Enter game name: ");
                    if (catalog.deleteGame(scanner.nextLine())) {
                        System.out.println("Game deleted.");
                    } else {
                        System.out.println("Game not found.");
                    }
                    break;

                // Case 5: Display all games in the catalog by calling the getAllGames method and printing the list of games. If the catalog is empty, a message is shown.
                case "5":
                    printGames(catalog.getAllGames());
                    break;

                // Case 6: Display games sorted by name by calling the getGamesSortedByName method and printing the sorted list of games. If the catalog is empty, a message is shown.
                case "6":
                    printGames(catalog.getGamesSortedByName());
                    break;

                // Case 7: Display games sorted by release date by calling the getGamesSortedByReleaseDate method and printing the sorted list of games. If the catalog is empty, a message is shown.
                case "7":
                    printGames(catalog.getGamesSortedByReleaseDate());
                    break;

                // Case 8: Exit the program by setting the running flag to false and displaying an exit message.
                case "8":
                    running = false;
                    System.out.println("Exiting program.");
                    break;

                // Default case: Handle invalid menu options by displaying an error message.
                default:
                    System.out.println("Invalid option.");
            }
        }
    scanner.close();
    }

    private static void printGames(List<RecordGame> games) {
        // Helper method to print a list of games. If the list is empty, it displays a message indicating that the catalog is empty. //
        if (games.isEmpty()) {
            System.out.println("Catalog is empty.");
            return;
        }
        // If the list is not empty, it iterates through the games and prints their details using the toString method of the RecordGame class. //
        for (RecordGame game : games) {
            System.out.println(game);
        }
    }
}


