package lexer;

import lexer.Token.TokenType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lexer.Token.*;
import static lexer.Token.Operator.isOperator;
import static lexer.Token.Parenthesis.isParenthesis;
import static lexer.Token.Matrix.isMatrix;
import static lexer.Token.Prefix.isPrefix;
import static lexer.Token.Suffix.isSuffix;

public class Lexer {
    public static TokenType[] tokenize(String input) {
        final int inputLength = input.length();
        if (inputLength == 0) return new NumberToken[]{new NumberToken(BigDecimal.ZERO)};
        int i = 0;

        List<TokenType> tokens = new ArrayList<>(inputLength);
        StringBuilder stringBuffer = new StringBuilder();

        while (i < inputLength) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                stringBuffer.setLength(0);
                boolean dot = false;

                while (i < inputLength) {
                    char cNum = input.charAt(i);

                    if (Character.isDigit(cNum)) {
                        stringBuffer.append(cNum);
                    } else if (cNum == '.') {
                        if (dot) throw new RuntimeException("Multiple dots in number");
                        dot = true;
                        stringBuffer.append(cNum);
                    } else {
                        break;
                    }
                    i++;
                }

                if (stringBuffer.length() == 1 && stringBuffer.charAt(0) == '.') {
                    throw new RuntimeException("A single dot is not a valid number");
                }

                tokens.add(new NumberToken(new BigDecimal(stringBuffer.toString())));
                continue;
            }

            if (isMatrix(c)) {
                tokens.add(new MatrixToken(Matrix.fromSymbol(c)));
                i++;
                continue;
            }

            if (isPrefixChar(c, tokens)) {
                tokens.add(new PrefixToken(Prefix.fromSymbol(c)));
                i++;
                continue;
            }

            if (isSuffixChar(c, tokens) && (i + 1 >= inputLength || !Character.isLetterOrDigit(input.charAt(i + 1)))) {
                tokens.add(new SuffixToken(Suffix.fromSymbol(c)));
                i++;
                continue;
            }

            if (isOperator(c)) {
                tokens.add(new OperatorToken(Operator.fromSymbol(c)));
                i++;
                continue;
            }

            if (isParenthesis(c)) {
                tokens.add(new ParenthesisToken(Parenthesis.fromSymbol(c)));
                i++;
                continue;
            }

            if (c == ',') {
                tokens.add(new CommaToken());
                i++;
                continue;
            }

            if (Character.isLetter(c)) {
                stringBuffer.setLength(0);

                while (i < inputLength) {
                    char cIdentifier = input.charAt(i);
                    if (Character.isLetterOrDigit(cIdentifier) ||cIdentifier == '_') {
                        stringBuffer.append(cIdentifier);
                        i++;
                    } else break;
                }

                tokens.add(new IdentifierToken(stringBuffer.toString()));
                continue;
            }

            throw new RuntimeException("Invalid char: " + c);
        }

        tokens.add(new EndToken());
        return tokens.toArray(TokenType[]::new);
    }

    private static boolean isPrefixChar(char c, List<TokenType> tokenList) {
        if (!isPrefix(c)) return false;

        if (tokenList.isEmpty()) return true;

        TokenType last = tokenList.getLast();

        return switch (last) {
            case PrefixToken ignored -> true;
            case OperatorToken ignored -> true;
            case CommaToken ignored -> true;
            case ParenthesisToken pT when pT.parenthesis() == Parenthesis.OPEN -> true;
            default -> false;
        };
    }

    private static boolean isSuffixChar(char c, List<TokenType> tokenList) {
        if (!isSuffix(c)) return false;

        if (tokenList.isEmpty()) return false;

        TokenType last = tokenList.getLast();

        return switch (last) {
            case NumberToken ignored -> true;
            case IdentifierToken ignored -> true;
            case ParenthesisToken p when p.parenthesis() == Parenthesis.CLOSE -> true;
            default -> false;
        };
    }
}