package lexer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Token {
    public sealed interface TokenType
            permits NumberToken, OperatorToken, PrefixToken, SuffixToken,
            ParenthesisToken, IdentifierToken, MatrixToken, CommaToken, EndToken {
        String toString();
    }

    public record NumberToken(BigDecimal value) implements TokenType {
        @Override
        public String toString() {
            return value.toString() + " NumberToken";
        }
    }

    public record MatrixToken(BigDecimal[][] matrix) implements TokenType {
        @Override
        public String toString() {
            return "Matrix";
        }
    }

    public enum Operator {
        ADD('+'),
        SUB('-'),
        MUL('*'),
        DIV('/'),
        MOD('%'),
        POW('^');

        private final char operatorSymbol;

        Operator(char symbol) {
            this.operatorSymbol = symbol;
        }

        public char getOperatorSymbol() {
            return operatorSymbol;
        }

        private static final Map<Character, Operator> MAP = new HashMap<>();
        static {
            for (Operator op : values()) {
                MAP.put(op.getOperatorSymbol(), op);
            }
        }

        public static Operator fromSymbol(char symbol) {
            return MAP.get(symbol);
        }

        public static boolean isOperator(char c) {
            return MAP.containsKey(c);
        }

        @Override
        public String toString() {
            return String.valueOf(operatorSymbol);
        }
    }

    public record OperatorToken(Operator operator) implements TokenType {
        @Override
        public String toString() {
            return operator.toString() + " OperatorToken";
        }
    }

    public enum Prefix {
        PLUS('+'),
        MINUS('-');

        private final char prefixSymbol;

        Prefix(char symbol) {
            this.prefixSymbol = symbol;
        }

        public char getPrefixSymbol() {
            return prefixSymbol;
        }

        private static final Map<Character, Prefix> MAP = new HashMap<>();
        static {
            for (Prefix p : values()) {
                MAP.put(p.getPrefixSymbol(), p);
            }
        }

        public static Prefix fromSymbol(char symbol) {
            return MAP.get(symbol);
        }

        public static boolean isPrefix(char c) {
            return MAP.containsKey(c);
        }

        @Override
        public String toString() {
            return String.valueOf(prefixSymbol);
        }
    }

    public record PrefixToken(Prefix prefix) implements TokenType {
        @Override
        public String toString() {
            return prefix.toString() + " PrefixToken";
        }
    }

    public enum Suffix {
        THOUSAND('k'),
        MILLION('m'),
        BILLION('b'),
        TRILLION('t'),
        FACTORIAL('!');

        private final char suffixSymbol;

        Suffix(char symbol) {
            this.suffixSymbol = symbol;
        }

        public char getSuffixSymbol() {
            return suffixSymbol;
        }

        private static final Map<Character, Suffix> MAP = new HashMap<>();
        static {
            for (Suffix s : values()) {
                MAP.put(s.getSuffixSymbol(), s);
            }
        }

        public static Suffix fromSymbol(char symbol) {
            return MAP.get(symbol);
        }

        public static boolean isSuffix(char c) {
            return MAP.containsKey(c);
        }

        @Override
        public String toString() {
            return String.valueOf(suffixSymbol);
        }
    }

    public record SuffixToken(Suffix suffix) implements TokenType {
        @Override
        public String toString() {
            return suffix.toString() + " SuffixToken";
        }
    }

    public enum Parenthesis {
        OPEN('('),
        CLOSE(')');

        private final char parenthesisSymbol;

        Parenthesis(char symbol) {
            this.parenthesisSymbol = symbol;
        }

        public char getParenthesisSymbol() {
            return parenthesisSymbol;
        }

        private static final Map<Character, Parenthesis> MAP = new HashMap<>();
        static {
            for (Parenthesis p : values()) {
                MAP.put(p.getParenthesisSymbol(), p);
            }
        }

        public static Parenthesis fromSymbol(char symbol) {
            return MAP.get(symbol);
        }

        public static boolean isParenthesis(char c) {
            return MAP.containsKey(c);
        }

        @Override
        public String toString() {
            return String.valueOf(parenthesisSymbol);
        }
    }

    public record ParenthesisToken(Parenthesis parenthesis) implements TokenType {
        @Override
        public String toString() {
            return parenthesis.toString() + " ParenthesisToken";
        }
    }

    public record IdentifierToken(String identifier) implements TokenType {
        @Override
        public String toString() {
            return identifier + " IdentifierToken";
        }
    }

    public record CommaToken() implements TokenType {
        @Override
        public String toString() {
            return ", CommaToken";
        }
    }

    public record EndToken() implements TokenType {
        @Override
        public String toString() {
            return "END";
        }
    }
}