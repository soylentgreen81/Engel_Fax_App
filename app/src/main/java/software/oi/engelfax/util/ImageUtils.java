package software.oi.engelfax.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.IOException;

/**
 * Created by Stefan Beukmann on 24.01.2016.
 */
public abstract class ImageUtils {
    public static String toString(BitSet bits){
        byte[] result = bits.toByteArray();
        byte[] reversed = result;
        for (int i=0;i<result.length;i++) {

            reversed[i] = (byte)(Integer.reverse(result[i]) >>> (Integer.SIZE - Byte.SIZE));

        }
        return new String(Base64.encode(reversed, Base64.NO_WRAP));
    }
    public static Bitmap scaleMonochrome(Context context, Uri imagepath, final int width, final int height){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imagepath);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 24, 24, false);
            scaledBitmap.setHasAlpha(false);
            for (int y=0;y<height;y++){
                for (int x=0;x<width;x++){

                    int pixel = scaledBitmap.getPixel(x, y);
                    int R = Color.red(pixel);
                    int G = Color.green(pixel);
                    int B = Color.blue(pixel);

                    int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                    scaledBitmap.setPixel(x, y, gray);
                }
            }
            return scaledBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
