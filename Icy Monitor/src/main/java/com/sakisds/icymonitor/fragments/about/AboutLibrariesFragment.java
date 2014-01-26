package com.sakisds.icymonitor.fragments.about;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.sakisds.icymonitor.R;

public class AboutLibrariesFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_list, container, false);

        setListAdapter(new AboutLibrariesListArrayAdapter(getActivity(), new String[6]));

        return mRootView;
    }

    private class AboutLibrariesListArrayAdapter extends ArrayAdapter<String> {

        private final String[] mNames = {
                "Android Support Library",
                "Asynchronous Http Client",
                "AChartEngine",
                "Actionbar Pull-to-Refresh",
                "Pager Sliding Tab Strip",
                "Splitpane layout",
                "Color Picker Collection"};
        private final String[] mAuthors = {
                "By the Android Project",
                "By James Smith",
                "By the AChartEngine team",
                "By Chris Banes",
                "By Andreas Stuetz",
                "By MobiDevelop",
                "By Gabriele Mariotti"};
        private final String[] mLinks = {
                "http://developer.android.com/tools/support-library/index.html",
                "http://loopj.com/android-async-http/",
                "http://www.achartengine.org/",
                "https://github.com/chrisbanes/ActionBar-PullToRefresh",
                "https://github.com/astuetz/PagerSlidingTabStrip",
                "https://github.com/MobiDevelop/android-split-pane-layout",
                "https://github.com/gabrielemariotti/colorpickercollection"};
        private final Context mContext;

        public AboutLibrariesListArrayAdapter(Context context, String[] names) {
            super(context, R.layout.list_item_lib, names);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_lib, parent, false);

            // Set data
            ((TextView) rowView.findViewById(R.id.textView_libname)).setText(mNames[position]);
            ((TextView) rowView.findViewById(R.id.textView_libauthor)).setText(mAuthors[position]);
            ((TextView) rowView.findViewById(R.id.textView_liburl)).setText(mLinks[position]);

            // Return view to be displayed
            return rowView;
        }
    }

}
