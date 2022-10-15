package nl.ica.asd.agenthandler;

/**
 * Error type for script errors.
 *
 * Used by the ScriptErrorHandler and ScriptErrorListener to determine the kind of error that
 * occurred when processing a script.
 */
public enum ScriptErrorType {
  /**
   * Indicates a syntax error occurred while parsing the script.
   */
  SYNTAX_ERROR,
  /**
   * Indicates a checker error occurred while checking the script.
   */
  CHECKER_ERROR
}
