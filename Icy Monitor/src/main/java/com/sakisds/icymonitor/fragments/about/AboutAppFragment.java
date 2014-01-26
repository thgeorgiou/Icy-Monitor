package com.sakisds.icymonitor.fragments.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.sakisds.icymonitor.R;

/**
 * Created by stratisg on 18/8/2013.
 */
public class AboutAppFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        // View page
        ((WebView) rootView.findViewById(R.id.about_webview)).loadUrl("file:///android_asset/about.html");

        return rootView;
    }

}
