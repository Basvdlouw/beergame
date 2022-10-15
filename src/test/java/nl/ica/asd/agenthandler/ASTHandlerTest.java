package nl.ica.asd.agenthandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;
import nl.ica.asd.agenthandler.tokens.StatementContainer;
import nl.ica.asd.agenthandler.tokens.VariableAssignment;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ASTHandlerTest {

  private ASTHandler astHandler;

  @BeforeEach
  void setUp() {
    astHandler = new ASTHandler();
  }

  @Test
  void build_should_parseAndCheckAST() throws ScriptErrorException {
    VariableAssignment variableAssignment = new VariableAssignment("order", 0, 0);
    variableAssignment.addChild(new IntLiteral(10, 0, 0));
    StatementContainer container = new StatementContainer(0, 0);
    container.addChild(variableAssignment);
    AST ast = new AST(container);
    assertEquals(astHandler.build("order = 10"), ast);
  }

  @Test
  void check_should_throwCheckerScriptErrorException_when_astIsInvalid() {
    AST ast = new AST(new VariableAssignment("budget", 0, 0));
    ScriptErrorException exception = assertThrows(ScriptErrorException.class,
        () -> astHandler.check(ast));
    assertEquals(1, exception.getScriptErrors().length, "Expected exactly one error");
    assertEquals(ScriptErrorType.CHECKER_ERROR, exception.getScriptErrors()[0].getErrorType());
  }
}
