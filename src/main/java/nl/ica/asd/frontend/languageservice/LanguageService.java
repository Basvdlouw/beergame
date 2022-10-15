package nl.ica.asd.frontend.languageservice;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public interface LanguageService {

  ResourceBundle getLanguageBundle();

  void setLanguageConfig(String language);

  List<String> getAllLanguageData();

  Map<String, String> getAllLanguageDataKeys();
}
