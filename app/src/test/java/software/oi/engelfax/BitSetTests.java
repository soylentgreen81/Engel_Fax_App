package software.oi.engelfax;

import org.junit.Before;
import org.junit.Test;

import software.oi.engelfax.util.BitSet;
import software.oi.engelfax.util.BitUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stefan Beukmann on 12.02.2016.
 */
public class BitSetTests {
    private BitSet zero;
    private BitSet one;
    private BitSet two;
    private BitSet three;

    @Before
    public void setUp(){
        zero = BitUtils.toBitSet(0, 2);
        one = BitUtils.toBitSet(1, 2);
        two = BitUtils.toBitSet(2, 2);
        three = BitUtils.toBitSet(3, 2);


    }
    @Test
    public void testIncrement(){
        BitSet zeroInc = BitUtils.increment(zero, 2);
        assertEquals(one, zeroInc);
    }
    @Test
    public void testDecrement(){
        BitSet oneDec = BitUtils.decrement(one, 2);
        assertEquals(zero, oneDec);
    }
    @Test
    public void testDecrementTwo(){
        BitSet twoDec= BitUtils.decrement(two, 2);
        assertEquals(one, twoDec);
    }

    @Test
    public void testDecrementThree(){
        BitSet threeDec= BitUtils.decrement(three, 2);
        assertEquals(two, threeDec);
    }
    @Test
    public void testIncrementTwice(){
        BitSet zeroIncInc = BitUtils.increment(BitUtils.increment(zero, 2), 2);
        assertEquals(two, zeroIncInc);
    }
    @Test
    public void testOverflow(){
        BitSet overflow = BitUtils.increment(three, 2);
        assertEquals(zero, overflow);
    }

    @Test
    public void testUnderflow(){
        BitSet underflow = BitUtils.decrement(zero, 2);
        assertEquals(zero, underflow);
    }
}
