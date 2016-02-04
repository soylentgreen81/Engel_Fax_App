package software.oi.engelfax;

import org.junit.Test;

import software.oi.engelfax.util.TextUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stefan Beukmann on 16.12.2015.
 */

public class EngelFaxUnitTests {

    @Test
    public void testSimpleWrap() throws Exception {
        String input = "a b c";
        String expected = "a\nb\nc";
        String result = TextUtils.wordWrap(input, 1);
        assertEquals(expected, result);
    }
    @Test
    public void testLongWordWrap() throws Exception {
        String input = "abc";
        String expected = "a\nb\nc";
        String result = TextUtils.wordWrap(input, 1);
        assertEquals(expected, result);
    }
    @Test
    public void testMixed() throws Exception {
        String input = "aa bbb cc";
        String expected = "aa\nbb\nb\ncc";
        String result = TextUtils.wordWrap(input, 2);
        assertEquals(expected, result);
    }
    @Test
    public void testMultiWords() throws Exception {
        String input = "a b c d e f";
        String expected = "a b c\nd e f";
        String result = TextUtils.wordWrap(input, 5);
        assertEquals(expected, result);
    }
    @Test
    public void testMultiLongWords() throws Exception {
        String input = "a bbbbb c d e";
        String expected = "a\nbbbbb\nc d e";
        String result = TextUtils.wordWrap(input, 5);
        assertEquals(expected, result);
    }
    @Test
    public void testMultiBreaks() throws Exception{
        String input = "a\n\nb";
        String expected = "a\n\nb";
        String result = TextUtils.wordWrap(input, 1);
        assertEquals(expected, result);
    }
}
