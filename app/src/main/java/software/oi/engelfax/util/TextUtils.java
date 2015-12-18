package software.oi.engelfax.util;

import org.apache.commons.io.IOUtils;

import java.util.Map;

/**
 * Created by stefa_000 on 16.12.2015.
 */
public abstract class TextUtils {
    public static String wordWrap(String input, final int lineWidth){
        return wordWrap(input, lineWidth, new SimplePrinter());
    }

    public static String wordWrap(String input, final int lineWidth, TextPrinter printer){
        String[] lines = input.replace("\r\n", "\n").replace("\r", "\n").split("\n");
        StringBuffer result = new StringBuffer();
        for (int l = 0;l<lines.length;l++){
            String line = lines[l];
            String[] words = line.split("[ \t]");
            int counter = 0;
            int spaceWidth = printer.getLength(" ");
            for (int i=0;i<words.length;i++) {
                String word = words[i];
                int length = printer.getLength(word);
                if (length <= lineWidth) {
                    if (counter + length > lineWidth) {
                        printer.printBreak();
                        counter = 0;
                    }
                    counter += length;
                    printer.print(word);
                    if (i < words.length - 1 && counter+ printer.getLength( words[i + 1])  <= lineWidth) {
                        printer.print(" ");
                        counter += spaceWidth;
                    }
                }
                else { //Wort ist zu lang für eine Zeile

                    for (int j = 0;j<word.length();j++){
                        char c = word.charAt(j);
                        int charLength = printer.getLength(c);
                        if (charLength + counter >lineWidth) {
                            printer.printBreak();
                            counter = 0;
                        }
                        printer.print(c);
                        counter+=charLength;
                    }

                }


            }
            if (l+1<lines.length)
                printer.printBreak();
        }
        return printer.toString();
    }

}
