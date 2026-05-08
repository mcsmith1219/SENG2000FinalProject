# Video Game Catalog Manager

## PLEASE BE ADVISED
This project is **NOT** finalized.

As of now, this project is a a simple program meant to be ran from the terminal of an IDE of your choice, which does most of the following below.
Last Updated: 4/22/2026

## Description
This project is a Java-based Video Game Catalog Manager created to demonstrate the use of advanced data structures in a practical application for SENG2000, Advanced Data Structures and Algorithims, at ECU. The program stores and manages video game records, including information such as the game name, release date, personal rating, number of ratings, IGN rating, and synopsis.

The project includes two catalog implementations:

- `ArrayListCatalog` — a baseline version that stores games in an `ArrayList` and uses linear search
- `HashmapCatalog` — an optimized version that stores games in a `HashMap` for faster lookup by game name

The program is designed to support core catalog operations such as adding games, searching for a game by name, updating a personal rating, deleting a game, displaying all games, and displaying games in sorted order.

## Project Structure
- `RecordGame.java`  
  Represents a single video game record and stores all game-related information.

- `CatalogBehaviour.java`  
  Interface that defines the required catalog operations.

- `ArrayListCatalog.java`  
  Catalog implementation using an `ArrayList`.

- `HashmapCatalog.java`  
  Catalog implementation using a `HashMap`.

- `main.java`  
  Driver file used to run and test the catalog implementations.

## Features
- Add a game to the catalog
- Search for a game by name
- Update a game’s personal rating
- Delete a game from the catalog
- Display all stored games
- Display games sorted by name
- Display games sorted by release date

## Requirements
- Java 17 or later
- An IDE such as Eclipse, IntelliJ IDEA, or VS Code with Java support

## Compile
Open a terminal in the project folder and compile the Java files included in the Github, then run Main.

Run: 
javac main/*.java catalog/*.java record/*.java

After compiling, run: 
java main.Main