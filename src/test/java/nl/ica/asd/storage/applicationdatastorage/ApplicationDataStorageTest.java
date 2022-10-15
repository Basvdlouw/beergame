package nl.ica.asd.storage.applicationdatastorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApplicationDataStorageTest {

  //System Under Testing
  private DataAccess sut;

  @BeforeEach
  public void beforeClass() {
    sut = new DataAccess();
  }

  @Test
  public void setLanguageConfig() {
    //Retrieve current value
    final String oldLanguage = sut.getCurrentLanguage();

    //The variables used for the test
    final String newLanguage = "en-US";

    //Set the variables
    sut.setLanguageConfig(newLanguage);

    //Check if the variables are correct
    assertEquals(sut.getCurrentLanguage(), newLanguage);

    //Set back old value
    sut.setLanguageConfig(oldLanguage);

    //Check if old value is set back
    assertEquals(oldLanguage, sut.getCurrentLanguage());
  }

  @Test
  public void setUsername() {
    //Retrieve current value
    final String oldUsername = sut.getUsername();

    //The variables
    final String newUsername = "MJ.vanWalstijn";

    //Set the variables
    sut.setUsername(newUsername);

    //Check if the variables are correct
    assertEquals(sut.getUsername(), newUsername);

    //Set back old value
    sut.setUsername(oldUsername);

    //Check if old value is set back
    assertEquals(oldUsername, sut.getUsername());
  }
}
