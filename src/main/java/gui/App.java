package gui;
import auth.AuthService;
import auth.RAWGClient;
import config.AppSettings;
import config.SettingsManager;
import database.DatabaseManager;
import database.GameRepository;
import database.UserGameRepository;
import database.UserRepository;
import datastructures.PriorityQueueEntry;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.RecordGame;
import model.User;
import model.UserGameEntry;
import service.CatalogService;
import service.GraphService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.OptionalDouble;

/**
 * Class:
 * App() - main JavaFX application for game catalog management with social features.
 * Handles authentication, UI rendering, and coordination between services.
 *
 * Methods:
 * start(Stage) - entry point for JavaFX application.
 * initializeServices() - initializes database and service layers.
 * initializeUI() - creates main scene and layout.
 * All other UI methods for building catalog, social, and settings interfaces.
 **/

public class App extends Application {
    private AuthService authService;
    private RAWGClient rawgClient;
    private CatalogService catalogService;
    private GraphService graphService;
    private Stage primaryStage;
    private SettingsManager settingsManager;
    private AppSettings appSettings;
    private BorderPane root;
    private VBox settingsSidebar;
    private User currentUser;
    private User viewedPublicUser;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField displayNameField;
    private Label loginStatusLabel;
    private TextField searchField;
    private TextField myGamesSearchField;
    private TextField userSearchField;
    private ListView<String> resultsView;
    private ListView<String> myGamesView;
    private ListView<String> publicGamesView;
    private TextArea libraryStatsArea;
    private ListView<String> socialView;
    private ListView<String> detailCommentsView;
    private Label detailTitleLabel;
    private Label detailReleaseLabel;
    private Label detailRatingLabel;
    private Label detailImageUrlLabel;
    private Label detailLikeCountLabel;
    private Label detailStatusLabel;
    private Label detailVisibilityLabel;
    private Label detailFavoriteLabel;
    private Label detailHoursLabel;
    private Label detailCompletedLabel;
    private TextArea detailSynopsisArea;
    private TextArea detailReviewArea;
    private ImageView detailImageView;
    private ComboBox<String> themeComboBox;
    private ComboBox<String> resolutionComboBox;
    private CheckBox rememberUsernameCheckBox;
    private TextField rememberedUsernameField;
    private List<RecordGame> currentSearchResults = new ArrayList<>();
    private List<UserGameEntry> myGamesList = new ArrayList<>();
    private List<UserGameEntry> publicGamesList = new ArrayList<>();

    @Override

    public void start(Stage stage) {
        this.primaryStage = stage;
        this.settingsManager = new SettingsManager();
        this.appSettings = settingsManager.loadSettings();
        initializeServices();
        initializeControls();
        configureEvents();
        root = new BorderPane();
        root.getStyleClass().add("app-root");
        VBox topArea = new VBox(12, buildTopNavigationBar(), buildSearchToolbarCard());
        topArea.setPadding(new Insets(20, 20, 12, 20));
        root.setTop(topArea);
        SplitPane mainSplit = new SplitPane(buildLeftColumn(), buildCenterColumn(), buildRightColumn());
        mainSplit.setDividerPositions(0.24, 0.62);
        BorderPane.setMargin(mainSplit, new Insets(0, 20, 20, 20));
        ScrollPane mainScrollPane = new ScrollPane(mainSplit);
        mainScrollPane.getStyleClass().add("main-scroll");
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(false);
        mainScrollPane.setPannable(true);
        StackPane centerStack = new StackPane(mainScrollPane, buildSettingsSidebarContainer());
        StackPane.setAlignment(settingsSidebar, Pos.TOP_RIGHT);
        root.setCenter(centerStack);
        Scene scene = new Scene(root, 1550, 1020);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        applyTheme();
        applyResolution();
        if (appSettings.isRememberUsername() && appSettings.getSavedUsername() != null) {
            usernameField.setText(appSettings.getSavedUsername());
        }
        toggleSettingsSidebar(appSettings.isSidebarOpen());
        stage.setTitle("RateYourGames");
        stage.setMinWidth(1200);
        stage.setMinHeight(780);
        stage.setScene(scene);
        stage.show();
    }

    private void initializeServices() {
        DatabaseManager db = new DatabaseManager();
        db.initializeDatabase();
        UserRepository userRepository = new UserRepository(db);
        GameRepository gameRepository = new GameRepository(db);
        UserGameRepository userGameRepository = new UserGameRepository(db, gameRepository, userRepository);
        authService = new AuthService(userRepository);
        catalogService = new CatalogService(gameRepository, userGameRepository);
        graphService = new GraphService();
        seedUsers();
        loadGraphData(userRepository, userGameRepository);
        try {
            rawgClient = new RAWGClient();
        } catch (Exception e) {
            rawgClient = null;
        }
    }

    private void loadGraphData(UserRepository userRepository, UserGameRepository userGameRepository) {
        for (User user : userRepository.getAllUsers()) {
            graphService.addUser(user);
        }
        for (UserGameRepository.FollowPair pair : userGameRepository.getAllFollowPairs()) {
            User follower = userRepository.getAllUsers().stream()
                    .filter(u -> u.getId() == pair.followerUserId)
                    .findFirst()
                    .orElse(null);
            User followed = userRepository.getAllUsers().stream()
                    .filter(u -> u.getId() == pair.followedUserId)
                    .findFirst()
                    .orElse(null);
            if (follower != null && followed != null) {
                graphService.follow(follower, followed);
            }
        }
    }

    private void seedUsers() {
        registerIfNotExists("mcsmith1219", "password123", "Matthew");
        registerIfNotExists("BereketA6", "password123", "Bereket");
        registerIfNotExists("user1", "password123", "user1");
        registerIfNotExists("user2", "password123", "user2");
        registerIfNotExists("user3", "password123", "user3");
        registerIfNotExists("user4", "password123", "user4");
        registerIfNotExists("user5", "password123", "user5");
        registerIfNotExists("user6", "password123", "user6");
    }

    private void registerIfNotExists(String username, String password, String displayName) {
        if (authService.findUser(username) == null) {
            authService.register(username, password, displayName);
        }
    }

    private void initializeControls() {
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(150);
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(150);
        displayNameField = new TextField();
        displayNameField.setPromptText("Display Name");
        displayNameField.setPrefWidth(160);
        loginStatusLabel = new Label("Not logged in.");
        loginStatusLabel.getStyleClass().add("subtle-label");
        searchField = new TextField();
        searchField.setPromptText("Search RAWG for a game...");
        searchField.setPrefWidth(280);
        myGamesSearchField = new TextField();
        myGamesSearchField.setPromptText("Search my games...");
        userSearchField = new TextField();
        userSearchField.setPromptText("Search username...");
        resultsView = new ListView<>();
        myGamesView = new ListView<>();
        publicGamesView = new ListView<>();
        socialView = new ListView<>();
        detailCommentsView = new ListView<>();
        detailTitleLabel = new Label("Select a game");
        detailTitleLabel.getStyleClass().add("detail-title");
        detailReleaseLabel = new Label("Release Date: N/A");
        detailReleaseLabel.getStyleClass().add("detail-meta");
        detailRatingLabel = new Label("RAWG Rating: N/A");
        detailRatingLabel.getStyleClass().add("detail-meta");
        detailImageUrlLabel = new Label("Image URL: N/A");
        detailImageUrlLabel.getStyleClass().add("detail-meta");
        detailImageUrlLabel.setWrapText(true);
        detailLikeCountLabel = new Label("Likes: 0");
        detailLikeCountLabel.getStyleClass().add("detail-meta");
        detailStatusLabel = new Label("Status: N/A");
        detailStatusLabel.getStyleClass().add("detail-meta");
        detailVisibilityLabel = new Label("Visibility: N/A");
        detailVisibilityLabel.getStyleClass().add("detail-meta");
        detailFavoriteLabel = new Label("Favorite: N/A");
        detailFavoriteLabel.getStyleClass().add("detail-meta");
        detailHoursLabel = new Label("Hours Played: N/A");
        detailHoursLabel.getStyleClass().add("detail-meta");
        detailCompletedLabel = new Label("Completed: N/A");
        detailCompletedLabel.getStyleClass().add("detail-meta");
        detailSynopsisArea = new TextArea();
        detailSynopsisArea.setWrapText(true);
        detailSynopsisArea.setEditable(false);
        detailSynopsisArea.setPrefRowCount(5);
        detailReviewArea = new TextArea();
        detailReviewArea.setWrapText(true);
        detailReviewArea.setEditable(false);
        detailReviewArea.setPrefRowCount(4);
        detailImageView = new ImageView();
        detailImageView.setFitWidth(240);
        detailImageView.setFitHeight(320);
        detailImageView.setPreserveRatio(true);
        detailImageView.setSmooth(true);
    }

    private void configureEvents() {
        searchField.setOnAction(e -> handleSearch());
        myGamesSearchField.setOnAction(e -> handleSearchMyGames());
        socialView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selected = socialView.getSelectionModel().getSelectedItem();
                String username = extractUsernameFromSocialLine(selected);
                if (username != null) {
                    userSearchField.setText(username);
                    handleLoadPublicGames();
                }
            }
        });
        resultsView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < currentSearchResults.size()) {
                showSearchResultDetails(currentSearchResults.get(index));
            }
        });
        myGamesView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < myGamesList.size()) {
                showOwnedGameDetails(myGamesList.get(index), currentUser);
            }
        });
        publicGamesView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < publicGamesList.size() && viewedPublicUser != null) {
                showOwnedGameDetails(publicGamesList.get(index), viewedPublicUser);
            }
        });
    }

    private VBox buildTopNavigationBar() {
        Label titleLabel = new Label("RateYourGames");
        titleLabel.getStyleClass().add("nav-title");
        Label subtitleLabel = new Label("Track games, ratings, and social activity powered by advanced data structure views.");
        subtitleLabel.getStyleClass().add("nav-subtitle");
        VBox titleBox = new VBox(4, titleLabel, subtitleLabel);
        Button loginButton = createButton("Login", "primary-button");
        loginButton.setOnAction(e -> handleLogin());
        Button registerButton = createButton("Register", "secondary-button");
        registerButton.setOnAction(e -> handleRegister());
        Button settingsButton = createButton("Settings", "window-button");
        settingsButton.setOnAction(e -> toggleSettingsSidebar(!settingsSidebar.isVisible()));
        Button aboutButton = createButton("About", "window-button");
        aboutButton.setOnAction(e -> showAboutDialog());
        HBox authRow = new HBox(
                10,
                usernameField,
                passwordField,
                displayNameField,
                loginButton,
                registerButton,
                settingsButton,
                aboutButton
        );
        authRow.setAlignment(Pos.CENTER_RIGHT);
        VBox authBox = new VBox(8, authRow, loginStatusLabel);
        authBox.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(20, titleBox, spacer, authBox);
        topRow.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(topRow);
        card.getStyleClass().add("top-nav");
        return card;
    }

    private VBox buildSearchToolbarCard() {
        Button searchButton = createButton("Search RAWG", "primary-button");
        searchButton.setOnAction(e -> handleSearch());
        Button addButton = createButton("Add Selected Game", "accent-button");
        addButton.setOnAction(e -> handleAddSelectedGame());
        Button editButton = createButton("Edit Selected Game", "secondary-button");
        editButton.setOnAction(e -> handleEditSelectedGame());
        Button deleteButton = createButton("Delete Selected Game", "secondary-button");
        deleteButton.setOnAction(e -> handleDeleteSelectedGame());
        FlowPane controls = new FlowPane(10, 10, searchField, searchButton, addButton, editButton, deleteButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        return createCard("Quick Actions", controls);
    }

    private VBox buildLeftColumn() {
        Button loadPublicButton = createButton("Load Public Games", "primary-button");
        loadPublicButton.setOnAction(e -> handleLoadPublicGames());
        Button likePublicButton = createButton("Like", "secondary-button");
        likePublicButton.setOnAction(e -> handleLikePublicSelected());
        Button unlikePublicButton = createButton("Unlike", "secondary-button");
        unlikePublicButton.setOnAction(e -> handleUnlikePublicSelected());
        Button commentPublicButton = createButton("Comment", "secondary-button");
        commentPublicButton.setOnAction(e -> handleCommentPublicSelected());
        Button deleteCommentButton = createButton("Delete My Comment", "secondary-button");
        deleteCommentButton.setOnAction(e -> handleDeleteMyComment());
        Button followButton = createButton("Follow", "accent-button");
        followButton.setOnAction(e -> handleFollowUser());
        Button unfollowButton = createButton("Unfollow", "secondary-button");
        unfollowButton.setOnAction(e -> handleUnfollowUser());
        HBox userSearchRow = new HBox(10, userSearchField, loadPublicButton);
        HBox.setHgrow(userSearchField, Priority.ALWAYS);
        FlowPane actionRow = new FlowPane(10, 10, likePublicButton, unlikePublicButton, commentPublicButton,
                deleteCommentButton, followButton, unfollowButton);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        VBox searchCard = createCard("Search Results", resultsView);
        VBox publicCard = createCard("Socials", userSearchRow, actionRow, publicGamesView);
        VBox.setVgrow(resultsView, Priority.ALWAYS);
        VBox.setVgrow(publicGamesView, Priority.ALWAYS);
        VBox col = new VBox(16, searchCard, publicCard);
        VBox.setVgrow(searchCard, Priority.ALWAYS);
        VBox.setVgrow(publicCard, Priority.ALWAYS);
        return col;
    }

    private VBox buildCenterColumn() {
        Button searchMyGamesButton = createButton("Search", "primary-button");
        searchMyGamesButton.setOnAction(e -> handleSearchMyGames());
        Button refreshMyGamesButton = createButton("Show All", "secondary-button");
        refreshMyGamesButton.setOnAction(e -> refreshMyGames());
        Button showSortedButton = createButton("Sorted Titles", "secondary-button");
        showSortedButton.setOnAction(e -> showSortedGames());
        showSortedButton.setMinWidth(Region.USE_PREF_SIZE);
        Button showTopRatedButton = createButton("Top Rated", "secondary-button");
        showTopRatedButton.setOnAction(e -> showTopRatedGames());
        showTopRatedButton.setMinWidth(Region.USE_PREF_SIZE);
        searchMyGamesButton.setMinWidth(Region.USE_PREF_SIZE);
        refreshMyGamesButton.setMinWidth(Region.USE_PREF_SIZE);
        HBox searchInputRow = new HBox(10, myGamesSearchField, searchMyGamesButton, refreshMyGamesButton);
        HBox.setHgrow(myGamesSearchField, Priority.ALWAYS);
        HBox viewButtonRow = new HBox(10, showSortedButton, showTopRatedButton);
        VBox searchRow = new VBox(8, searchInputRow, viewButtonRow);
        VBox libraryCard = createCard("My Library", searchRow, myGamesView);
        VBox.setVgrow(myGamesView, Priority.ALWAYS);
        libraryStatsArea = new TextArea();
        libraryStatsArea.setEditable(false);
        libraryStatsArea.setWrapText(true);
        libraryStatsArea.setPrefRowCount(6);
        libraryStatsArea.setText("Log in and load your library to see stats.");
        VBox suggestionsCard = createCard("Library Stats", libraryStatsArea);
        VBox.setVgrow(libraryStatsArea, Priority.ALWAYS);
        VBox col = new VBox(16, libraryCard, suggestionsCard);
        VBox.setVgrow(libraryCard, Priority.ALWAYS);
        VBox.setVgrow(suggestionsCard, Priority.ALWAYS);
        return col;
    }

    private VBox buildRightColumn() {
        StackPane imageFrame = new StackPane(detailImageView);
        imageFrame.getStyleClass().add("image-frame");
        VBox details = new VBox(
                10,
                detailTitleLabel,
                detailReleaseLabel,
                detailRatingLabel,
                detailLikeCountLabel,
                detailStatusLabel,
                detailVisibilityLabel,
                detailFavoriteLabel,
                detailHoursLabel,
                detailCompletedLabel,
                detailImageUrlLabel,
                imageFrame,
                new Label("Synopsis"),
                detailSynopsisArea,
                new Label("Review / Record"),
                detailReviewArea,
                new Label("Comments"),
                detailCommentsView
        );
        VBox detailsCard = createCard("Selected Game Details", details);
        VBox socialCard = createCard("Social Overview", socialView);
        VBox col = new VBox(16, detailsCard, socialCard);
        VBox.setVgrow(detailsCard, Priority.ALWAYS);
        return col;
    }

    private StackPane buildSettingsSidebarContainer() {
        settingsSidebar = new VBox(14);
        settingsSidebar.getStyleClass().add("settings-sidebar");
        settingsSidebar.setPrefWidth(300);
        settingsSidebar.setMaxWidth(300);
        settingsSidebar.setMinWidth(300);
        Label header = new Label("Settings");
        header.getStyleClass().add("settings-header");
        Label themeLabel = new Label("Theme");
        themeLabel.getStyleClass().add("subtle-label");
        themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("light", "dark", "ecu");
        themeComboBox.setValue(appSettings.getTheme());
        Label resolutionLabel = new Label("Resolution");
        resolutionLabel.getStyleClass().add("subtle-label");
        resolutionComboBox = new ComboBox<>();
        resolutionComboBox.getItems().addAll("1280x720", "1366x768", "1600x900", "1920x1080", "maximized");
        resolutionComboBox.setValue(appSettings.getResolution());
        rememberUsernameCheckBox = new CheckBox("Remember username");
        rememberUsernameCheckBox.setSelected(appSettings.isRememberUsername());
        rememberedUsernameField = new TextField(appSettings.getSavedUsername());
        rememberedUsernameField.setPromptText("Saved username");
        Label noteLabel = new Label("Click apply to save settings!");
        noteLabel.getStyleClass().add("subtle-label");
        noteLabel.setWrapText(true);
        Button applyButton = createButton("Apply", "primary-button");
        applyButton.setOnAction(e -> applySettingsFromSidebar());
        Button closeButton = createButton("Close", "secondary-button");
        closeButton.setOnAction(e -> toggleSettingsSidebar(false));
        settingsSidebar.getChildren().addAll(
                header,
                themeLabel,
                themeComboBox,
                resolutionLabel,
                resolutionComboBox,
                rememberUsernameCheckBox,
                rememberedUsernameField,
                noteLabel,
                new Separator(),
                applyButton,
                closeButton
        );
        StackPane wrapper = new StackPane(settingsSidebar);
        wrapper.setPickOnBounds(false);
        StackPane.setAlignment(settingsSidebar, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsSidebar, new Insets(14));
        return wrapper;
    }

    private void applySettingsFromSidebar() {
        appSettings.setTheme(themeComboBox.getValue());
        appSettings.setResolution(resolutionComboBox.getValue());
        appSettings.setRememberUsername(rememberUsernameCheckBox.isSelected());
        appSettings.setSavedUsername(rememberUsernameCheckBox.isSelected() ? rememberedUsernameField.getText().trim() : "");
        appSettings.setSidebarOpen(settingsSidebar.isVisible());
        settingsManager.saveSettings(appSettings);
        applyTheme();
        applyResolution();
        if (appSettings.isRememberUsername()) {
            usernameField.setText(appSettings.getSavedUsername());
        }
    }

    private void toggleSettingsSidebar(boolean show) {
        settingsSidebar.setVisible(show);
        settingsSidebar.setManaged(show);
        appSettings.setSidebarOpen(show);
        settingsManager.saveSettings(appSettings);
    }

    private void applyTheme() {
        root.getStyleClass().removeAll("theme-light", "theme-dark", "theme-ecu");
        String theme = appSettings.getTheme() == null ? "light" : appSettings.getTheme().toLowerCase();
        switch (theme) {
            case "dark":
                root.getStyleClass().add("theme-dark");
                break;
            case "ecu":
                root.getStyleClass().add("theme-ecu");
                break;
            default:
                root.getStyleClass().add("theme-light");
                break;
        }
    }

    private void applyResolution() {
        String resolution = appSettings.getResolution() == null ? "1600x900" : appSettings.getResolution();
        if ("maximized".equalsIgnoreCase(resolution)) {
            primaryStage.setMaximized(true);
            return;
        }
        primaryStage.setMaximized(false);
        switch (resolution) {
            case "1280x720":
                primaryStage.setWidth(1280);
                primaryStage.setHeight(720);
                break;
            case "1366x768":
                primaryStage.setWidth(1366);
                primaryStage.setHeight(768);
                break;
            case "1920x1080":
                primaryStage.setWidth(1920);
                primaryStage.setHeight(1080);
                break;
            default:
                primaryStage.setWidth(1600);
                primaryStage.setHeight(900);
                break;
        }
    }

    private VBox createCard(String title, Node... content) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        if (title != null && !title.isBlank()) {
            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add("section-title");
            card.getChildren().add(titleLabel);
        }
        card.getChildren().addAll(content);
        return card;
    }

    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll(styleClass, "icon-button");
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        User user = authService.login(username, password);
        if (user != null) {
            currentUser = user;
            graphService.addUser(user);
            String shownName = user.getDisplayName() == null || user.getDisplayName().isBlank()
                    ? user.getUsername()
                    : user.getDisplayName();
            loginStatusLabel.setText("Logged in as " + shownName + " (@" + user.getUsername() + ")");
            usernameField.clear();
            passwordField.clear();
            displayNameField.clear();
            if (appSettings.isRememberUsername()) {
                appSettings.setSavedUsername(username);
                settingsManager.saveSettings(appSettings);
            }
            refreshMyGames();
            refreshSocialPanel();
        } else {
            loginStatusLabel.setText("Invalid login.");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String displayName = displayNameField.getText().trim();
        if (displayName.isEmpty()) {
            displayName = username;
        }
        boolean created = authService.register(username, password, displayName);
        if (created) {
            User newUser = authService.findUser(username);
            if (newUser != null) {
                graphService.addUser(newUser);
            }
            loginStatusLabel.setText("Account created. You can now log in.");
        } else {
            loginStatusLabel.setText("Could not create account.");
        }
    }

    private void handleSearch() {
        if (currentUser == null) {
            showAlert("Login Required", "Please log in first.");
            return;
        }
        if (rawgClient == null) {
            showAlert("RAWG Error", "Set your RAWG API key in RAWGClient.java.");
            return;
        }
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            showAlert("Search Error", "Enter a game title.");
            return;
        }
        try {
            currentSearchResults = rawgClient.searchGames(query);
            resultsView.getItems().setAll(currentSearchResults.stream()
                    .map(game -> game.getName() + " (" + extractYear(game.getReleaseDate()) + ")")
                    .collect(Collectors.toList()));
            clearGameDetails();
        } catch (Exception e) {
            showAlert("Search Error", "Failed to search RAWG: " + e.getMessage());
        }
    }

    private String extractYear(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank() || "N/A".equalsIgnoreCase(releaseDate)) {
            return "N/A";
        }
        return releaseDate.length() >= 4 ? releaseDate.substring(0, 4) : releaseDate;
    }

    private void handleAddSelectedGame() {
        if (currentUser == null) {
            showAlert("Login Required", "Please log in first.");
            return;
        }
        if (rawgClient == null) {
            showAlert("RAWG Error", "Set your RAWG API key in RAWGClient.java.");
            return;
        }
        int selectedIndex = resultsView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= currentSearchResults.size()) {
            showAlert("Selection Error", "Please select a game from the search results.");
            return;
        }
        try {
            RecordGame selected = currentSearchResults.get(selectedIndex);
            RecordGame detailed = rawgClient.getGameDetails(selected.getRawgId());
            boolean added = catalogService.addGame(currentUser, detailed);
            if (added) {
                refreshMyGames();
                showGameDetails(detailed);
                showAlert("Success", "Game added to your library.");
            } else {
                showAlert("Info", "Game is already in your library.");
            }
        } catch (Exception e) {
            showAlert("Add Error", "Failed to add game: " + e.getMessage());
        }
    }

    private void refreshMyGames() {
        if (currentUser == null) {
            return;
        }
        myGamesList = new ArrayList<>(catalogService.getUserGames(currentUser));
        myGamesView.getItems().setAll(myGamesList.stream().map(this::formatEntry).collect(Collectors.toList()));
        refreshLibraryStats();
    }

    private void refreshLibraryStats() {
        if (libraryStatsArea == null) {
            return;
        }
        if (currentUser == null || myGamesList == null || myGamesList.isEmpty()) {
            libraryStatsArea.setText("Log in and load your library to see stats.");
            return;
        }
        int total = myGamesList.size();
        long playing = myGamesList.stream().filter(e -> "playing".equalsIgnoreCase(e.getStatus())).count();
        long completed = myGamesList.stream().filter(e -> "completed".equalsIgnoreCase(e.getStatus())).count();
        long backlog = myGamesList.stream().filter(e -> "backlog".equalsIgnoreCase(e.getStatus())).count();
        long dropped = myGamesList.stream().filter(e -> "dropped".equalsIgnoreCase(e.getStatus())).count();
        long favorites = myGamesList.stream().filter(UserGameEntry::isFavorite).count();
        double totalHours = myGamesList.stream().mapToDouble(e -> e.getHoursPlayed()).sum();
        OptionalDouble avgRating = myGamesList.stream()
                .filter(e -> e.getPersonalRating() != null && e.getPersonalRating() > 0)
                .mapToDouble(e -> e.getPersonalRating())
                .average();
        Optional<UserGameEntry> topGame = myGamesList.stream()
                .filter(e -> e.getPersonalRating() != null && e.getPersonalRating() > 0)
                .max((a, b) -> Double.compare(a.getPersonalRating(), b.getPersonalRating()));
        StringBuilder sb = new StringBuilder();
        sb.append("Total Games: ").append(total).append("\n\n");
        sb.append("Playing: ").append(playing).append("\n");
        sb.append("Completed: ").append(completed).append("\n");
        sb.append("Backlog: ").append(backlog).append("\n");
        sb.append("Dropped: ").append(dropped).append("\n\n");
        sb.append("Favorites: ").append(favorites).append("\n");
        sb.append(String.format("Total Hours: %.1f h%n", totalHours));
        avgRating.ifPresent(rating -> sb.append(String.format("Average Rating: %.1f / 5.0%n", rating)));
        topGame.ifPresent(entry -> sb.append("\nTop Rated:\n").append(entry.getGame().getName()));
        libraryStatsArea.setText(sb.toString());
    }

    private void showSortedGames() {
        if (currentUser == null) {
            return;
        }
        List<String> sorted = catalogService.getGamesSortedByTitle(currentUser).stream()
                .map(entry -> entry.getGame().getName())
                .collect(Collectors.toList());
        myGamesList = new ArrayList<>(catalogService.getGamesSortedByTitle(currentUser));
        myGamesView.getItems().setAll(sorted);
    }

    private void showTopRatedGames() {
        if (currentUser == null) {
            return;
        }
        List<PriorityQueueEntry> topRated = catalogService.getTopRatedGames(currentUser, 5);
        List<String> topRatedStrings = topRated.stream()
                .map(PriorityQueueEntry::toString)
                .collect(Collectors.toList());
        myGamesView.getItems().setAll(topRatedStrings);
        myGamesList.clear();
    }

    private void handleSearchMyGames() {
        if (currentUser == null) {
            showAlert("Login Required", "Please log in first.");
            return;
        }
        String query = myGamesSearchField.getText().trim();
        if (query.isEmpty()) {
            refreshMyGames();
            return;
        }
        myGamesList = new ArrayList<>(catalogService.searchMyGames(currentUser, query));
        myGamesView.getItems().setAll(myGamesList.stream().map(this::formatEntry).collect(Collectors.toList()));
    }

    private void handleLoadPublicGames() {
        if (currentUser == null) {
            showAlert("Login Required", "Please log in first.");
            return;
        }
        User user = authService.findUser(userSearchField.getText().trim());
        if (user == null) {
            showAlert("Search Error", "User not found.");
            return;
        }
        viewedPublicUser = user;
        graphService.addUser(user);
        publicGamesList = new ArrayList<>(catalogService.getPublicUserGames(user));
        publicGamesView.getItems().setAll(publicGamesList.stream().map(this::formatEntry).collect(Collectors.toList()));
        clearGameDetails();
        refreshSocialPanel();
    }

    private void handleLikePublicSelected() {
        if (!ensureViewedPublicSelection()) {
            return;
        }
        UserGameEntry selected = publicGamesList.get(publicGamesView.getSelectionModel().getSelectedIndex());
        boolean liked = catalogService.addLike(viewedPublicUser, currentUser, selected.getGame().getRawgId());
        if (liked) {
            showOwnedGameDetails(selected, viewedPublicUser);
        } else {
            showAlert("Info", "Already liked or could not like.");
        }
    }

    private void handleUnlikePublicSelected() {
        if (!ensureViewedPublicSelection()) {
            return;
        }
        UserGameEntry selected = publicGamesList.get(publicGamesView.getSelectionModel().getSelectedIndex());
        boolean removed = catalogService.removeLike(viewedPublicUser, currentUser, selected.getGame().getRawgId());
        if (removed) {
            showOwnedGameDetails(selected, viewedPublicUser);
        } else {
            showAlert("Info", "You have not liked this entry or it could not be removed.");
        }
    }

    private void handleCommentPublicSelected() {
        if (!ensureViewedPublicSelection()) {
            return;
        }
        UserGameEntry selected = publicGamesList.get(publicGamesView.getSelectionModel().getSelectedIndex());
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText("Comment on " + selected.getGame().getName());
        dialog.setContentText("Comment:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            boolean commented = catalogService.addComment(viewedPublicUser, currentUser, selected.getGame().getRawgId(), result.get().trim());
            if (commented) {
                showOwnedGameDetails(selected, viewedPublicUser);
            } else {
                showAlert("Info", "Duplicate comment or failed to add comment.");
            }
        }
    }

    private void handleDeleteMyComment() {
        if (!ensureViewedPublicSelection()) {
            return;
        }
        String selectedComment = detailCommentsView.getSelectionModel().getSelectedItem();
        if (selectedComment == null || selectedComment.isBlank() || selectedComment.equals("No comments yet.")) {
            showAlert("Selection Error", "Select one of your comments from the comments list.");
            return;
        }
        String myDisplay = currentUser.getDisplayName() == null || currentUser.getDisplayName().isBlank()
                ? currentUser.getUsername()
                : currentUser.getDisplayName();
        String myPrefix = myDisplay + ": ";
        String fallbackPrefix = currentUser.getUsername() + ": ";
        if (!selectedComment.startsWith(myPrefix) && !selectedComment.startsWith(fallbackPrefix)) {
            showAlert("Permission Error", "You can only delete your own comments.");
            return;
        }
        String commentText = selectedComment.startsWith(myPrefix)
                ? selectedComment.substring(myPrefix.length())
                : selectedComment.substring(fallbackPrefix.length());
        UserGameEntry selectedGame = publicGamesList.get(publicGamesView.getSelectionModel().getSelectedIndex());
        boolean deleted = catalogService.deleteComment(viewedPublicUser, currentUser, selectedGame.getGame().getRawgId(), commentText);
        if (deleted) {
            showOwnedGameDetails(selectedGame, viewedPublicUser);
        } else {
            showAlert("Info", "Could not delete that comment.");
        }
    }

    private boolean ensureViewedPublicSelection() {
        if (currentUser == null || viewedPublicUser == null) {
            showAlert("Selection Error", "Load a user's public games first.");
            return false;
        }
        int index = publicGamesView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= publicGamesList.size()) {
            showAlert("Selection Error", "Select a public game first.");
            return false;
        }
        return true;
    }

    private void handleFollowUser() {
        if (currentUser == null || viewedPublicUser == null) {
            showAlert("Selection Error", "Load a user first.");
            return;
        }
        boolean followed = catalogService.followUser(currentUser, viewedPublicUser);
        if (followed) {
            graphService.follow(currentUser, viewedPublicUser);
            refreshSocialPanel();
        } else {
            showAlert("Info", "Already following or failed.");
        }
    }

    private void handleUnfollowUser() {
        if (currentUser == null || viewedPublicUser == null) {
            showAlert("Selection Error", "Load a user first.");
            return;
        }
        boolean unfollowed = catalogService.unfollowUser(currentUser, viewedPublicUser);
        if (unfollowed) {
            graphService.unfollow(currentUser, viewedPublicUser);
            refreshSocialPanel();
        } else {
            showAlert("Info", "Not currently following or failed.");
        }
    }

    private void refreshSocialPanel() {
        socialView.getItems().clear();
        if (currentUser == null) {
            return;
        }
        Set<String> following = graphService.getFollowing(currentUser);
        Set<String> followers = graphService.getFollowers(currentUser);
        List<String> recommendations = graphService.recommendFollows(currentUser);
        socialView.getItems().add("Following Count: " + following.size());
        socialView.getItems().add("Following: " + formatUserList(following));
        socialView.getItems().add("Follower Count: " + followers.size());
        socialView.getItems().add("Followers: " + formatUserList(followers));
        if (viewedPublicUser != null) {
            Set<String> mutuals = graphService.getMutuals(currentUser, viewedPublicUser);
            socialView.getItems().add("Mutuals with @" + viewedPublicUser.getUsername() + ": " + mutuals.size());
        }
        socialView.getItems().add("Follow Recommendations: " + formatUserList(recommendations));
    }

    private String formatUserList(Iterable<String> usernames) {
        List<String> formatted = new ArrayList<>();
        for (String username : usernames) {
            formatted.add("@" + username);
        }
        return formatted.isEmpty() ? "None" : formatted.stream().sorted().collect(Collectors.joining(", "));
    }

    private String extractUsernameFromSocialLine(String line) {
        if (line == null) {
            return null;
        }
        int at = line.indexOf('@');
        if (at < 0) {
            return null;
        }
        String username = line.substring(at + 1).trim();
        int comma = username.indexOf(',');
        return comma < 0 ? username : username.substring(0, comma).trim();
    }

    private void handleDeleteSelectedGame() {
        if (currentUser == null) {
            showAlert("Login Required", "Please log in first.");
            return;
        }
        int index = myGamesView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= myGamesList.size()) {
            showAlert("Selection Error", "Select a game from My Games to delete.");
            return;
        }
        UserGameEntry selected = myGamesList.get(index);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Game");
        confirm.setHeaderText("Remove game from your library?");
        confirm.setContentText("This will remove \"" + selected.getGame().getName() + "\" from your library.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean removed = catalogService.removeGame(currentUser, selected.getGame().getRawgId());
            if (removed) {
                refreshMyGames();
                clearGameDetails();
            } else {
                showAlert("Error", "Could not remove the selected game.");
            }
        }
    }

    private void handleEditSelectedGame() {
        if (currentUser == null) {
            showAlert("Login Required", "Please log in first.");
            return;
        }
        int index = myGamesView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= myGamesList.size()) {
            showAlert("Selection Error", "Select a game from My Games to edit.");
            return;
        }
        UserGameEntry selected = myGamesList.get(index);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Game Entry");
        dialog.setHeaderText("Update details for " + selected.getGame().getName());
        ChoiceBox<String> statusBox = new ChoiceBox<>();
        statusBox.getItems().addAll("backlog", "playing", "completed", "dropped");
        statusBox.setValue(selected.getStatus());
        TextField ratingField = new TextField(String.valueOf(selected.getPersonalRating()));
        TextArea reviewArea = new TextArea(selected.getUserRecord() == null ? "" : selected.getUserRecord());
        reviewArea.setWrapText(true);
        reviewArea.setPrefRowCount(4);
        CheckBox publicCheckBox = new CheckBox("Public");
        publicCheckBox.setSelected(selected.isPublic());
        CheckBox favoriteCheckBox = new CheckBox("Favorite");
        favoriteCheckBox.setSelected(selected.isFavorite());
        TextField hoursPlayedField = new TextField(String.valueOf(selected.getHoursPlayed()));
        TextField completedDateField = new TextField(selected.getDateCompleted() == null ? "" : selected.getDateCompleted());
        completedDateField.setPromptText("YYYY-MM-DD or leave blank");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Status"), 0, 0);
        grid.add(statusBox, 1, 0);
        grid.add(new Label("Rating (0-10)"), 0, 1);
        grid.add(ratingField, 1, 1);
        grid.add(new Label("Hours Played"), 0, 2);
        grid.add(hoursPlayedField, 1, 2);
        grid.add(new Label("Completion Date"), 0, 3);
        grid.add(completedDateField, 1, 3);
        grid.add(new Label("Visibility"), 0, 4);
        grid.add(publicCheckBox, 1, 4);
        grid.add(new Label("Favorite"), 0, 5);
        grid.add(favoriteCheckBox, 1, 5);
        grid.add(new Label("Review / Record"), 0, 6);
        grid.add(reviewArea, 1, 6);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double newRating = Double.parseDouble(ratingField.getText().trim());
                double hoursPlayed = Double.parseDouble(hoursPlayedField.getText().trim());
                if (newRating < 0 || newRating > 10 || hoursPlayed < 0) {
                    showAlert("Error", "Invalid rating or hours played.");
                    return;
                }
                String completionDate = completedDateField.getText().trim();
                if (completionDate.isBlank()) {
                    completionDate = null;
                }
                boolean updated = catalogService.updateEntry(
                        currentUser,
                        selected.getGame().getRawgId(),
                        reviewArea.getText().trim(),
                        newRating,
                        publicCheckBox.isSelected(),
                        statusBox.getValue(),
                        favoriteCheckBox.isSelected(),
                        hoursPlayed,
                        completionDate
                );
                if (updated) {
                    refreshMyGames();
                } else {
                    showAlert("Error", "Failed to update game entry.");
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Rating and hours played must be numbers.");
            }
        }
    }

    private void showSearchResultDetails(RecordGame game) {
        showGameDetails(game);
        detailLikeCountLabel.setText("Likes: N/A");
        detailStatusLabel.setText("Status: N/A");
        detailVisibilityLabel.setText("Visibility: N/A");
        detailFavoriteLabel.setText("Favorite: N/A");
        detailHoursLabel.setText("Hours Played: N/A");
        detailCompletedLabel.setText("Completed: N/A");
        detailReviewArea.clear();
        List<String> comments = catalogService.getAllCommentsForGame(game.getRawgId());
        if (comments.isEmpty()) {
            detailCommentsView.getItems().setAll("No comments yet.");
        } else {
            detailCommentsView.getItems().setAll(comments);
        }
    }

    private void showOwnedGameDetails(UserGameEntry entry, User owner) {
        if (entry == null || owner == null) {
            clearGameDetails();
            return;
        }
        showGameDetails(entry.getGame());
        detailLikeCountLabel.setText("Likes: " + catalogService.getLikeCount(owner, entry.getGame().getRawgId()));
        detailStatusLabel.setText("Status: " + safeText(entry.getStatus()));
        detailVisibilityLabel.setText("Visibility: " + (entry.isPublic() ? "Public" : "Private"));
        detailFavoriteLabel.setText("Favorite: " + (entry.isFavorite() ? "Yes" : "No"));
        detailHoursLabel.setText("Hours Played: " + entry.getHoursPlayed());
        detailCompletedLabel.setText("Completed: " + safeText(entry.getDateCompleted()));
        detailReviewArea.setText(safeText(entry.getUserRecord()));
        List<String> comments = catalogService.getComments(owner, entry.getGame().getRawgId());
        if (comments.isEmpty()) {
            detailCommentsView.getItems().setAll("No comments yet.");
        } else {
            detailCommentsView.getItems().setAll(comments);
        }
    }

    private void showGameDetails(RecordGame game) {
        if (game == null) {
            clearGameDetails();
            return;
        }
        detailTitleLabel.setText(safeText(game.getName()));
        detailReleaseLabel.setText("Release Date: " + safeText(game.getReleaseDate()));
        detailRatingLabel.setText("RAWG Rating: " + game.getRatingRawg());
        detailImageUrlLabel.setText("Image URL: " + safeText(game.getImageUrl()));
        detailSynopsisArea.setText(safeText(game.getSynopsis()));
        String imageUrl = game.getImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                detailImageView.setImage(new Image(imageUrl, true));
            } catch (Exception e) {
                detailImageView.setImage(null);
            }
        } else {
            detailImageView.setImage(null);
        }
    }

    private void clearGameDetails() {
        detailTitleLabel.setText("Select a game");
        detailReleaseLabel.setText("Release Date: N/A");
        detailRatingLabel.setText("RAWG Rating: N/A");
        detailImageUrlLabel.setText("Image URL: N/A");
        detailLikeCountLabel.setText("Likes: 0");
        detailStatusLabel.setText("Status: N/A");
        detailVisibilityLabel.setText("Visibility: N/A");
        detailFavoriteLabel.setText("Favorite: N/A");
        detailHoursLabel.setText("Hours Played: N/A");
        detailCompletedLabel.setText("Completed: N/A");
        detailSynopsisArea.clear();
        detailReviewArea.clear();
        detailCommentsView.getItems().clear();
        detailImageView.setImage(null);
    }

    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }

    private String formatEntry(UserGameEntry entry) {
        String favoriteBadge = entry.isFavorite() ? "[★] " : "";
        String visibilityBadge = entry.isPublic() ? "[Public] " : "[Private] ";
        String statusBadge = "[" + safeText(entry.getStatus()) + "] ";
        return favoriteBadge + visibilityBadge + statusBadge + entry.getGame().getName()
                + " | Rating: " + entry.getPersonalRating()
                + " | Hours: " + entry.getHoursPlayed();
    }

    private void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("RateYourGames");
        about.setContentText(
                "Developed for SENG 2000\n\n" +
                "This application allows users to track their video game libraries,\n" +
                "rate and review games, follow other users, and explore their\n" +
                "collection through various data structure views including\n" +
                "Trie suggestions, TreeMap sorting, and PriorityQueue ranking.\n\n" +
                "Powered by the RAWG Video Games Database API."
        );
        about.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
