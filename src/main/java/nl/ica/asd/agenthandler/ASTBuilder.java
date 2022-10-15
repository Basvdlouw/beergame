package nl.ica.asd.agenthandler;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import nl.ica.asd.agenthandler.antlr.BeerGameBaseListener;
import nl.ica.asd.agenthandler.antlr.BeerGameLexer;
import nl.ica.asd.agenthandler.antlr.BeerGameParser;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.AgentSyntaxContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.ComparisonContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.Else_statementContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.If_statementContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.Int_literalContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.Math_operationContext;
import nl.ica.asd.agenthandler.antlr.BeerGameParser.Variable_assignmentContext;
import nl.ica.asd.agenthandler.exceptions.ScriptErrorException;
import nl.ica.asd.agenthandler.exceptions.TokenException;
import nl.ica.asd.agenthandler.tokens.ASTNode;
import nl.ica.asd.agenthandler.tokens.ElseStatement;
import nl.ica.asd.agenthandler.tokens.IfStatement;
import nl.ica.asd.agenthandler.tokens.Statement;
import nl.ica.asd.agenthandler.tokens.StatementContainer;
import nl.ica.asd.agenthandler.tokens.VariableAssignment;
import nl.ica.asd.agenthandler.tokens.expressions.BoolLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.IntLiteral;
import nl.ica.asd.agenthandler.tokens.expressions.Variable;
import nl.ica.asd.agenthandler.tokens.expressions.operations.Comparison;
import nl.ica.asd.agenthandler.tokens.expressions.operations.ComparisonOperatorType;
import nl.ica.asd.agenthandler.tokens.expressions.operations.LogicalOperation;
import nl.ica.asd.agenthandler.tokens.expressions.operations.LogicalOperatorType;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperation;
import nl.ica.asd.agenthandler.tokens.expressions.operations.MathOperatorType;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

class ASTBuilder extends BeerGameBaseListener {

  private Deque<ASTNode> currentContainer;
  private Statement root;

  ASTBuilder() {
    currentContainer = new LinkedList<>();
  }

  AST getAST(String script) throws ScriptErrorException {
    ParserErrorHandler scriptErrorHandler = new ParserErrorHandler();

    CharStream stream = CharStreams.fromString(script);
    BeerGameLexer lexer = new BeerGameLexer(stream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(scriptErrorHandler);

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    BeerGameParser parser = new BeerGameParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(scriptErrorHandler);

    try {
      ParseTree parseTree = parser.agentSyntax();
      ParseTreeWalker walker = new ParseTreeWalker();
      walker.walk(this, parseTree);
      checkForScriptErrors(scriptErrorHandler);
    } catch (TokenException e) {
      checkForScriptErrors(scriptErrorHandler);
      throw e;
    }

    return new AST(root);
  }

  private void checkForScriptErrors(ParserErrorHandler parserErrorHandler)
      throws ScriptErrorException {
    List<ScriptError> scriptErrors = parserErrorHandler.getScriptErrors();
    if (!scriptErrors.isEmpty()) {
      throw new ScriptErrorException(scriptErrors.toArray(new ScriptError[0]));
    }
  }

  @Override
  public void enterBool_value(BeerGameParser.Bool_valueContext ctx) {
    BoolLiteral boolLiteral;
    if (ctx.comparison() != null) {
      return;
    } else if (ctx.TRUE() != null) {
      boolLiteral = new BoolLiteral(true, ctx.start.getLine(), ctx.start.getCharPositionInLine());
    } else if (ctx.FALSE() != null) {
      boolLiteral = new BoolLiteral(false, ctx.start.getLine(), ctx.start.getCharPositionInLine());
    } else {
      throw new TokenException("Boolvalue is neither TRUE nor FALSE.");
    }

    currentContainer.push(boolLiteral);
  }

  @Override
  public void exitBool_value(BeerGameParser.Bool_valueContext ctx) {
    if (ctx.FALSE() != null || ctx.TRUE() != null) {
      BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
      ASTNode parent = currentContainer.peek();
      Objects.requireNonNull(parent).addChild(boolLiteral);
    }
  }

  @Override
  public void enterLogical_operation(BeerGameParser.Logical_operationContext ctx) {
    LogicalOperatorType operator;
    if (ctx.AND() != null) {
      operator = LogicalOperatorType.AND;
    } else if (ctx.OR() != null) {
      operator = LogicalOperatorType.OR;
    } else if (ctx.bool_value() != null || ctx.BEGIN_BRACKET() != null) {
      // Logical operator between brackets should be ignored.
      return;
    } else {
      throw new TokenException("Invalid operator.");
    }

    LogicalOperation logicalOperation = new LogicalOperation(operator, ctx.start.getLine(),
        ctx.start.getCharPositionInLine());
    currentContainer.push(logicalOperation);
  }

  @Override
  public void exitLogical_operation(BeerGameParser.Logical_operationContext ctx) {
    if (ctx.AND() != null || ctx.OR() != null) {
      LogicalOperation logicalOperation = (LogicalOperation) currentContainer.pop();
      ASTNode parent = currentContainer.peek();
      Objects.requireNonNull(parent).addChild(logicalOperation);
    }
  }

  @Override
  public void enterAgentSyntax(AgentSyntaxContext ctx) {
    root = new StatementContainer(ctx.start.getLine(), ctx.start.getCharPositionInLine());
    currentContainer.push(root);
  }

  @Override
  public void enterIf_statement(If_statementContext ctx) {
    currentContainer.push(new IfStatement(ctx.start.getLine(), ctx.start.getCharPositionInLine()));
  }

  @Override
  public void exitIf_statement(If_statementContext ctx) {
    IfStatement ifStatement = (IfStatement) currentContainer.pop();
    ASTNode parent = currentContainer.peek();
    Objects.requireNonNull(parent).addChild(ifStatement);
  }

  @Override
  public void enterElse_statement(Else_statementContext ctx) {
    currentContainer
        .push(new ElseStatement(ctx.start.getLine(), ctx.start.getCharPositionInLine()));
  }

  @Override
  public void exitElse_statement(Else_statementContext ctx) {
    ElseStatement elseStatement = (ElseStatement) currentContainer.pop();
    ASTNode parent = currentContainer.peek();
    Objects.requireNonNull(parent).addChild(elseStatement);
  }

  @Override
  public void enterComparison(ComparisonContext ctx) {
    ComparisonOperatorType operator;
    if (ctx.EQUALS() != null) {
      operator = ComparisonOperatorType.EQUALS;
    } else if (ctx.MORE_THAN() != null) {
      operator = ComparisonOperatorType.MORE_THAN;
    } else if (ctx.LESS_THAN() != null) {
      operator = ComparisonOperatorType.LESS_THAN;
    } else {
      throw new TokenException("Invalid operator.");
    }

    Comparison comparison = new Comparison(operator, ctx.start.getLine(),
        ctx.start.getCharPositionInLine());
    currentContainer.push(comparison);
  }

  @Override
  public void exitComparison(ComparisonContext ctx) {
    ASTNode comparison = currentContainer.pop();
    ASTNode parent = currentContainer.peek();
    Objects.requireNonNull(parent).addChild(comparison);
  }

  @Override
  public void enterInt_literal(Int_literalContext ctx) {
    ASTNode parent = currentContainer.peek();
    if (parent instanceof IntLiteral) {
      // Fix for the inconsistent solution between the comparisons and math operations.
      return;
    }
    IntLiteral intLiteral = new IntLiteral(Integer.parseInt(ctx.INT().getText()),
        ctx.start.getLine(), ctx.start.getCharPositionInLine());
    Objects.requireNonNull(parent).addChild(intLiteral);
  }

  @Override
  public void enterMath_operation(Math_operationContext ctx) {
    MathOperatorType mathOperatorType;

    if (ctx.int_literal() != null) {
      currentContainer
          .push(new IntLiteral(Integer.valueOf(ctx.int_literal().getText()), ctx.start.getLine(),
              ctx.start.getCharPositionInLine()));
    } else if (ctx.VARIABLE() != null) {
      currentContainer.push(new Variable(ctx.VARIABLE().getText(), ctx.start.getLine(),
          ctx.start.getCharPositionInLine()));
    } else if (ctx.BEGIN_BRACKET() == null) {
      if (ctx.PLUS() != null) {
        mathOperatorType = MathOperatorType.PLUS;
      } else if (ctx.MINUS() != null) {
        mathOperatorType = MathOperatorType.MINUS;
      } else if (ctx.MULTIPLY() != null) {
        mathOperatorType = MathOperatorType.MULTIPLY;
      } else if (ctx.DIVIDE() != null) {
        mathOperatorType = MathOperatorType.DIVIDE;
      } else if (ctx.PERCENTAGE() != null) {
        mathOperatorType = MathOperatorType.PERCENTAGE;
      } else if (ctx.POWER() != null) {
        mathOperatorType = MathOperatorType.POWER;
      } else {
        throw new TokenException("Invalid mathOperatorType.");
      }
      currentContainer.push(new MathOperation(mathOperatorType, ctx.start.getLine(),
          ctx.start.getCharPositionInLine()));
    }
  }

  @Override
  public void exitMath_operation(Math_operationContext ctx) {
    if (ctx.BEGIN_BRACKET() == null) {
      ASTNode astNode = currentContainer.pop();
      Objects.requireNonNull(currentContainer.peek()).addChild(astNode);
    }
  }

  @Override
  public void enterVariable_assignment(Variable_assignmentContext ctx) {
    String variableName = ctx.VARIABLE().getText();
    VariableAssignment variableAssignment = new VariableAssignment(variableName,
        ctx.start.getLine(),
        ctx.start.getCharPositionInLine());
    currentContainer.push(variableAssignment);

    if (ctx.POWER() != null) {
      currentContainer.push(
          getVariableMathOperation(variableName, MathOperatorType.POWER, variableAssignment, ctx)
      );
    } else if (ctx.MULTIPLY() != null) {
      currentContainer.push(
          getVariableMathOperation(variableName, MathOperatorType.MULTIPLY, variableAssignment, ctx)
      );
    } else if (ctx.DIVIDE() != null) {
      currentContainer.push(
          getVariableMathOperation(variableName, MathOperatorType.DIVIDE, variableAssignment, ctx)
      );
    } else if (ctx.PLUS() != null) {
      currentContainer.push(
          getVariableMathOperation(variableName, MathOperatorType.PLUS, variableAssignment, ctx)
      );
    } else if (ctx.MINUS() != null) {
      currentContainer.push(
          getVariableMathOperation(variableName, MathOperatorType.MINUS, variableAssignment, ctx)
      );
    } else if (ctx.PERCENTAGE() != null) {
      currentContainer.push(
          getVariableMathOperation(variableName, MathOperatorType.PERCENTAGE, variableAssignment,
              ctx)
      );
    } else if (ctx.EQUALS() == null) {
      throw new TokenException("Invalid operator on variable assignment.");
    }
  }

  private MathOperation getVariableMathOperation(String variableName, MathOperatorType operatorType,
      VariableAssignment variableAssignment, Variable_assignmentContext ctx) {
    MathOperation mathOperation = new MathOperation(operatorType, ctx.start.getLine(),
        ctx.start.getCharPositionInLine());
    mathOperation.addChild(new Variable(variableName, ctx.start.getLine(),
        ctx.start.getCharPositionInLine()));
    variableAssignment.addChild(mathOperation);
    return mathOperation;
  }

  @Override
  public void exitVariable_assignment(Variable_assignmentContext ctx) {
    if (ctx.EQUALS() == null) {
      currentContainer.pop();
    }
    ASTNode astNode = currentContainer.pop();
    Objects.requireNonNull(currentContainer.peek()).addChild(astNode);
  }
}