package software.oi.engelfax;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.util.HashMap;
import java.util.Map;

import software.oi.engelfax.util.BitSet;
import software.oi.engelfax.util.BitUtils;

/**
 * Wrapper class for a java.util.BitSet for Ascii-Bitmaps
 * with variable bit-depth
 *
 * Created by Stefan Beukmann on 27.01.2016.
 */
public class AsciiBitmap implements Parcelable{
    private final int bitdepth;
    private final int width;
    private final int height;
    private char[] alphabet;
    private Map<BitSet, Character> charMap;
    private Map<Character, BitSet> inverseCharMap;
    private final int maxValue;
    private final BitSet bits;

    private AsciiBitmap(Builder builder){
        this.bitdepth = builder.bitdepth;
        this.width = builder.width;
        this.height = builder.height;
        this.alphabet = builder.alphabet;
        bits = new BitSet(width*bitdepth* height);
        maxValue = (int) Math.pow(2,bitdepth)-1;
        buildMaps();
    }
    private AsciiBitmap(Parcel parcel){
        bitdepth = parcel.readInt();
        width = parcel.readInt();
        height = parcel.readInt();
        byte[] bytes = new byte[bitdepth*width*height/8];
        parcel.readByteArray(bytes);
        bits = BitSet.valueOf(bytes);
        maxValue = (int) Math.pow(2,bitdepth)-1;
        buildMaps();
    }
    private void buildMaps(){
        charMap = new HashMap<>(alphabet.length);
        inverseCharMap = new HashMap<>(alphabet.length);
        for (int i=0;i<alphabet.length;i++){
            BitSet set = BitUtils.toBitSet(i, bitdepth);
            char c = alphabet[i];
            if (!inverseCharMap.containsKey(c)) {
                charMap.put(set, c);
                inverseCharMap.put(c, set);
            }
        }
    }
    public void loadBitmap(Bitmap bitmap, int brightness){

        for (int y=0;y<height;y++){
            for (int x=0;x<width;x++) {
                int pixel = bitmap.getPixel(x, y);
                int v = getBrightness(pixel);
                double relV = Math.min((double) v * brightness / 128.0, 255.0);
                int index = alphabet.length - (int) ((relV) / 255.0 * alphabet.length);
                index = Math.min(alphabet.length-1, index);
                this.drawChar(x,y, alphabet[index] );
            }
        }
    }
    public String getAlphabet(){
        return new String(alphabet);
    }
    private int getBrightness(int pixel) {
        int r = Color.green(pixel);
        int g = Color.red(pixel);
        int b = Color.blue(pixel);
        return  Math.max(b, Math.max(r, g));
    }
    public void clear() {
        bits.clear();
    }
    public void invert() {
        bits.flip(0, height * bitdepth * width);
    }


    public void drawChar(int x, int y, char character){
        BitSet charBits = inverseCharMap.get(character);
        setBits(x, y, charBits);
    }
    private void setBits(int x, int y, BitSet charBits){
        int offset = getOffset(x, y);
        for (int i=0;i<bitdepth;i++){
            bits.set(offset+i, charBits.get(i));
        }

    }
    private BitSet getBits(int x, int y){
        int offset = getOffset(x,y);
        return bits.get(offset, offset + bitdepth);
    }
    public void brush(int x, int y){
        //Mittelpunkt
        increment(x, y, 3);
        //Direkte Nachbarn
        increment(x-1, y, 1);
        increment(x+1, y, 1);
        increment(x, y+1, 1);
        increment(x, y-1, 1);
        //Diagonale Nachbarn
        /*
        increment(x-1, y-1, 1);
        increment(x-1, y+1, 1);
        increment(x+1, y-1, 1);
        increment(x+1, y+1, 1);
        */
    }
    public void erase(int x, int y){
        //Mittelpunkt
        decrement(x, y, 3);
        //Direkte Nachbarn
        decrement(x-1, y, 1);
        decrement(x+1, y, 1);
        decrement(x, y+1, 1);
        decrement(x, y-1, 1);
        //Diagonale Nachbarn
        /*
        decrement(x-1, y-1, 1);
        decrement(x-1, y+1, 1);
        decrement(x+1, y-1, 1);
        decrement(x+1, y+1, 1);
        */
    }
    private void increment(int x, int y, int count){
        if (outOfBounds(x, y)){
            return;
        }
        BitSet charBits = getBits(x, y);
        final int initValue = BitUtils.toInteger(charBits,bitdepth);
        for (int i=initValue;i<Math.min(initValue+count,maxValue);i++){
            BitUtils.increment(charBits, bitdepth);
        }
        setBits(x, y, charBits);
    }
    private void decrement(int x, int y, int count){
        if (outOfBounds(x, y)){
            return;
        }
        BitSet charBits = getBits(x, y);
        final int initValue = BitUtils.toInteger(charBits,bitdepth);
        for (int i=initValue;i>Math.max(initValue-count,0);i--){
            BitUtils.decrement(charBits, bitdepth);
        }
        setBits(x, y, charBits);
    }

    private boolean outOfBounds(int x, int y){
        return x < 0 || x >= width || y < 0 || y >= height;
    }
    private int getOffset(int x, int y){
        return  (y* width * bitdepth) + (x * bitdepth);
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        for (int y=0;y<height;y++) {

            for (int x = 0; x < width; x++) {
                result.append(getChar(x, y));
            }
            if (y!=height - 1)
                result.append("\n");
        }
        return result.toString();
    }
    public char getChar(int x, int y){
        int offset = getOffset(x,y);
        BitSet charSet = bits.get(offset, offset + bitdepth);
        return charMap.get(charSet);
    }
    public String toBase64(){
        byte[] result = bits.toByteArray();
        byte[] reversed = result;
        for (int i=0;i<result.length;i++) {
            reversed[i] = (byte)(Integer.reverse(result[i]) >>> (Integer.SIZE - Byte.SIZE));
        }
        return new String(Base64.encode(reversed, Base64.NO_WRAP));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bitdepth);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeByteArray(bits.toByteArray());
    }
    public static final Creator<AsciiBitmap> CREATOR = new Creator<AsciiBitmap>() {
        @Override
        public AsciiBitmap createFromParcel(Parcel in) {
            return new AsciiBitmap(in);
        }

        @Override
        public AsciiBitmap[] newArray(int size) {
            return new AsciiBitmap[size];
        }
    };
    public static class Builder {
        private int bitdepth = 1;
        private int height = 18;
        private int width = 24;
        private char[] alphabet = new char[]{' ', '#'};

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setAlphabet(char... alphabet) {
            this.alphabet = alphabet;
            return this;
        }

        public Builder setBitDepth(int bitdepth) {
            this.bitdepth = bitdepth;
            return this;
        }

        public AsciiBitmap build() {
            int maxAlphabet = BitUtils.log2(alphabet.length);
            if (maxAlphabet > bitdepth) {
                throw new IllegalArgumentException(String.format("Alphabet is too large (%d) for bit-depth of %d!", alphabet.length, bitdepth));
            }
            return new AsciiBitmap(this);
        }
    }

}
