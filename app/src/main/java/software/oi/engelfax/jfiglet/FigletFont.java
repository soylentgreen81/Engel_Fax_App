package software.oi.engelfax.jfiglet;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

/**
 * FigletFont implementation. A single static method call will create the ascii
 * art in a mulitilne String. FigletFont format is specified at:
 * https://github.com/lalyos/jfiglet/blob/master/figfont.txt
 *
 * <pre>
 * <code>String asciiArt = FigletFont.convertOneLine("hello");</code>
 * </pre>
 *
 * Originally found at: http://www.rigaut.com/benoit/CERN/FigletJava/. Moved to
 * <a href="http://lalyos.github.io/jfiglet/">github.com</a>.
 *
 * @author Benoit Rigaut CERN July 96
 * www.rigaut.com benoit@rigaut.com
 * released with GPL the 13th of november 2000 (my birthday!)
 *
 */
public class FigletFont {
  public char hardblank;
  public int height = -1;
  public int heightWithoutDescenders = -1;
  public int maxLine = -1;
  public int smushMode = -1;
  public char font[][][] = null;
  public String fontName = null;
  final public static int MAX_CHARS = 1024;

   /**
     * Returns all character from this Font. Each character is defined as
     * char[][]. So the whole font is a char[][][].
     *
     * @return The representation of all characters.
     */
  public char[][][] getFont() {
    return font;
  }

    /**
     * Return a single character represented as char[][].
     *
     * @param c
     *            The numerical id of the character.
     * @return The definition of a single character.
     */
  public char[][] getChar(int c) {
      if (c >= 0 && c<font.length)
          return font[c];
      else
          return null;
  }

    /**
     * Selects a single line from a character.
     *
     * @param c Character id
     * @param l Line number
     * @return The selected line from the character
     */
  public String getCharLineString(int c, int l) {
    if (font[c][l] == null)
      return null;
    else {
        String ret = new String(font[c][l]);
        return ret.substring(1);
    }
  }

    /**
     * Creates a FigletFont as specified at: https://github.com/lalyos/jfiglet/blob/master/figfont.txt
     *
     * @param stream
     */
  public FigletFont(InputStream stream) throws IOException {
    font = new char[MAX_CHARS][][];
    DataInputStream data = null;
    String dummyS;
    char dummyC;
    int dummyI;
    int charCode;

    String codeTag;
    try {
      data = new DataInputStream(new BufferedInputStream(stream));

      dummyS = data.readLine();
      StringTokenizer st = new StringTokenizer(dummyS, " ");
      String s = st.nextToken();
      hardblank = s.charAt(s.length() - 1);
      height = Integer.parseInt(st.nextToken());
      heightWithoutDescenders = Integer.parseInt(st.nextToken());
      maxLine = Integer.parseInt(st.nextToken());
      smushMode = Integer.parseInt(st.nextToken());
      dummyI = Integer.parseInt(st.nextToken());

            /*
             * try to read the font name as the first word of the first comment
             * line, but this is not standardized !
             */
      st = new StringTokenizer(data.readLine(), " ");
      if (st.hasMoreElements())
        fontName = st.nextToken();
      else
        fontName = "";

      for (int i = 0; i < dummyI-1; i++) // skip the comments
        dummyS = data.readLine();
      charCode = 31;
      while (dummyS!=null) {  // for all the characters
        //System.out.print(i+":");
        charCode++;
        for (int h = 0; h < height; h++) {
          dummyS = data.readLine();
          if (dummyS != null){
            //System.out.println(dummyS);
            int iNormal = charCode;
            boolean abnormal = true;
            if (h == 0) {
              try {
                  codeTag = dummyS.concat(" ").split(" ")[0];
                  if (codeTag.length()>2&&"x".equals(codeTag.substring(1,2))){
                      charCode = Integer.parseInt(codeTag.substring(2),16);
                  } else {
                      charCode = Integer.parseInt(codeTag);
                  }
              } catch (NumberFormatException e) {
                abnormal = false;
              }
              if (abnormal)
                dummyS = data.readLine();
              else
                charCode = iNormal;
            }
            if (h == 0)
              font[charCode] = new char[height][];
            int t = dummyS.length() - 1 - ((h == height-1) ? 1 : 0);
            if (height == 1)
              t++;
            font[charCode][h] = new char[t];
            for (int l = 0; l < t; l++) {
              char a = dummyS.charAt(l);
              font[charCode][h][l] = (a == hardblank) ? ' ' : a;
            }
          }
        }
      }
    } finally {
        if (data != null) {
            data.close();
        }
    }
  }


    public String convert(String message, final int maxWidth) throws IOException {
        String[] lines = message.replace("\r\n", "\n").replace("\r", "\n").split("\n");
        StringBuffer buffer = new StringBuffer();
        StringBuffer[] lineBuffers = new StringBuffer[this.height];
        for (int i=0;i<this.height;i++){
            lineBuffers[i] = new StringBuffer();
        }
        for (String line : lines) {
            int count = 0;
            for (StringBuffer lineBuffer : lineBuffers){
                lineBuffer.setLength(0);
            }
            for (int c = 0; c < line.length(); c++){
                // for each char
                char[][] aChar = getChar(line.charAt(c));
                if (aChar != null) {
                    int width = aChar[0].length;

                    if (count + width > maxWidth) {

                        for (StringBuffer lineBuffer : lineBuffers) {
                            lineBuffer.append("\n");
                            buffer.append(lineBuffer.toString());
                            lineBuffer.setLength(0);
                        }
                        count = 0;
                    }
                    for (int l = 0; l < this.height; l++) { // for each line
                        lineBuffers[l].append(new String(aChar[l]));
                    }
                    count += width;
                }
            }



            for (StringBuffer lineBuffer : lineBuffers){
                lineBuffer.append("\n");
                buffer.append(lineBuffer.toString());
            }
        }
        return buffer.toString();
    }

}
