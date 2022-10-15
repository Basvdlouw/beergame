package nl.ica.asd.frontend.gui.personalsettings;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;

public class PersonalSettingsViewModel {

  private StringProperty usernameProperty = new SimpleStringProperty("");

  private StringProperty userNameErrorMessageProperty = new SimpleStringProperty();

  private ObjectProperty<ObservableList> languagesProperty = new SimpleObjectProperty<>(
      FXCollections.emptyObservableList());

  private ObjectProperty<SingleSelectionModel> selectedLanguageProperty = new SimpleObjectProperty<>(
      null);


  public StringProperty getUsernameProperty() {
    return usernameProperty;
  }

  public ObjectProperty<ObservableList> getLanguagesProperty() {
    return languagesProperty;
  }

  public ObjectProperty<SingleSelectionModel> getSelectedLanguageProperty() {
    return selectedLanguageProperty;
  }

  public StringProperty getUserNameErrorMessageProperty() {
    return userNameErrorMessageProperty;
  }

  public String getUsername() {
    return usernameProperty.get();
  }

  public void setUsername(String username) {
    if (username != null) {
      usernameProperty.set(username);
    }
  }

  public String getSelectedLanguageCode() {
    return ((LanguageComboBoxItem) selectedLanguageProperty.get().getSelectedItem()).languageCode;
  }

  public String getSelectedLanguageName() {
    return ((LanguageComboBoxItem) selectedLanguageProperty.get().getSelectedItem()).languageName;
  }

  public void setLanguages(Map<String, String> languages) {
    List<LanguageComboBoxItem> comboBoxItems = languages.entrySet().stream()
        .map(x -> new LanguageComboBoxItem(x.getKey(), x.getValue())).collect(Collectors.toList());

    languagesProperty.set(FXCollections.observableList(comboBoxItems));
  }

  public void selectLanguageByCode(String languageCode) {
    if (languageCode != null) {
      LanguageComboBoxItem comboBoxItem = (LanguageComboBoxItem) languagesProperty.get().stream()
          .filter(x -> ((LanguageComboBoxItem) x).languageCode.equals(languageCode)).findAny()
          .orElse(null);

      if (comboBoxItem != null) {
        selectedLanguageProperty.get().select(comboBoxItem);
      }
    }
  }

  private static class LanguageComboBoxItem {

    private final String languageCode;

    private final String languageName;

    private LanguageComboBoxItem(String languageCode, String languageName) {
      this.languageCode = languageCode;
      this.languageName = languageName;
    }

    @Override
    public String toString() {
      return languageName;
    }
  }
}
