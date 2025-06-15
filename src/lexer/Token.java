package lexer;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Token {
    public sealed interface TokenType
            permits NumberToken, OperatorToken, PrefixToken, SuffixToken,
            ParenthesisToken, IdentifierToken, CommaToken, SemiColonToken, EndToken {
        String toString();
    }

    public record NumberToken(BigDecimal value) implements TokenType {
        @Override
        public String toString() {
            return value.toString() + " NumberToken";
        }
    }

    public enum Operator {
        ADD('+', 2, 3),
        SUB('-', 2, 3),
        MUL('*', 4, 5),
        DIV('/', 4, 5),
        MOD('%', 4, 5),
        POW('^', 7, 6);

        private final char operatorSymbol;
        private final int leftBindingPower;
        private final int rightBindingPower;

        Operator(char symbol, int left, int right) {
            this.operatorSymbol = symbol;
            this.leftBindingPower = left;
            this.rightBindingPower = right;
        }

        public char getOperatorSymbol() {
            return operatorSymbol;
        }

        public static int[] getBindingPower(Operator op) {
            return new int[]{op.leftBindingPower, op.rightBindingPower};
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
        public static final CommaToken COMMA_TOKEN = new CommaToken();

        @Override
        public String toString() {
            return ", CommaToken";
        }
    }

    public record SemiColonToken() implements TokenType {
        public static final SemiColonToken SEMI_COLON_TOKEN = new SemiColonToken();

        @Override
        public String toString() {
            return "; SemiColonToken";
        }
    }

    public record EndToken() implements TokenType {
        public static final EndToken END_TOKEN = new EndToken();

        @Override
        public String toString() {
            return "END";
        }
    }

    private final static Map<BigDecimal, NumberToken> numberPool = new HashMap<>();
    private final static Map<String, IdentifierToken> identifierPool = new HashMap<>();
    private final static EnumMap<Operator, OperatorToken> operatorPool = new EnumMap<>(Operator.class);
    private final static EnumMap<Prefix, PrefixToken> prefixPool = new EnumMap<>(Prefix.class);
    private final static EnumMap<Suffix, SuffixToken> suffixPool = new EnumMap<>(Suffix.class);
    private final static EnumMap<Parenthesis, ParenthesisToken> parenthesisPool = new EnumMap<>(Parenthesis.class);

    public static NumberToken numberToken(BigDecimal value) {
        return numberPool.computeIfAbsent(value, NumberToken::new);
    }
    public static IdentifierToken identifierToken(String id) {
        return identifierPool.computeIfAbsent(id.intern(), IdentifierToken::new);
    }
    public static OperatorToken operatorToken(Operator op) {
        return operatorPool.computeIfAbsent(op, OperatorToken::new);
    }
    public static PrefixToken prefixToken(Prefix p) {
        return prefixPool.computeIfAbsent(p, PrefixToken::new);
    }
    public static SuffixToken suffixToken(Suffix s) {
        return suffixPool.computeIfAbsent(s, SuffixToken::new);
    }
    public static ParenthesisToken parenthesisToken(Parenthesis p) {
        return parenthesisPool.computeIfAbsent(p, ParenthesisToken::new);
    }
}