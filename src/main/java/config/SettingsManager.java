package config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Class:
 * SettingsManager() - manages application settings persistence in properties file.
 *
 * Methods:
 * loadSettings() - loads settings from properties file or creates default settings.
 * saveSettings(AppSettings) - saves settings to properties file.
 **/

public class SettingsManager {
    private static final Path SETTINGS_DIR = Paths.get("app_data");
    private static final Path SETTINGS_FILE = SETTINGS_DIR.resolve("settings.properties");

    public AppSettings loadSettings() {
        AppSettings settings = new AppSettings();
        try {
            Files.createDirectories(SETTINGS_DIR);
            if (!Files.exists(SETTINGS_FILE)) {
                saveSettings(settings);
                return settings;
            }
            Properties props = new Properties();
            try (var in = Files.newInputStream(SETTINGS_FILE)) {
                props.load(in);
            }
            settings.setTheme(props.getProperty("theme", "light"));
            settings.setResolution(props.getProperty("resolution", "1600x900"));
            settings.setRememberUsername(Boolean.parseBoolean(props.getProperty("rememberUsername", "false")));
            settings.setSavedUsername(props.getProperty("savedUsername", ""));
            settings.setSidebarOpen(Boolean.parseBoolean(props.getProperty("sidebarOpen", "false")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load settings.", e);
        }
        return settings;
    }

    public void saveSettings(AppSettings settings) {
        try {
            Files.createDirectories(SETTINGS_DIR);
            Properties props = new Properties();
            props.setProperty("theme", settings.getTheme());
            props.setProperty("resolution", settings.getResolution());
            props.setProperty("rememberUsername", String.valueOf(settings.isRememberUsername()));
            props.setProperty("savedUsername", settings.getSavedUsername() == null ? "" : settings.getSavedUsername());
            props.setProperty("sidebarOpen", String.valueOf(settings.isSidebarOpen()));
            try (var out = Files.newOutputStream(SETTINGS_FILE)) {
                props.store(out, "Application Settings");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save settings.", e);
        }
    }
}
