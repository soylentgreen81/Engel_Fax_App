package software.oi.engelfax;

import junit.framework.TestCase;

import software.oi.engelfax.util.TextUtils;

import static junit.framework.Assert.assertEquals;

/**
 * Created by stefa_000 on 27.01.2016.
 */
public class AsciiBitmapTests extends TestCase {

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

