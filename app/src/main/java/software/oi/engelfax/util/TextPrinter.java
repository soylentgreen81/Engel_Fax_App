package software.oi.engelfax.util;

/**
 * Created by stefa_000 on 17.12.2015.
 */
public interface TextPrinter {
    public void print(String word);
    public void print(char c);
    public void printBreak();
    public int getLength(String word);
    public int getLength(char c);
    @Override
    public String toString();
}
