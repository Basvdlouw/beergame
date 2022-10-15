package nl.ica.asd.frontend.gui.converters;

import javafx.scene.control.TextField;

public class InputRestrictor {

  private static final String STRINGMATCHER = "\\d*";
  private static final String STRINGREPLACER = "[^\\d]";
  private static final String IPMATCHER = "\\d*\\.\\d*";
  private static final String IPREPLACER = "[^\\d*.]";
  private static final String NAMEMATCHER = "[\\p{L}\\p{Z}\\p{P}\\d]+";
  private static final String NAMEREPLACER = "[^\\p{L}\\p{Z}\\p{P}\\d]+";
  private static final int ONLYNUMERIC = 1;
  private static final int IPADDRESS = 2;
  private static final int GAMENAME = 3;

  public void restrictInput(TextField textField, int restrictionLevel) {
    switch (restrictionLevel) {
      case ONLYNUMERIC:
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
          String turns = newValue;
          if (!newValue.matches(STRINGMATCHER)) {
            turns = newValue.replaceAll(STRINGREPLACER, "");
          }
          if (!turns.equals("")) {
            try {
              Integer.parseInt(turns);
            } catch (NumberFormatException e) {
              turns = String.valueOf(Integer.MAX_VALUE);
            }
          }
          textField.setText(turns);
        });
        break;
      case IPADDRESS:
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
          if (!newValue.matches(IPMATCHER)) {
            textField.setText(newValue.replaceAll(IPREPLACER, ""));
          }
        });
        break;
      case GAMENAME:
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
          if (!newValue.matches(NAMEMATCHER)) {
            textField.setText(newValue.replaceAll(NAMEREPLACER, ""));
          }
        });
        break;
      default:
        break;
    }
  }

  public int getONLYNUMERIC() {
    return ONLYNUMERIC;
  }

  public int getIPADDRESS() {
    return IPADDRESS;
  }

  public int getGAMENAME() {
    return GAMENAME;
  }
}
