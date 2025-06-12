package evaluator;

import identifier.Functions;
import identifier.Variables;
import lexer.Token;
import parser.Node.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static identifier.Functions.getFunction;

public class Evaluator {
    private static final BigDecimal thousand = BigDecimal.valueOf(1000);
    private static final BigDecimal million = BigDecimal.valueOf(1000000);
    private static final BigDecimal billion = BigDecimal.valueOf(1000000000);
    private static final BigDecimal trillion = BigDecimal.valueOf(1000000000000L);

    private static final Deque<Map<String, BigDecimal>> contextStack = new ArrayDeque<>();
    static {
        contextStack.push(new HashMap<>());
    }

    public static BigDecimal evaluate(NodeType node) {
        return switch (node) {
            case NumberNode n -> n.value();
            case BinaryOperationNode b -> {
                BigDecimal left = evaluate(b.left());
                BigDecimal right = evaluate(b.right());
                yield switch (b.operator()) {
                    case ADD -> left.add(right);
                    case SUB -> left.subtract(right);
                    case MUL -> left.multiply(right);
                    case DIV -> left.divide(right, 20, RoundingMode.HALF_UP);
                    case MOD -> left.remainder(right);
                    case POW -> {
                        if (right.scale() <= 0) yield left.pow(right.intValue());
                        else yield BigDecimal.valueOf(Math.pow(left.doubleValue(), right.doubleValue()));
                    }
                };
            }
            case PrefixOperationNode p -> {
                BigDecimal value = evaluate(p.value());
                yield switch (p.prefix()) {
                    case Token.Prefix.PLUS -> value;
                    case Token.Prefix.MINUS -> value.negate();
                };
            }
            case SuffixOperationNode s -> {
                BigDecimal value = evaluate(s.value());
                yield switch (s.suffix()) {
                    case THOUSAND -> value.multiply(thousand);
                    case MILLION -> value.multiply(million);
                    case BILLION -> value.multiply(billion);
                    case TRILLION -> value.multiply(trillion);
                    case FACTORIAL -> factorial(value);
                };
            }
            case VariableNode v -> resolveVariable(v.variable().identifier());
            case FunctionNode f -> {
                Functions.Function func = getFunction(f.function().identifier());
                List<String> params = func.params();
                List<BigDecimal> args = new ArrayList<>(f.args().size());
                for (NodeType arg : f.args()) {
                    args.add(evaluate(arg));
                }

                if (args.size() != params.size()) {
                    throw new RuntimeException("Function '" + f.function().identifier() + "' expects " + params.size() + " args, got " + args.size());
                }

                Map<String, BigDecimal> scope = new HashMap<>(params.size());
                for (int i = 0; i < params.size(); i++) {
                    scope.put(params.get(i), args.get(i));
                }

                contextStack.push(scope);
                try {
                    yield evaluate(func.body());
                } finally {
                    contextStack.pop();
                }
            }
        };
    }

    private static BigDecimal resolveVariable(String name) {
        for (Map<String, BigDecimal> scope : contextStack) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        if (Variables.includes(name)) {
            return Variables.get(name);
        }
        throw new RuntimeException("Variable not found: " + name);
    }

    private static BigDecimal factorial(BigDecimal value) {
        if (value.signum() < 0 || value.stripTrailingZeros().scale() > 0) {
            throw new ArithmeticException("Factorial only works for positive integers rn");
        }
        if (value.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            throw new RuntimeException("Factorial too large");
        }
        BigDecimal result = BigDecimal.ONE;
        int i = 1;
        while (i <= value.intValue()) {
            result = result.multiply(BigDecimal.valueOf(i));
            i++;
        }
        return result;
    }
}