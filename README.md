# Video Game Catalog Manager

## Overview
Video Game Catalog Manager is a JavaFX desktop app for searching video games, saving games to a personal library, reviewing games, and interacting with other demo users through follows, likes, comments, mutuals, and recommendations.

The project was created for SENG 2000 to show how advanced data structures can support a practical application while inluding benchmark testing to prove why HashMap is superior for
this particular project converse to linear search.

## Project Objective
The objective of this app is to combine a usable game catalog interface with data structure demonstrations.

The app includes:

- RAWG API game search
- user registration and login
- personal game libraries
- public game libraries
- ratings, reviews, status, favorites, hours played, and completion dates
- likes and comments on public game entries
- following and unfollowing users
- social overview information
- BFS-based follow recommendations
- readable structure snapshots in `data_structures`
- terminal benchmark tests comparing catalog implementations

## Main Data Structures Used

- `Trie` supports title suggestions while searching a user's library.
- `TreeMap` supports sorted title output.
- `PriorityQueue` supports top-rated game views.
- `BFSGraph` supports following, followers, mutuals, and BFS follow recommendations.
- `BTree` supports indexed lookup in the file-based repositories.
- `HashSet` helps prevent duplicate user-game entries.
- `HashMap` is compared against `ArrayList` in the benchmark tests.

## Demo Users

The project includes preloaded users for the in-class demo at ECU to showcase it's social features. You can login to one of the 8 accounts below to see how this app functions:

| Username      | Password      | Display Name |
| `mcsmith1219` | `password123` | Matthew      |
| `BereketA6`   | `password123` | Bereket      |
| `user1`       | `password123` | user1        |
| `user2`       | `password123` | user2        |
| `user3`       | `password123` | user3        |
| `user4`       | `password123` | user4        |
| `user5`       | `password123` | user5        |
| `user6`       | `password123` | user6        |

For example: when `mcsmith1219` views `BereketA6`, the Social Overview should show the current user's following count, followers, mutual count, and BFS follow recommendations in a cleaner format.

## RAWG API Key

RAWG search requires a RAWG API key.

1. Create a RAWG account.
2. Get an API key from the RAWG API documentation page.
3. Open this file:

```text
src/main/java/auth/RAWGClient.java
```

4. Replace the value of `RAWG_API_KEY` with your own key.

```java
private static final String RAWG_API_KEY = "PASTE_YOUR_RAWG_API_KEY_HERE";
```

Please note: The included demo data still works without the API key, but live RAWG searching will not work until the key is set.

## Requirements

- Java 21 or later
- Maven Daemon (`mvnd`) or regular Maven
- JavaFX support through the Maven dependencies in `pom.xml`
- Internet connection for RAWG API searches
- RAWG API key for live search features

## Installing mvnd

Download Maven Daemon, extract it, and add the `bin` folder to your system PATH.

After installing it, verify it with:

```powershell
mvnd -version
```

If `mvnd` is not available, regular Maven may also work with the same commands by replacing `mvnd` with `mvn`.

## How to Run the App

Open a terminal in the project folder.

Compile the project:

```powershell
mvnd clean compile
```

Run the JavaFX app:

```powershell
mvnd javafx:run
```

Clean and run in one step:

```powershell
mvnd clean javafx:run
```

## How to Run the Benchmark Test

The benchmark compares `CatalogArrayListImpl` and `CatalogHashMapImpl` by adding games, searching by name, checking IDs, and sorting titles.

Run the benchmark class from your IDE, or use Maven/Java once the project is compiled.

Benchmark source files:

```text
src/main/java/service/CatalogBenchmark.java
src/main/java/service/CatalogBenchmarkTest.java
```

Benchmark output file:

```text
data_structures/catalog_benchmark.txt
```

## Benchmark Results Summary

The saved benchmark results show that `HashMap` is usually much faster than `ArrayList` for exact title search and ID existence checks, especially as the test size grows.

Highlights from the included benchmark output:

- At 10 games, exact name search was about `3.47x` faster with `HashMap`.
- At 50 games, ID existence checks were about `64.15x` faster with `HashMap`.
- At 500 games, adding games was about `8.89x` faster with `HashMap`.
- At 1000 games, exact name search was about `448.16x` faster with `HashMap`.

Some sorting results vary because both structures still need to produce ordered output, but the benchmark clearly shows why hash-based lookup is useful for catalog search operations.
