package software.oi.engelfax.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import software.oi.engelfax.PreviewText;
import software.oi.engelfax.jfiglet.FigletFont;
import software.oi.engelfax.util.FigletPrinter;
import software.oi.engelfax.util.TextUtils;

/**
 * Retained Fragment that loads the preview-"images" (a.k.a. textstrings)
 * asynchronously. Create via PreviewLoaderFragment.newInstance(String message)
 *
 * Created by Stefan Beukmann on 24.12.2015.
 */
public class PreviewLoaderFragment extends Fragment {
    private static final String TEXT_KEY = "TEXT";
    private final static String TAG = PreviewLoader.class.getSimpleName();

    interface TaskCallbacks{
        void onPreExecute();
        void onPostExecute(ArrayList<PreviewText>  texts);
    }

    private PreviewLoader mTask;
    public static PreviewLoaderFragment newInstance(String text) {
        PreviewLoaderFragment f = new PreviewLoaderFragment();
        Bundle args = new Bundle();
        args.putString(TEXT_KEY, text);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String text = getArguments().getString(TEXT_KEY);
        mTask = new PreviewLoader();
        mTask.execute(text);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    private TaskCallbacks mCallbacks;
    private class PreviewLoader extends AsyncTask<String, Void, ArrayList<PreviewText>> {

        private final String emptyLine = "\n" + TextUtils.padRight("", PreviewActivity.WIDTH);
        private List<PreviewText> readCSV(String path, String csv, String prefix, String text) throws IOException {
            List<PreviewText> previewTexts = new ArrayList<>();

            InputStream in = getActivity().getAssets().open(path + "/" + csv);

            String input = IOUtils.toString(in);
            String wrappedText = "";
            if (prefix.equals(PreviewActivity.COWSAY))
                wrappedText = TextUtils.cowWrap(text, PreviewActivity.WIDTH);
            else if (prefix.equals(PreviewActivity.ASCII_ART))
                wrappedText = TextUtils.wordWrap(text, PreviewActivity.WIDTH);
            IOUtils.closeQuietly(in);
            String[] rawCodes = input.split("\n");
            for (String line : rawCodes){
                String[] items = line.split(";");

                if (items.length == 2) {
                    String finalText = "";
                    switch (prefix){
                        case PreviewActivity.ASCII_ART:
                            try {
                                InputStream is = getActivity().getAssets().open(path + "/" + items[1]);
                                String asciiArt = IOUtils.toString(is);
                                IOUtils.closeQuietly(is);
                                finalText = wrappedText + "\n" + asciiArt;
                            }
                            catch (Exception ex){
                                Log.w(TAG, "Fehler beim Laden des Preview!", ex);
                                finalText = ex.getMessage();
                            }
                            break;
                        case PreviewActivity.FIGLET:
                            try {
                                InputStream is = getActivity().getAssets().open(path + "/" + items[1] + ".flf");
                                finalText = TextUtils.wordWrap(text, 24, new FigletPrinter(new FigletFont(is)));
                                IOUtils.closeQuietly(is);
                            }
                            catch (Exception ex){
                                Log.w(TAG, "Fehler beim Laden des Preview!", ex);
                                finalText = ex.getMessage();
                            }
                            break;
                        case PreviewActivity.COWSAY:
                            try {
                                InputStream is = getActivity().getAssets().open(path + "/" + items[1] + ".say");
                                String cow = IOUtils.toString(is);
                                IOUtils.closeQuietly(is);
                                finalText = wrappedText + "\n" + cow;
                            }
                            catch (Exception ex){
                                Log.w(TAG, "Fehler beim Laden des Preview!", ex);
                                finalText = ex.getMessage();
                            }
                            break;
                    }
                    finalText+=emptyLine;
                    previewTexts.add(new PreviewText("#" + prefix + items[0], items[1], finalText));
                }
            }
            return previewTexts;

        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null)
                mCallbacks.onPreExecute();
        }

        @Override
        protected ArrayList<PreviewText> doInBackground(String... textArray) {
            ArrayList<PreviewText> texts = new ArrayList<>();
            String text = textArray[0];
            texts.add(new PreviewText("", "Simple", TextUtils.wordWrap(text, 24) + emptyLine));
            try {
                texts.addAll(readCSV("asciiart","art.csv", "A", text));
                texts.addAll(readCSV("fonts","fonts.csv", "F", text));
                texts.addAll(readCSV("cowsay", "cowsay.csv", "C", text));
            }
            catch (Exception ex){
                Log.w(TAG, ex.getMessage(), ex);
            }
            return texts;

        }

        @Override
        protected void onPostExecute(ArrayList<PreviewText> texts) {
            if (mCallbacks != null)
                mCallbacks.onPostExecute(texts);
        }

    }
}
