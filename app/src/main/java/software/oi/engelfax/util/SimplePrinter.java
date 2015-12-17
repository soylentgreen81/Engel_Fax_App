package software.oi.engelfax.util;

/**
 * Created by stefa_000 on 17.12.2015.
 */
public class SimplePrinter implements  TextPrinter{
    private final StringBuffer buffer = new StringBuffer();

    @Override
    public void print(String word) {
        buffer.append(word);
    }

    @Override
    public void print(char c) {
        buffer.append(c);

    }

    @Override
    public void printBreak() {
        buffer.append("\n");
    }

    @Override
    public int getLength(String word) {
        return word.length();
    }

    @Override
    public int getLength(char c) {
        return 1;
    }


    @Override
    public String toString() {
        return buffer.toString();
    }
}
