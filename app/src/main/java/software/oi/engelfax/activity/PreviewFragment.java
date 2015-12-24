package software.oi.engelfax.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import software.oi.engelfax.PreviewText;
import software.oi.engelfax.R;

/**
 * Created by stefa_000 on 24.12.2015.
 */
public class PreviewFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String PREVIEW_TEXT = "previewText";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PreviewFragment newInstance(PreviewText previewText) {
            PreviewFragment fragment = new PreviewFragment();
            Bundle args = new Bundle();
            args.putString(PREVIEW_TEXT, previewText.text);
            fragment.setArguments(args);
            return fragment;
        }

        public PreviewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_engel_preview, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setText(getArguments().getString(PREVIEW_TEXT));
            textView.setMovementMethod(new ScrollingMovementMethod());
            return rootView;
        }

}
