package software.oi.engelfax;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Stefan Beukmann on 27.01.2016.
 */
public class AsciiBitmapTests  {

    @Test
    public void test1BitDraw() throws Exception {
        AsciiBitmap bitMap = new AsciiBitmap.Builder()
                                    .setHeight(2)
                                    .setWidth(2)
                                    .setAlphabet(' ', '#')
                                    .build();
        bitMap.drawChar(0,0,'#');
        bitMap.drawChar(1,1,'#');
        assertEquals("# \n #", bitMap.toString());
    }
    @Test
    public void testInvert1BitDraw() throws Exception {
        AsciiBitmap bitMap = new AsciiBitmap.Builder()
                .setHeight(2)
                .setWidth(2)
                .setAlphabet(' ', '#')
                .build();
        bitMap.drawChar(0,0,'#');
        bitMap.drawChar(1,1,'#');
        bitMap.invert();
        assertEquals(" #\n# ", bitMap.toString());
    }
    @Test
    public void test2BitDraw() throws Exception {
        AsciiBitmap bitMap =  new AsciiBitmap.Builder()
                .setHeight(1)
                .setWidth(4)
                .setBitDepth(2)
                .setAlphabet(' ', '.', '+', '#')
                .build();
        bitMap.drawChar(0,0,' ');
        bitMap.drawChar(1,0,'.');
        bitMap.drawChar(2,0,'+');
        bitMap.drawChar(3, 0, '#');
        assertEquals(" .+#", bitMap.toString());
    }
    @Test
    public void testInvert2Bit() throws Exception {
        AsciiBitmap bitMap =  new AsciiBitmap.Builder()
                .setHeight(1)
                .setWidth(4)
                .setBitDepth(2)
                .setAlphabet(' ', '.', '+', '#')
                .build();
        bitMap.drawChar(0,0,' ');
        bitMap.drawChar(1,0,'.');
        bitMap.drawChar(2,0,'+');
        bitMap.drawChar(3,0,'#');
        bitMap.invert();
        assertEquals("#+. ", bitMap.toString());
    }
    @Test
    public void testIncompleteAlphabet() throws Exception {
        AsciiBitmap bitMap =  new AsciiBitmap.Builder()
                .setHeight(2)
                .setWidth(4)
                .setBitDepth(2)
                .setAlphabet(' ', '.')
                .build();
        bitMap.drawChar(0,0,' ');
        bitMap.drawChar(1,0,'.');
        bitMap.drawChar(2,0,' ');
        bitMap.drawChar(3,0,'.');
        assertEquals(" . .\n    ", bitMap.toString());
    }
    @Test
    public void testInvalidParameter()  {
        try {
            AsciiBitmap bitMap = new AsciiBitmap.Builder()
                    .setBitDepth(1)
                    .setAlphabet(' ', '.', '+', '#')
                    .build();
            fail();
        } catch(IllegalArgumentException ex){
        }
    }
}

