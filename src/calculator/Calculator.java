package calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static evaluator.Evaluator.evaluate;
import static lexer.Lexer.tokenize;
import static parser.Parser.parse;

public class Calculator {
    public static class CalcResult {
        private final BigDecimal value;

        private CalcResult(BigDecimal value) {
            this.value = value;
        }

        public String format() {
            return value.stripTrailingZeros().scale() <= 0
                    ? value.toBigInteger().toString()
                    : value.stripTrailingZeros().toPlainString();
        }

        public BigDecimal raw() {
            return value;
        }

        public String round(int places) {
            if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative");
            return value.setScale(places, RoundingMode.HALF_UP).toPlainString();
        }

        public String scientific() {
            return value.stripTrailingZeros().toEngineeringString();
        }

        @Override
        public String toString() {
            return format();
        }
    }

    public static CalcResult calc(String input) {
        return new CalcResult(evaluate(parse(tokenize(input)))
        );
    }
}