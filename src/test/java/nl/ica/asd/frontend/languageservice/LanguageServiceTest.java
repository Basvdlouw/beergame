package nl.ica.asd.frontend.languageservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import nl.ica.asd.storage.applicationdatastorage.ApplicationDataAccess;
import nl.ica.asd.storage.applicationdatastorage.DataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class LanguageServiceTest {

  @Mock
  private ApplicationDataAccess applicationDataAccess;

  @InjectMocks
  private LanguageService languageService;

  @BeforeEach
  void beforeClass() {
    applicationDataAccess = mock(DataAccess.class);
    when(applicationDataAccess.getCurrentLanguage()).thenReturn("nl-NL");
    languageService = new LanguageAccess(applicationDataAccess);
  }

  @Test
  void setLanguageConfigTest() {
    languageService.setLanguageConfig("en-US");
    verify(applicationDataAccess, times(1)).setLanguageConfig("en-US");
  }

  @Test
  void getLanguageBundleTestDefault() {
    ResourceBundle rsBundle = languageService.getLanguageBundle();
    Locale locale = rsBundle.getLocale();
    //Check if the returned resourcebundle is the default english version
    assertEquals("nl", locale.getLanguage());
  }

  @Test
  void getLanguageBundleTestEnglish() {
    //Get current language
    String language =
        languageService.getLanguageBundle().getLocale().getLanguage() + "-" + languageService
            .getLanguageBundle().getLocale().getCountry();
    //Get all languages
    Map<String, String> values = languageService.getAllLanguageDataKeys();
    //Check if languages contains the set language
    assertTrue(values.keySet().contains(language));
  }

  @Test
  void getAllLanguageDataTest() {
    //We make a list with all languages that should be available
    List<String> list = Arrays.asList("Nederlands", "English");

    assertIterableEquals(list, languageService.getAllLanguageData());
  }
}
