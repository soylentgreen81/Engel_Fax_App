package software.oi.engelfax.util;

import software.oi.engelfax.jfiglet.FigletFont;

/**
 * Created by stefa_000 on 17.12.2015.
 */
public class FigletPrinter implements TextPrinter{
    private final FigletFont font;
    private final StringBuffer result = new StringBuffer();
    private final StringBuffer[] lineBuffers;
    public FigletPrinter(FigletFont font){
        this.font = font;
        lineBuffers = new StringBuffer[font.height];
        for (int i=0;i<font.height;i++){
            lineBuffers[i] = new StringBuffer();
        }
    }
    @Override
    public void print(String word) {
        for (char c : word.toCharArray()){
           print(c);
        }
    }

    @Override
    public void print(char c) {
        char[][] aChar = font.getChar(c);
        if (aChar != null) {
            for (int l = 0; l < font.height; l++) { // for each line
                lineBuffers[l].append(new String(aChar[l]));
            }
        }
    }

    @Override
    public void printBreak() {
        for (StringBuffer lineBuffer : lineBuffers){
            lineBuffer.append("\n");
            result.append(lineBuffer.toString());
            lineBuffer.setLength(0);
        }
    }

    @Override
    public int getLength(String word) {
        int sum = 0;
        for (char c : word.toCharArray()){
            sum+=getLength(c);
        }
        return sum;
    }

    @Override
    public int getLength(char c) {
        char[][] aChar = font.getChar(c);
        if (aChar != null) {
            return aChar[0].length;
        } else return 0;
    }

    @Override
    public String toString() {
        for (StringBuffer lineBuffer : lineBuffers){
            lineBuffer.append("\n");
            result.append(lineBuffer.toString());
            lineBuffer.setLength(0);
        }
        return result.toString();
    }
}
