package config;
/**
 * Class:
 * AppSettings() - configuration model for application settings and preferences.
 *
 * Methods:
 * AppSettings() - constructor with default settings.
 * getTheme() - returns the current theme.
 * setTheme(String) - sets the theme.
 * getResolution() - returns the current resolution.
 * setResolution(String) - sets the resolution.
 * isRememberUsername() - checks if username should be remembered.
 * setRememberUsername(boolean) - sets username remembering preference.
 * getSavedUsername() - returns the saved username.
 * setSavedUsername(String) - sets the saved username.
 * isSidebarOpen() - checks if sidebar is open.
 * setSidebarOpen(boolean) - sets sidebar open state.
 **/

public class AppSettings {
    private String theme;
    private String resolution;
    private boolean rememberUsername;
    private String savedUsername;
    private boolean sidebarOpen;

    public AppSettings() {
        this.theme = "light";
        this.resolution = "1600x900";
        this.rememberUsername = false;
        this.savedUsername = "";
        this.sidebarOpen = false;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public boolean isRememberUsername() {
        return rememberUsername;
    }

    public void setRememberUsername(boolean rememberUsername) {
        this.rememberUsername = rememberUsername;
    }

    public String getSavedUsername() {
        return savedUsername;
    }

    public void setSavedUsername(String savedUsername) {
        this.savedUsername = savedUsername;
    }

    public boolean isSidebarOpen() {
        return sidebarOpen;
    }

    public void setSidebarOpen(boolean sidebarOpen) {
        this.sidebarOpen = sidebarOpen;
    }
}
