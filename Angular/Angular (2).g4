grammar Angular;

// Parser Rules
prog: expression* EOF;

expression
    : importStatement
    | interfaceDeclaration
    | componentDeclaration
    | classDeclaration
    | htmlTemplate
    ;

importStatement
    : 'import' '{' importItems '}' 'from' stringLiteral ';'
    ;

importItems
    : ID (',' ID)*
    ;

interfaceDeclaration
    : 'interface' ID '{' interfaceBody '}'
    ;

interfaceBody
    : interfaceProperty*
    ;

interfaceProperty
    : ID ':' type ';'
    ;

componentDeclaration
    : '@Component' '(' componentMetadata ')' classDeclaration
    ;

componentMetadata
    : '{' componentProperty* '}'
    ;

componentProperty
    : selector
    | templateUrl
    | styleUrls
    | imports
    | template
    ;

selector
    : 'selector' ':' stringLiteral ','
    ;

templateUrl
    : 'templateUrl' ':' stringLiteral ','
    ;

template
    : 'template' ':' templateString ','
    ;

styleUrls
    : 'styleUrls' ':' '[' stringLiteral (',' stringLiteral)* ']' ','
    ;

imports
    : 'imports' ':' '[' ID (',' ID)* ']' ','
    ;

classDeclaration
    : 'export'? 'class' ID classBody
    ;

classBody
    : '{' classMember* '}'
    ;

classMember
    : propertyDeclaration
    | methodDeclaration
    | constructorDeclaration
    ;

propertyDeclaration
    : ID ':' type ('=' expression)? ';'
    ;

methodDeclaration
    : ID '(' parameterList? ')' (':' type)? methodBody
    ;

constructorDeclaration
    : 'constructor' '(' parameterList? ')' methodBody
    ;

parameterList
    : parameter (',' parameter)*
    ;

parameter
    : ID ':' type
    ;

methodBody
    : '{' statement* '}'
    ;

statement
    : expressionStatement
    | returnStatement
    | ifStatement
    | forStatement
    | assignmentStatement
    ;

expressionStatement
    : expression ';'
    ;

returnStatement
    : 'return' expression? ';'
    ;

ifStatement
    : 'if' '(' expression ')' statement ('else' statement)?
    ;

forStatement
    : 'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
    ;

forInit
    : variableDeclaration | expressionList
    ;

forUpdate
    : expressionList
    ;

assignmentStatement
    : ID '=' expression ';'
    ;

type
    : 'string'
    | 'number'
    | 'boolean'
    | 'void'
    | 'any'
    | 'int'
    | ID
    | arrayType
    | unionType
    ;

arrayType
    : type '[]'
    ;

unionType
    : type '|' type
    ;

htmlTemplate
    : '<' htmlElement '>'
    ;

htmlElement
    : htmlTag htmlContent* '</' htmlTag '>'
    ;

htmlTag
    : ID
    ;

htmlContent
    : text
    | expression
    | htmlElement
    | directive
    ;

directive
    : '*ngIf' '=' expression
    | '*ngFor' '=' expression
    | '(click)' '=' expression
    | '[' ID ']' '=' expression
    | '{{' expression '}}'
    ;

text
    : TEXT
    ;

// Lexer Rules
ID: [a-zA-Z_][a-zA-Z0-9_]*;
TEXT: ~[<>{}[\]]+;
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' .*? '*/' -> skip;

// الرموز الخاصة
DOT: '.';
COMMA: ',';
SEMICOLON: ';';
COLON: ':';
EQUALS: '=';
PLUS: '+';
MINUS: '-';
MULTIPLY: '*';
DIVIDE: '/';
MODULO: '%';
NOT: '!';
AND: '&&';
OR: '||';
QUESTION: '?';
OPEN_PAREN: '(';
CLOSE_PAREN: ')';
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
OPEN_ANGLE: '<';
CLOSE_ANGLE: '>';
PIPE: '|';
AT: '@';
HASH: '#';
DOLLAR: '$';
PERCENT: '%';
CARET: '^';
AMPERSAND: '&';
TILDE: '~';
BACKTICK: '`';

// النصوص
stringLiteral
    : SingleQuote stringContent SingleQuote
    | DoubleQuote stringContent DoubleQuote
    | BackTickQuote stringContent BackTickQuote
    ;

stringContent
    : ~['"`]*
    ;

SingleQuote: '\'';
DoubleQuote: '"';
BackTickQuote: '`';

templateString
    : BackTickQuote templateContent BackTickQuote
    ;

templateContent
    : ~[`]*
    ;

// الأرقام
NUMBER: [0-9]+ ('.' [0-9]+)?;
HEX: '0x' [0-9a-fA-F]+;
BINARY: '0b' [01]+;
OCTAL: '0o' [0-7]+;

// المعرفات الخاصة
ANGULAR_DIRECTIVE: '*ng' [a-zA-Z]+;
EVENT_BINDING: '(' [a-zA-Z]+ ')';
PROPERTY_BINDING: '[' [a-zA-Z]+ ']';
INTERPOLATION: '{{' .*? '}}'; 