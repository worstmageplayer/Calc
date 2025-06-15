package parser;

import lexer.Token.*;
import parser.Node.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lexer.Token.Operator.getBindingPower;

public class Parser {
    private final TokenType[] tokens;
    private final int tokensLength;
    private int pos = 0;

    private Parser(TokenType[] tokens) {
        this.tokens = tokens;
        this.tokensLength = tokens.length;
    }

    public static NodeType parse(TokenType[] tokens) {
        return new Parser(tokens).parseExpression(0);
    }

    private NodeType prefixNodeSoThatMyIDECanShutUP(Prefix prefix) {
        return new PrefixOperationNode(parseExpression(9), prefix);
    }

    private NodeType parseExpression(int minBp) {
        TokenType token = tokens[pos++];
        NodeType lhs;

        switch (token) {
            case NumberToken num -> lhs = new NumberNode(num.value());
            case IdentifierToken id -> {
                if (isOpenParenthesis(tokens[pos])) {
                    pos++; // Consume '('
                    List<NodeType> args = new ArrayList<>();

                    while (!isCloseParenthesis(tokens[pos])) {
                        args.add(parseExpression(0));
                        if (!isCloseParenthesis(tokens[pos])) {
                            if (!(tokens[pos++] instanceof CommaToken)) {
                                throw new RuntimeException("Expected comma between arguments");
                            }
                        }
                    }

                    pos++; // Consume '('
                    lhs = new FunctionNode(id, args);
                } else {
                    lhs = new VariableNode(id);
                }
            }
            case PrefixToken prefix -> lhs = prefixNodeSoThatMyIDECanShutUP(prefix.prefix());
            case ParenthesisToken p when p.parenthesis() == Parenthesis.OPEN -> {
                lhs = parseExpression(0);
                if (!isCloseParenthesis(tokens[pos])) {
                    throw new RuntimeException("Expected closing parenthesis");
                }
                pos++;
            }
            default -> throw new RuntimeException("Unexpected token: " + token);
        }

        loop:
        while (pos < tokensLength) {
            int[] bp;
            Operator implicitMul = Operator.MUL;
            TokenType next = tokens[pos];

            switch (next) {
                case EndToken ignored -> {break loop;}
                case CommaToken ignored -> {break loop;}
                case SemiColonToken ignored -> {break loop;}
                case ParenthesisToken(Parenthesis parenthesis) -> {
                    if (parenthesis == Parenthesis.CLOSE) break loop;

                    bp = getBindingPower(implicitMul);
                    if (bp[0] < minBp) break loop;

                    pos++; // Consume'('
                    NodeType rhs = parseExpression(0);
                    if (!isCloseParenthesis(tokens[pos])) throw new RuntimeException("Expected closing parenthesis");
                    pos++; // Consume ')'

                    lhs = new BinaryOperationNode(lhs, rhs, implicitMul);
                }
                case SuffixToken(Suffix suffix) -> {
                    pos++; // Consume 'suffix'
                    lhs = new SuffixOperationNode(lhs, suffix);
                }
                case IdentifierToken id -> {
                    NodeType rhs;
                    pos++; // Consume 'variable'
                    if (isOpenParenthesis(tokens[pos])) {
                        pos++; // Consume '('
                        List<NodeType> args = new ArrayList<>();

                        while (!isCloseParenthesis(tokens[pos])) {
                            args.add(parseExpression(0));
                            if (!isCloseParenthesis(tokens[pos])) {
                                if (!(tokens[pos++] instanceof CommaToken)) {
                                    throw new RuntimeException("Expected comma between arguments");
                                }
                            }
                        }

                        pos++; // Consume ')'
                        rhs = new FunctionNode(id, args);
                    } else {
                        rhs = new VariableNode(id);
                    }

                    bp = getBindingPower(implicitMul);
                    if (bp[0] < minBp) break loop;
                    lhs = new BinaryOperationNode(lhs, rhs, implicitMul);
                }
                case OperatorToken(Operator operator) -> {
                    bp = getBindingPower(operator);
                    if (bp[0] < minBp) break loop;
                    pos++; // Consume 'operator'
                    lhs = new BinaryOperationNode(lhs, parseExpression(bp[1]), operator);
                }
                case NumberToken(BigDecimal value) -> {
                    bp = getBindingPower(implicitMul);
                    if (bp[0] < minBp) break loop;
                    pos++; // Consume 'number'
                    NodeType rhs = new NumberNode(value);
                    lhs = new BinaryOperationNode(lhs, rhs, implicitMul);
                }
                case PrefixToken ignored -> throw new RuntimeException("This shouldn't be here");
            }
        }

        return lhs;
    }

    private boolean isOpenParenthesis(TokenType token) {
        return token instanceof ParenthesisToken(Parenthesis parenthesis) && parenthesis == Parenthesis.OPEN;
    }

    private boolean isCloseParenthesis(TokenType token) {
        return token instanceof ParenthesisToken(Parenthesis parenthesis) && parenthesis == Parenthesis.CLOSE;
    }

}