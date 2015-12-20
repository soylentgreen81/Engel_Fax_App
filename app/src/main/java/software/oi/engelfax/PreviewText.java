package software.oi.engelfax;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stefa_000 on 20.12.2015.
 */
public class PreviewText implements Parcelable {


    public PreviewText(String code, String title, String text) {
        this.title = title;
        this.code = code;
        this.text = text;
    }
    public final String text;
    public final String title;
    public final String code;

    protected PreviewText(Parcel in) {
        String[] values = new String[3];
        in.readStringArray(values);
        title = values[0];
        code = values[1];
        text = values[2];

    }

    public static final Creator<PreviewText> CREATOR = new Creator<PreviewText>() {
        @Override
        public PreviewText createFromParcel(Parcel in) {
            return new PreviewText(in);
        }

        @Override
        public PreviewText[] newArray(int size) {
            return new PreviewText[size];
        }
    };

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {title, code, text});
    }


}
