package nl.ica.asd.storage.applicationdatastorage;

import java.util.prefs.Preferences;

public class DataAccess implements ApplicationDataAccess {

  private Preferences preferences;

  private static final String USERNAME = "username";
  private static final String LANGUAGE = "language";
  private static final String DEFAULT_LANGUAGE_CODE = "en-US";

  public DataAccess() {
    // Retrieve the user preference node
    preferences = Preferences.userNodeForPackage(this.getClass());
  }

  @Override
  public String getUsername() {
    return preferences.get(USERNAME, "");
  }

  @Override
  public String getCurrentLanguage() {
    return preferences.get(LANGUAGE, DEFAULT_LANGUAGE_CODE);
  }

  @Override
  public void setLanguageConfig(String language) {
    preferences.put(LANGUAGE, language);
  }

  @Override
  public void setUsername(String username) {
    preferences.put(USERNAME, username);
  }
}
