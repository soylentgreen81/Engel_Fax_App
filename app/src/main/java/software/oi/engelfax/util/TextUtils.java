package software.oi.engelfax.util;

/**
 * Created by stefa_000 on 16.12.2015.
 */
public abstract class TextUtils {

    public static String wordWrap(String input, final int lineWidth){
        String[] lines = input.replace("\r\n", "\n").replace("\r", "\n").split("\n");
        StringBuffer result = new StringBuffer();
        for (int l = 0;l<lines.length;l++){
            String line = lines[l];
            String[] words = line.split("[ \t]");
            int counter = 0;
            for (int i=0;i<words.length;i++){
                String word = words[i];
                int length = word.length();
                if (counter + length <=lineWidth) {
                    counter += length;
                    result.append(word);
                    if (i<words.length-1 && (counter+words[i+1].length()) <=lineWidth){
                        result.append(" ");
                        counter++;
                    }
                }
                else {
                    if (length <= lineWidth) {
                        result.append("\n");
                        result.append(word);
                        counter = length;
                        if (i<words.length-1 && counter <lineWidth){
                            result.append(" ");
                            counter++;
                        }
                    } else {
                        if (i>0)
                            result.append("\n");
                        int count = (int) Math.ceil((double)word.length() / lineWidth);
                        for (int j=0;j<count; j++){
                            int start = j*lineWidth;
                            int end = ((j+1)*lineWidth);
                            if (start+end > word.length())
                                end = word.length();
                            result.append(word.substring(start,end ));
                            if (j+1<count)
                                result.append("\n");
                        }
                    }
                }

            }
            if (l+1<lines.length)
                result.append("\n");
        }
        return result.toString();
    }
}
