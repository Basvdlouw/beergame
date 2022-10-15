grammar BeerGame;

// Parser rules

agentSyntax
  : statement+
  ;

statement
  : if_statement
  | variable_assignment
  ;

variable_assignment
  : VARIABLE (EQUALS | POWER | MULTIPLY | DIVIDE | PLUS | MINUS | PERCENTAGE) math_operation
  ;

if_statement
  : IF logical_operation BEGIN_BRACKET
    statement+
    END_BRACKET
    else_statement?
  ;

else_statement
  : ELSE BEGIN_BRACKET
    statement+
    END_BRACKET
  ;

bool_value
  :  comparison | TRUE | FALSE
  ;

comparison
  : int_value (EQUALS | MORE_THAN | LESS_THAN) int_value
  ;

logical_operation
  :BEGIN_BRACKET logical_operation END_BRACKET
  |logical_operation AND logical_operation
  |logical_operation OR logical_operation
  | bool_value
  ;

math_operation
  : BEGIN_BRACKET math_operation END_BRACKET
  | math_operation POWER math_operation
  | math_operation (MULTIPLY | DIVIDE | PERCENTAGE) math_operation
  | math_operation (PLUS | MINUS) math_operation
  | int_literal
  | VARIABLE
  ;

int_value
  : int_literal | math_operation | VARIABLE
  ;

int_literal
  : '-'? INT | '0'
  ;


// Lexer rules

WS: [ \n\t\r]+ -> skip;

IF
  : I F
  ;

ELSE
  : E L S E
  ;

AND
  : A N D
  ;

OR
  : O R
  ;

BEGIN_BRACKET
  : '('
  ;

END_BRACKET
  : ')'
  ;

PERCENTAGE
  : '%'
  ;

PLUS
  : '+'
  ;

MINUS
  : '-'
  ;

DIVIDE
  : '/'
  ;

MULTIPLY
  : '*'
  ;

POWER
  : '^'
  ;

// Literals

INT
  : '1'..'9' DIGIT*
  ;

DIGIT
  : '0'..'9'
  ;

TRUE
  : T R U E
  ;

FALSE
  : F A L S E
  ;

// Comparison operations
MORE_THAN
  : '>'
  ;
LESS_THAN
  : '<'
  ;
EQUALS
  : '='
  ;

// Variables
VARIABLE
  : ('a'..'z' | 'A'..'Z')+
  ;

// A fragment is somewhat akin to an inline function: It makes the grammar more readable and easier to maintain.
// A fragment will never be counted as a token, it only serves to simplify a grammar.

fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');