package software.oi.engelfax;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stefan Beukmann on 12.02.2016.
 */
public class AsciiBitmapBrushTests {
    private AsciiBitmap theBitmap;
    @Before
    public void setUp(){
        theBitmap=  new AsciiBitmap.Builder()
                .setHeight(5)
                .setWidth(5)
                .setBitDepth(2)
                .setAlphabet('A', 'B', 'C', 'D')
                .build();

    }

    @Test
    public void testCentral(){

        theBitmap.brush(2, 2);
        String expected =   "AAAAA\n"+
                            "AABAA\n"+
                            "ABDBA\n"+
                            "AABAA\n"+
                            "AAAAA";
        assertEquals(expected, theBitmap.toString());
    }

    @Test
    public void testEraserCentral(){
        theBitmap.invert();
        theBitmap.erase(2, 2);
        String expected =   "DDDDD\n"+
                            "DDCDD\n"+
                            "DCACD\n"+
                            "DDCDD\n"+
                            "DDDDD";
        assertEquals(expected, theBitmap.toString());
    }
    @Test
    public void testEraserTwice(){
        theBitmap.invert();
        theBitmap.erase(2, 2);
        theBitmap.erase(2, 2);
        String expected =   "DDDDD\n"+
                            "DDBDD\n"+
                            "DBABD\n"+
                            "DDBDD\n"+
                            "DDDDD";
        assertEquals(expected, theBitmap.toString());
    }
    @Test
    public void testCentralTwice(){

        theBitmap.brush(2, 2);
        theBitmap.brush(2, 2);
        String expected =   "AAAAA\n"+
                            "AACAA\n"+
                            "ACDCA\n"+
                            "AACAA\n"+
                            "AAAAA";
        assertEquals(expected, theBitmap.toString());
    }
    @Test
    public void testUpperLeftCorner(){
        theBitmap.brush(0, 0);
        String expected =   "DBAAA\n"+
                            "BAAAA\n"+
                            "AAAAA\n"+
                            "AAAAA\n"+
                            "AAAAA";
        assertEquals(expected, theBitmap.toString());
    }
    @Test
    public void testUpperRightCorner(){
        theBitmap.brush(4, 0);
        String expected =   "AAABD\n"+
                            "AAAAB\n"+
                            "AAAAA\n"+
                            "AAAAA\n"+
                            "AAAAA";
        assertEquals(expected, theBitmap.toString());
    }
    @Test
    public void testLowerLeftCorner(){
        theBitmap.brush(0, 4);
        String expected =   "AAAAA\n"+
                            "AAAAA\n"+
                            "AAAAA\n"+
                            "BAAAA\n"+
                            "DBAAA";
        assertEquals(expected, theBitmap.toString());
    }

    @Test
    public void testLowerRightCorner(){
        theBitmap.brush(4, 4);
        String expected =   "AAAAA\n"+
                            "AAAAA\n"+
                            "AAAAA\n"+
                            "AAAAB\n"+
                            "AAABD";
        assertEquals(expected, theBitmap.toString());
    }
}
