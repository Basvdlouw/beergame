package nl.ica.asd.frontend.gui.agentconfiguration;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Named;
import nl.ica.asd.agenthandler.AgentHandler;
import nl.ica.asd.agenthandler.DefaultAgentHandler;
import nl.ica.asd.agenthandler.ScriptError;
import nl.ica.asd.agenthandler.exceptions.AgentException;
import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;
import nl.ica.asd.frontend.gui.scenecreator.SceneController;
import nl.ica.asd.frontend.gui.scenecreator.SceneCreator;
import nl.ica.asd.frontend.gui.scenecreator.SceneLoaderInitializable;
import nl.ica.asd.logic.domain.BusinessRules;
import nl.ica.asd.logic.domain.builders.AgentBuilder;
import nl.ica.asd.logic.watchers.BusinessrulesChangedWatcher;
import nl.ica.asd.logic.watchers.events.BusinessrulesChangedEvent;
import nl.ica.asd.logic.watchers.events.Event;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentConfigurationController extends
    SceneController<AgentConfigurationViewModel> implements SceneLoaderInitializable,
    BusinessrulesChangedWatcher, Initializable {

  private static final Logger logger = LoggerFactory.getLogger(AgentConfigurationController.class);

  @Inject
  private AgentHandler agentHandler;

  @Inject
  private @Named("PlayTurnSceneCreator")
  SceneCreator playTurnSceneCreator;

  @FXML
  private Pane editorPane;

  @FXML
  private Pane editorPaneError;

  @FXML
  private ListView businessRulesError;

  @FXML
  private Label errorMessage;

  @FXML
  private CodeArea businessRules;

  @FXML
  private Button agentConfigurationSaveButton;

  @FXML
  private Button agentConfigurationBackButton;

  private static final String[] KEYWORDS = new String[]{
      "if", "else", "and", "or"
  };
  private static final String[] VARIABLE = new String[]{
      "round", "stock", "openorders", "outgoinggoods",
      "incominggoods", "incomingorders", "budget", "incominggoodsprice", "outgoinggoodsprice",
      "stockprize", "openordersprize", "order"
  };
  private static final String VARIABLE_PATTERN = "\\b(?i)(" + String.join("|", VARIABLE) + ")\\b";
  private static final String KEYWORD_PATTERN = "\\b(?i)(" + String.join("|", KEYWORDS) + ")\\b";
  private static final String PAREN_PATTERN = "\\(|\\)";
  private static final String INT_PATTERN = "(?<=\\s|^)\\d+(?=\\s|$)";

  private static final Pattern PATTERN = Pattern.compile(
      "(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
          "|(?<VARIABLE>" + VARIABLE_PATTERN + ")" +
          "|(?<PAREN>" + PAREN_PATTERN + ")" +
          "|(?<INT>" + INT_PATTERN + ")"
  );


  private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    Matcher matcher = PATTERN.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder
        = new StyleSpansBuilder<>();
    while (matcher.find()) {
      String styleClass = null;
      if (matcher.group("KEYWORD") != null) {
        styleClass = "keyword";
      } else if (matcher.group("VARIABLE") != null) {
        styleClass = "variable";
      } else if (matcher.group("PAREN") != null) {
        styleClass = "paren";
      } else if (matcher.group("INT") != null) {
        styleClass = "int";
      }
      assert styleClass != null;
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastKwEnd = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    return spansBuilder.create();
  }

  @Override
  public void sceneLoaderInitialize(URL location, ResourceBundle resources) {
    // add line numbers to the left of area
    businessRules.setParagraphGraphicFactory(LineNumberFactory.get(businessRules));

    // multi plain changes = save computation by not rerunning the code multiple times
    //   when making multiple changes (e.g. renaming a method at multiple parts in file)
    businessRules.multiPlainChanges().
        // do not emit an event until 500 ms have passed since the last emission of previous stream
            successionEnds(Duration.ofMillis(500)).
        // run the following code block when previous stream emits an event
            subscribe(
            ignore -> businessRules.setStyleSpans(0, computeHighlighting(businessRules.getText())));

    businessRules.getStylesheets().add("AgentKeywords.css");

    editorPane.visibleProperty().bind(viewModel.getDefaultVisibleProperty());
    editorPaneError.visibleProperty().bind(viewModel.getErrorVisibleProperty());
    errorMessage.visibleProperty().bind(viewModel.getErrorVisibleProperty());
    businessRulesError.itemsProperty().bind(viewModel.getBusinessRulesErrorProperty());
    agentConfigurationSaveButton.disableProperty()
        .bindBidirectional(viewModel.getErrorVisibleProperty());

    try {
      businessRules.replaceText(agentHandler.getBusinessRules(viewModel.getBusiness()));
    } catch (AgentException e) {
      logger.error("Unhandled AgentException!", e);
    }

    businessRules.setOnKeyReleased(event -> {
      try {
        if (event.getCode() == KeyCode.DIGIT9 && event.isShiftDown()) {
          int caretPosition = businessRules.getCaretPosition();
          businessRules.insertText(caretPosition, ")");
          businessRules.displaceCaret(caretPosition);
        }
      } catch (IndexOutOfBoundsException e) {
        logger.error("Unhandled IndexOutOfBoundsException!", e);

      }
      checkErrors();
    });
  }


  @FXML
  private void handleAgentConfigurationSaveButton(ActionEvent action) {
    String script = businessRules.getText();
    if (isEnteredBusinessRulesValid(script)) {
      try {
        viewModel.setBusiness(viewModel.getBusiness().getBusinessType()
            .getBuilderFromBusiness(viewModel.getBusiness())
            .withAgent(AgentBuilder.fromBaseAgent(viewModel.getBusiness().getAgent())
                .withBusinessRules(script).build()).build());
        agentHandler.setBusinessRules(new BusinessRules(viewModel.getBusiness(), script));
      } catch (ScriptErrorException e) {
        setErrors(e);
        viewModel.doBusinessRulesErrorIndicator(true);
      } catch (AgentException e) {
        logger.error("Unexpected agent exception", e);
        viewModel.doBusinessRulesErrorIndicator(true);
      }
      ((Stage) agentConfigurationBackButton.getScene().getWindow()).close();
    } else {
      viewModel.doBusinessRulesErrorIndicator(true);
    }

  }

  private void clearErrors() {
    viewModel.setBusinessRulesErrorProperty(Arrays.asList());
    viewModel.doBusinessRulesErrorIndicator(false);
  }

  private void setErrors(AgentException e) {
    if (e instanceof ScriptErrorException) {
      viewModel.doBusinessRulesErrorIndicator(true);
      List<String> errorList = new ArrayList<>();
      ScriptError[] scriptErrors = ((ScriptErrorException) e).getScriptErrors();
      Arrays.stream(scriptErrors).forEach(scriptError -> {
        String errorString = "Line: " + scriptError.getLine() + " ";
        errorString += scriptError.getMessage();
        errorList.add(errorString);
      });
      viewModel.setBusinessRulesErrorProperty(errorList);
    }
  }

  @FXML
  private void handleAgentConfigurationBackButton(ActionEvent action) {
    ((Stage) agentConfigurationBackButton.getScene().getWindow()).close();
  }

  private boolean isEnteredBusinessRulesValid(String businessRules) {
    try {
      agentHandler.validateBusinessRules(businessRules);
    } catch (ScriptErrorException e) {
      setErrors(e);
      return false;
    } catch (AgentException e) {
      logger.error("Unhandled AgentException!", e);
    }
    return true;
  }

  private void checkErrors() {
    if (isEnteredBusinessRulesValid(businessRules.getText())) {
      clearErrors();
    }
  }

  public void onBusinessrulesChanged(BusinessrulesChangedEvent event) {
    // No need to implement this at the moment.
  }

  @Override
  public void subscribeAll() {
    DefaultAgentHandler.subscribeBusinessrulesChanged(this);
  }

  @Override
  public void unSubscribeAll() {
    DefaultAgentHandler.unsubscribeBusinessrulesChanged(this);
  }

  @Override
  public <E extends Event> void onEvent(E event) {
    onBusinessrulesChanged((BusinessrulesChangedEvent) event);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.subscribeAll();
  }
}