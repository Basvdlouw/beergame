package nl.ica.asd.frontend.languageservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.inject.Inject;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;

public class LanguageAccess implements LanguageService {

  private ApplicationDataAccess applicationDataAccess;
  private static final String RESOURCE_BUNDLE = "BeerGameBundle";
  private static final String LANGUAGE_BUNDLE = "BeerGameLanguagesBundle";

  @Inject
  public LanguageAccess(ApplicationDataAccess applicationDataAccess) {
    this.applicationDataAccess = applicationDataAccess;
  }

  @Override
  public void setLanguageConfig(String language) {
    applicationDataAccess.setLanguageConfig(language);
  }

  @Override
  public List<String> getAllLanguageData() {
    ResourceBundle bundle = ResourceBundle.getBundle(LANGUAGE_BUNDLE, getLanguageLocale());
    List<String> languagesList = Collections.list(bundle.getKeys());
    List<String> languagesValues = new ArrayList<>();
    for (String key : languagesList) {
      languagesValues.add(bundle.getString(key));
    }
    return languagesValues;
  }

  @Override
  public Map<String, String> getAllLanguageDataKeys() {
    HashMap<String, String> map = new HashMap<>();
    ResourceBundle bundle = ResourceBundle.getBundle(LANGUAGE_BUNDLE, getLanguageLocale());

    if (bundle.getKeys().hasMoreElements()) {
      List<String> languagesList = Collections.list(bundle.getKeys());
      for (int i = 0; i < languagesList.size(); i++) {
        map.put(languagesList.get(i), bundle.getString(languagesList.get(i)));
      }
    }
    return map;
  }

  @Override
  public ResourceBundle getLanguageBundle() {
    return ResourceBundle.getBundle(RESOURCE_BUNDLE, getLanguageLocale());
  }

  private Locale getLanguageLocale() {
    String language = applicationDataAccess.getCurrentLanguage();
    String country = language.split("-")[0];
    String region = language.split("-")[1];
    return new Locale(country, region);
  }
}
