package base;

public class TestData {

    public static String uniqueNumber() {
        String time = String.valueOf(System.currentTimeMillis());
        return time.substring(time.length() - 6);
    }

    public static String uniqueLetters() {
        String numbers = uniqueNumber();
        String letters = "";
        for (int i = 0; i < numbers.length(); i++) {
            int digit = numbers.charAt(i) - '0';
            letters = letters + (char) ('a' + digit);
        }
        return letters;
    }

}
