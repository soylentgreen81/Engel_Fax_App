package software.oi.engelfax.util;

/**
 * Created by Stefan Beukmann on 17.12.2015.
 */
interface TextPrinter {
    void print(String word);
    void print(char c);
    void printBreak();
    int getLength(String word);
    int getLength(char c);
    @Override
    String toString();
}
