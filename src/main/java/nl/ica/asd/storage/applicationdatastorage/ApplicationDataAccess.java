package nl.ica.asd.storage.applicationdatastorage;

public interface ApplicationDataAccess {

  /**
   * This method returns the current set username.
   *
   * @return the username that has been set by the user
   */
  String getUsername();

  /**
   * This method sets the username based on the given parameter.
   *
   * @param username the string that will be set as username
   */
  void setUsername(String username);

  /**
   * This method returns the current set Language.
   *
   * @return the language that has been set by the user
   */
  String getCurrentLanguage();

  /**
   * This method sets the LanguageConfig based on the given parameter.
   *
   * @param language the new language that will be set
   */
  void setLanguageConfig(String language);
}
