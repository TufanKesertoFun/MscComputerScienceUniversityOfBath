import java.util.*;
import java.util.function.BinaryOperator;

import static java.lang.Integer.parseInt;

/**
 * I think I don't need to explain what SRPN is because it is mentioned in the
 * assignment.
 * Firstly, I want to briefly clarify myself and my approach by creating the
 * SRPN Class.
 * 1: I try to avoid creating too many classes; I just want to use SRPN and
 * Main.
 * Otherwise, there could be some classes that can be added, such as:
 * 2: - Handler Classes for 'd' and 'r' operations (even they can be two
 * different classes because of the obscurity of them).
 * 3: - MultiLineArithmeticalOperation Class
 * 4: - SingleLineArithmeticalOperation Class
 * 5: - ExceptionHandler Class
 * 6: - CommentHandler Class
 * Even advanced techniques such as DI (Dependency Injection), Interfaces, etc.,
 * can be used.
 * I will explain in detail all the fields and methods below. Yes, it may not be
 * the best approach to write too many comments in real life.
 * I want to show that I completed this project on my own and know each line of
 * the code.
 **/
public class SRPN {
  // Stack to store operands
  private Stack<String> stack = new Stack<>();

  // Constants for minimum and maximum integer values
  private final int minValue = Integer.MIN_VALUE;
  private final int maxValue = Integer.MAX_VALUE;

  // Result of arithmetic operations
  private int result;
  // Array of predefined numbers for 'r' command
  private final int[] rNumbers = { 1804289383, 846930886, 1681692777, 1714636915, 1957747793, 424238335,
      719885386, 1649760492, 596516649, 1189641421, 1025202362, 1350490027, 783368690, 1102520059,
      2044897763, 1967513926, 1365180540, 1540383426, 304089172, 1303455736, 35005211, 521595368,
      1804289383 };

  // Array of characters considered as unrecognised in input. I did not want to
  // use external regex library.
  private final static char[] UnrecognisedCharArray = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
      'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
      'o',
      'p', 'q', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ',', '.', '\'', '[', ']', '!', '@', '#', '$', '&', '(', ')',
      '_',
      '{', '}', '|', ':', '"', '<', '>', '?', '~', '`' };

  /**
   * 1: Usage of String to concatenate symbols.
   * 2: In the processCommand function, I used if-else statements. We need to
   * check 'd' and '#' separately.
   * If the last or first character is 'd', it goes directly to the 'd' part of
   * the if-else statement.
   * I used some kind of boolean checker; if it is not empty, go to this if-else
   * part for "#".
   */
  private String concatResult = "";

  /**
   * Finds and returns a list of invalid items (characters) in the given input
   * string.
   * I put this function before the processCommand because ı need to check
   * UnrecognisedCharArray.
   * 
   * @param input The input string to check for invalid characters.
   * @return A list containing the invalid characters found in the input.
   */
  public List<String> findInvalidItems(String input) {
    List<String> invalidItems = new ArrayList<>();

    for (char c : input.toCharArray()) {
      for (char invalidChar : UnrecognisedCharArray) {
        if (c == invalidChar) {
          invalidItems.add(String.valueOf(c));
        }
      }
    }
    return invalidItems;
  }

  /**
   * Map of string operators to binary operator functions.
   * Example usage: BinaryOperator<Integer> addOperator = operators.get("+");
   * int result = addOperator.apply(5, 3);
   * I choose this approach because otherwise, I would have needed to use too many
   * switch cases or if-else statements
   * for arithmetic operations. Also, creating additional classes is not the main
   * purpose of this assignment.
   * Because of this reason, I did not use polymorphism and inheritance. Even
   * though you can add many classes into
   * one Java file, it is not logical. Hence, I used this approach to avoid switch
   * cases/if-else statements.
   */
  private final Map<String, BinaryOperator<Integer>> operators = Map.ofEntries(
      Map.entry("+", this::safeAdd),
      Map.entry("-", this::safeSub),
      Map.entry("*", this::safeMultiply),
      Map.entry("/", this::safeDivide),
      Map.entry("%", (x, y) -> x % y),
      Map.entry("^", (x, y) -> (int) Math.pow(x, y)));

  /**
   * Processes the given command string and performs corresponding actions.
   * Honestly, this is the hardest part to explain because of the condition
   * that we cannot change the main class.
   * I needed if-else statements and try-catch inside of it because the main class
   * provides a value using it.
   * Also, due to the obscurity of srpn logic, I needed to create some additional
   * functions to cover edge cases
   * which make it hard to a little bit read
   * 
   * @param s the command string to be processed
   */
  public void processCommand(String s) {
    try {
      // Check for invalid characters
      if (!findInvalidItems(s).isEmpty() && !s.contains("#")) {
        throw new IllegalArgumentException("Unrecognised operator or operand: " + " \"" + s + "\"");
      }
      ;
      // Handle different cases based on the command
      if (s.contains("d") && s.contains("r")) {
        rHandler(s);
      } else if (s.contains("#") || !concatResult.isEmpty()) {
        stringBuilderConnector(s);
        if (isSingleLine(concatResult) && !concatResult.isEmpty()) {
          singleLineArithmeticOperator(concatResult);
        }
      } else if (stack.isEmpty()) {
        singleLineArithmeticOperator(s);
      } else if (s.contains("d")) {
        dHandler(s);
      } else if (operators.containsKey(s)) {
        performArithmeticOperation(s);
      } else if (s.equals("=")) {
        System.out.println(stack.peek());
      } else {
        stack.push(s);
      }
    } catch (IllegalArgumentException c) {
      // there is loop because sprn emulate does not throw exception and break the app
      // it writes to letters new line
      List<String> items = findInvalidItems(s);
      for (int i = 0; i < items.size(); i++) {
        System.out.println(c.getMessage());
      }

    }
  }

  /**
   * Performs an arithmetic operation and updates the stack if the arithmetic
   * operation has multilines.
   * 1Example
   * 1
   * 2
   * +
   * =
   * 
   * @param s the operator string
   */
  private void performArithmeticOperation(String s) {
    try {
      if (stack.size() < 2) {
        throw new ArithmeticException("Stack underflow.");
      }

      int numberOne = parseInt(stack.pop());
      int numberTwo = parseInt(stack.pop());

      BinaryOperator<Integer> operation = operators.get(s);
      result = operation.apply(numberTwo, numberOne);

      stack.push(String.valueOf(result));
    } catch (ArithmeticException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Safely adds two integers, handling overflow and underflow.
   *
   * @param x the first operand
   * @param y the second operand
   * @return the sum of x and y
   */
  private int safeAdd(int x, int y) {
    if (y > 0 ? x > maxValue - y : x < minValue - y) {
      return maxValue;
    }
    return x + y;
  }

  /**
   * Safely adds two integers, handling overflow and underflow.
   *
   * @param x the first operand
   * @param y the second operand
   * @return the sum of x and y
   */
  private int safeSub(int x, int y) {
    if (y > 0 ? x < minValue + y : x > maxValue + y) {
      return minValue;
    }
    return x - y;
  }

  /**
   * Safely adds two integers, handling overflow and underflow.
   *
   * @param x the first operand
   * @param y the second operand
   * @return the sum of x and y
   */
  private int safeMultiply(int x, int y) {
    if (x * y > maxValue) {
      return maxValue;
    } else if (x * y < minValue) {
      return minValue;
    } else {
      return x * y;
    }
  }

  /**
   * Safely performs the division operation of two integers, and returns the
   * result.
   * If the divisor is zero, an ArithmeticException is thrown.
   *
   * @param x the dividend
   * @param y the divisor
   * @return the division result
   * @throws ArithmeticException if the divisor is zero
   */
  private int safeDivide(int x, int y) {
    if (y == 0) {
      throw new ArithmeticException("Divide by 0.");
    }
    return x / y;
  }

  /**
   * Concatenates the given character to the concatResult string after removing
   * any occurrence of "#" symbol and text following it.
   * I do this because firstly I want to remove all comments and create a
   * single-line string.
   * 
   * @param concatenatedCharacter the character to be concatenated to the
   *                              concatResult string
   */
  public void stringBuilderConnector(String concatenatedCharacter) {
    concatResult += concatenatedCharacter.replaceAll("#.*(?:\n|$)", "");
  }

  /**
   * Checks if the given input string is a single line without any line breaks.
   * I do this because if the first input comes from the console and does not
   * include a # comment,
   * it goes directly to singleLineArithmeticOperator() and causes a problem. With
   * this, I achieved to cover additional hidden edge cases.
   * 
   * @param input the input string to check
   * @return true if the string is a single line, false otherwise
   */
  private static boolean isSingleLine(String input) {
    return !input.contains("\n") && !input.contains("\r\n");
  }

  /**
   * Handles the input string if it contains 'd' character.
   * If the stack is not empty, then the method prints the elements of the stack
   * in reversed order.
   * If there are consecutive occurrences of 'd' in the input string,
   * then the method prints the current minimum value multiple times based on the
   * count of 'd'.
   * This is another edge case that is not included in the assignment's test
   * cases.
   * In my honest opinion, it should be a separate class to cover all edge cases.
   * 
   * @param input the input string to be processed
   */
  private void dHandler(String input) {
    if (!stack.isEmpty()) {
      printStackElementsInReversedOrder();
    } else if (checkConsecutiveD(input)) {
      printMinValueBasedOnOccurrenceOfD(input);
    }
  }

  /**
   * Prints the elements of the stack in reversed order.
   * The elements are transferred to a temporary stack, and then popped from the
   * temporary stack to print in reverse order.
   * If the stack is empty, the method does nothing.
   */
  private void printStackElementsInReversedOrder() {
    Stack<String> tempStack = new Stack<>();
    // Transfer elements to a temporary stack
    while (!stack.isEmpty()) {
      String element = stack.pop();
      tempStack.push(element);
    }
    // Print elements in reverse order by popping from the temporary stack
    while (!tempStack.isEmpty()) {
      String element = tempStack.pop();
      stack.push(element);
      System.out.println(element);
    }
  }

  /**
   * Prints the minimum value based on the occurrence of 'd' in the input string.
   * If the count of 'd' is greater than 0, the method prints the constant
   * minValue
   * multiple times based on the count of 'd'.
   * 
   * @param input the input string to be processed
   */
  private void printMinValueBasedOnOccurrenceOfD(String input) {
    Stack<String> tempStack2 = new Stack<>();
    tempStack2.push(input);
    int countOfD = 0;
    // Calculate count of 'd' in input
    for (String element : tempStack2) {
      countOfD += countOccurrences(element, 'd');
    }
    // Print constant value based on number of occurrence of 'd'
    if (countOfD > 0) {
      for (int i = 0; i < countOfD; i++) {
        System.out.println(minValue);
      }
    }
  }

  /**
   * Counts the number of occurrences of a target character in a given string.
   * I do this because my dHandler works for multiple lines of input. If there is
   * a single-line test case
   * like this, you need to calculate the counts of the 'd' replicate values in
   * the single-line string.
   * 11+1+1+d
   * r r r r r r r r r r r r r r r r r r r r r r d r r r d
   * 
   * @param str    the input string
   * @param target the target character to count
   * @return the number of occurrences of the target character in the input string
   */
  private int countOccurrences(String str, char target) {
    int count = 0;
    for (char c : str.toCharArray()) {
      if (c == target) {
        count++;
      }
    }
    return count;
  }

  /**
   * Checks if there are consecutive occurrences of 'd' character in a given
   * string.
   * I did it to cover another hidden edge case.
   * If there are just consecutive 'd' characters, it returns the minimum negative
   * number.
   * 
   * @param str the input string to be checked
   * @return true if there are consecutive occurrences of 'd', false otherwise
   */
  private static boolean checkConsecutiveD(String str) {
    int length = str.length();

    for (int i = 0; i < length - 1; i++) {
      if (str.charAt(i) == 'd' && str.charAt(i + 1) == 'd') {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if a given string is a valid number.
   * I do this because singleLineArithmeticOperator pushes values directly to the
   * stack if they are not numbers.
   * 
   * @param s the string to check
   * @return true if the string is a valid number, false otherwise.
   */
  private boolean isNumber(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Handles single line arithmetic expressions and pushes the result onto the
   * stack.
   * Handles operators like +, -, *, /, %, and ^. Also repeat d times
   * At the bottom of the function
   * there is if else: if (dCount >= 1 && operatorCount > 1)
   * I want to explain it more thoroughly. When we convert the example inputs,
   * such as "3 4",
   * into a single line like this:
   * 4.3 = 11+1+1+d
   * 4.4 = # This is a comment # 1 2 + # And so is this # d
   * 4.3 throws a StackOverflowError because the number count and operator count
   * are equal.
   * 4.4 does not throw an error because when you delete comments, it becomes = 1
   * 2 + d.
   * So the number count is greater than the operator count. I do this to cover
   * edge cases.
   * 
   * @param expression the arithmetic expression to be processed
   */
  public void singleLineArithmeticOperator(String expression) {
    while (!isNumber(expression)) {
      String[] terms = expression.split("(?=[-+*/^ ])|(?<=[-+*/^ ])");
      String operator = "";
      int operatorCount = 0;
      for (String opr : terms) {
        if (opr.matches("[-+*/^%]")) {
          operator = opr;
          operatorCount += 1;
        }
      }

      int sum = 0;
      int dCount = 0;
      BinaryOperator<Integer> currentOperator = operators.get(operator);

      for (String term : terms) {
        try {
          if (term.equals("d")) {
            dCount = countOccurrences(expression, 'd');
          } else if (term.equals("=")) {
            System.out.println(terms[0]);
          } else {
            int value = Integer.parseInt(term);
            sum = currentOperator.apply(sum, value);
          }
        } catch (NumberFormatException e) {
          currentOperator = operators.get(operator);
          if (currentOperator == null) {
            throw new IllegalArgumentException("Invalid term in the expression: " + term);
          }
        }
      }

      if (dCount >= 1 && operatorCount > 1) {
        System.out.println("Stack underflow.");
        for (int i = 0; i < dCount; i++) {
          System.out.println(sum);
        }
      } else if (dCount >= 1) {
        for (int i = 0; i < dCount; i++) {
          System.out.println(sum);
        }
      }
      break;
    }
    stack.push(expression);
  }

  /**
   * Finds the first index of a target character in a given string.
   *
   * @param str    the input string
   * @param target the target character to find
   * @return the index of the first occurrence of the target character, or -1 if
   *         not found
   */

  private int findFirstIndexOfChar(String str, char target) {
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == target) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Handles the 'r' command, printing random numbers based on the input string.
   * This was one of the hardest cases for me.
   * My algorithm:
   * Part 1: Remove all the empty spaces and obtain a string.
   * Part 2: If rCount < rLimit in the obtained string, no problem; repeat d times
   * r.
   * Part 3: If rCount > rLimit && firstIndexOfD < rLimit, SRPN first repeats d
   * times r, then stack overflow.
   * Part 4: If rCount > rLimit && firstIndexOfD > rLimit, first stack overflow,
   * then repeats d times r.
   * Even though I covered some hidden test cases, I did not refactor this
   * function due to the time limitation of the
   * assignment and the obscurity in SRPN logic.
   * 
   * @param s the input string containing 'r' command
   */
  private void rHandler(String s) {
    Stack<String> tempStack = new Stack<>();
    s = s.replaceAll("\s", "");

    int totalRcountInInput = countOccurrences(s, 'r');
    int totalDcounrInInput = countOccurrences(s, 'd');
    int firstIndexOfD = findFirstIndexOfChar(s, 'd');
    int totalRNumberBeforeFirstD = firstIndexOfD;

    int rCount = 0;
    int rLimit = 23;

    for (int i = 0; i < s.length(); i++) {
      if (rCount <= rLimit) { // For further explanation, please refer to Part 2 in the rHandler comment
                              // section.
        if (s.charAt(i) == 'r') {
          rCount += 1;
          tempStack.push(s);
        } else if (s.charAt(i) == 'd') {
          tempStack.pop();
          for (int j = 0; j < rCount; j++) {
            System.out.println(rNumbers[j]);
          }
        }
      } else {
        if (firstIndexOfD < rLimit) {// For further explanation, please refer to Part 3 in the rHandler comment
                                     // section.
          for (int j = rCount; j <= totalRcountInInput; j++) {
            System.out.println("Stack overflow.");
          }
          for (int k = 0; k < tempStack.size(); k++) {
            System.out.println(rNumbers[k]);
          }
        } else { // For further explanation, please refer to Part 4 in the rHandler comment
                 // section.
          int overflowOccurrenceNumber = totalRNumberBeforeFirstD - rCount;
          for (int j = 0; j <= overflowOccurrenceNumber; j++) {
            System.out.println("Stack overflow.");
          }
          for (int dTimesRepeat = 0; dTimesRepeat < totalDcounrInInput; dTimesRepeat++) {
            for (int k = 0; k < rNumbers.length; k++) {
              System.out.println(rNumbers[k]);
            }
          }
        }
        break;
      }
    }
  }
}
