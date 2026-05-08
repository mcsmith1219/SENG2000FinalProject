package auth;
import model.RecordGame;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Please note: This class requires a valid RAWG API key to function. Obtain one from https://rawg.io/apidocs and set the RAWG_API_KEY constant.
 *
 * Class:
 * RAWGClient() - client for interacting with RAWG Video Games Database API.
 * Provides methods to search games and get game details by RAWG ID.
 *
 * Methods:
 * RAWGClient() - constructor that initializes HTTP client and checks API key.
 * searchGames(String query) - searches for games by name and returns list of RecordGame.
 * getGameDetails(int rawgId) - retrieves detailed information for a game by RAWG ID.
 **/

public class RAWGClient {
    private static final String RAWG_API_KEY = "INSERT_YOUR_RAWG_API_KEY_HERE";
    private final HttpClient httpClient;

    public RAWGClient() {
        if (RAWG_API_KEY.isBlank() || "PASTE_YOUR_RAWG_API_KEY_HERE".equals(RAWG_API_KEY)) {
            throw new IllegalStateException("Set your RAWG API key in RAWGClient.java");
        }
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<RecordGame> searchGames(String query) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://api.rawg.io/api/games?key=" + RAWG_API_KEY + "&search=" + encodedQuery + "&search_precise=true" + "&page_size=10";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject root = new JSONObject(response.body());
        JSONArray results = root.getJSONArray("results");
        List<RecordGame> games = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);
            Integer rawgId = obj.getInt("id");
            String name = obj.optString("name", "Unknown");
            String releaseDate = obj.optString("released", "N/A");
            Double ratingRawg = obj.has("rating") ? obj.getDouble("rating") : 0.0;
            Integer numberOfRatings = obj.has("ratings_count") ? obj.getInt("ratings_count") : 0;
            String imageUrl = obj.optString("background_image", "");
            games.add(new RecordGame(rawgId, name, releaseDate, 0.0, numberOfRatings, ratingRawg, "Synopsis could not be found.", imageUrl));
        }
        return games;
    }

    public RecordGame getGameDetails(int rawgId) throws IOException, InterruptedException {
        String url = "https://api.rawg.io/api/games/" + rawgId + "?key=" + RAWG_API_KEY;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject obj = new JSONObject(response.body());
        Integer id = obj.getInt("id");
        String name = obj.optString("name", "Unknown");
        String releaseDate = obj.optString("released", "N/A");
        Double ratingRawg = obj.has("rating") ? obj.getDouble("rating") : 0.0;
        Integer numberOfRatings = obj.has("ratings_count") ? obj.getInt("ratings_count") : 0;
        String synopsis = obj.optString("description_raw", "No synopsis available.");
        String imageUrl = obj.optString("background_image", "");
        return new RecordGame(id, name, releaseDate, 0.0, numberOfRatings, ratingRawg, synopsis, imageUrl);
    }
}
