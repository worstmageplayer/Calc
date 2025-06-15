package lexer;

import lexer.Token.TokenType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lexer.Token.*;
import static lexer.Token.Operator.isOperator;
import static lexer.Token.Parenthesis.isParenthesis;
import static lexer.Token.Prefix.isPrefix;
import static lexer.Token.Suffix.isSuffix;

public class Lexer {
    public static TokenType[] tokenize(String input) {
        final int inputLength = input.length();
        if (input.trim().isEmpty()) return new NumberToken[]{new NumberToken(BigDecimal.ZERO)};
        int i = 0;

        final List<TokenType> tokens = new ArrayList<>(inputLength);

        while (i < inputLength) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                final int start = i;
                boolean dot = false;

                while (i < inputLength) {
                    c = input.charAt(i);

                    if (Character.isDigit(c)) {
                        i++;
                    } else if (c == '.') {
                        if (dot) throw new RuntimeException("Multiple dots in number");
                        dot = true;
                        i++;
                    } else break;
                }

                String number = input.substring(start, i);
                if (number.equals(".")) throw new RuntimeException("A single dot is not a valid number.");

                tokens.add(Token.numberToken(new BigDecimal(number).stripTrailingZeros()));
                continue;
            }

            if (isPrefixChar(c, tokens)) {
                tokens.add(Token.prefixToken(Prefix.fromSymbol(c)));
                i++;
                continue;
            }

            if (isSuffixChar(c, tokens) && (i + 1 >= inputLength || !Character.isLetterOrDigit(input.charAt(i + 1)))) {
                tokens.add(Token.suffixToken(Suffix.fromSymbol(c)));
                i++;
                continue;
            }

            if (isOperator(c)) {
                tokens.add(Token.operatorToken(Operator.fromSymbol(c)));
                i++;
                continue;
            }

            if (isParenthesis(c)) {
                tokens.add(Token.parenthesisToken(Parenthesis.fromSymbol(c)));
                i++;
                continue;
            }

            if (c == ',') {
                tokens.add(CommaToken.COMMA_TOKEN);
                i++;
                continue;
            }

            if (c == ';') {
                tokens.add(SemiColonToken.SEMI_COLON_TOKEN);
                i++;
                continue;
            }

            if (Character.isLetter(c)) {
                final int start = i++;
                while (i < inputLength) {
                    c = input.charAt(i);
                    if (!Character.isLetterOrDigit(c) && c != '_') break;
                    i++;
                }
                tokens.add(Token.identifierToken(input.substring(start, i)));
                continue;
            }

            throw new RuntimeException("Invalid char: " + c);
        }

        tokens.add(EndToken.END_TOKEN);
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
            case ParenthesisToken p when p.parenthesis() == Parenthesis.OPEN -> true;
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