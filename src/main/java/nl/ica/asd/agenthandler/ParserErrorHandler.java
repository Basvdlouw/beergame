package nl.ica.asd.agenthandler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ParserErrorHandler implements ANTLRErrorListener {

  private final List<ScriptError> scriptErrors;

  ParserErrorHandler() {
    scriptErrors = new ArrayList<>();
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
      int charPositionInLine, String msg, RecognitionException e) {
    ScriptError scriptError = new ScriptError(ScriptErrorType.SYNTAX_ERROR, msg, line,
        charPositionInLine);
    scriptErrors.add(scriptError);
  }

  @Override
  public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
      boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
    // Ambiguity is not handled at the moment.
  }

  @Override
  public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
      BitSet conflictingAlts, ATNConfigSet configs) {
    // Attempting full context is not handled at the moment.
  }

  @Override
  public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
      int prediction, ATNConfigSet configs) {
    // Context sensitivity is not handled at the moment.
  }

  List<ScriptError> getScriptErrors() {
    return scriptErrors;
  }
}
