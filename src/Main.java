import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean devMode = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter expressions to calculate. Type 'help' for help.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("help")) {
                System.out.print("Type dev for devMode\nType esc to exit\nExample:\n> 1 + 1\n2\n");
                continue;
            }

            if (input.equalsIgnoreCase("esc")) {
                System.out.println("Exiting.");
                break;
            }

            if (input.equalsIgnoreCase("dev")) {
                devMode = !devMode;
                System.out.printf("devMode: %b\n", devMode);
                continue;
            }

            try {
                if (devMode) {
                    lexer.Token.TokenType[] tokens = lexer.Lexer.tokenize(input);
                    System.out.println(Arrays.toString(tokens));
                    parser.Node.NodeType node = parser.Parser.parse(tokens);
                    System.out.println(node.toString());
                }

                var result = calculator.Calculator.calc(input);
                System.out.println(result.commas());
            } catch (Exception e) {
                StackTraceElement element = e.getStackTrace()[0];
                System.out.println("Error: " + e.getMessage());
                if (devMode) {
                    System.out.println("At: " + element.getClassName() + "." + element.getMethodName() + " (Line " + element.getLineNumber() + ")");
                }
            }
        }

        scanner.close();
    }
}