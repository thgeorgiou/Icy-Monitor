package com.sakisds.icymonitor.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OverviewFragment extends Fragment implements OnRefreshListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    // Textviews
    private TextView mTextViewOS, mTextViewOSArch, mTextViewHostname, mTextViewHistorySince, mTextViewUptime, mTextViewLastReboot;

    // Chart
    private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;

    // Progress and error
    private View mProgressBar, mEmptyHidden;

    // Http Client
    AsyncHttpClient mClient = new AsyncHttpClient();

    // Date format
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);


    private SharedPreferences mSettings;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, Context.MODE_PRIVATE);

        // Get text views
        mTextViewOS = (TextView) rootView.findViewById(R.id.textView_OS);
        mTextViewOSArch = (TextView) rootView.findViewById(R.id.textView_OSArch);
        mTextViewHostname = (TextView) rootView.findViewById(R.id.textView_hostname);
        mTextViewHistorySince = (TextView) rootView.findViewById(R.id.textView_historySince);
        mTextViewUptime = (TextView) rootView.findViewById(R.id.textView_uptimeSince);
        mTextViewLastReboot = (TextView) rootView.findViewById(R.id.textView_uptimeDate);

        // Get progress bar for graph and error view
        mProgressBar = rootView.findViewById(R.id.progressBar_overview);
        mEmptyHidden = rootView.findViewById(R.id.emptyhidden);

        // Client
        mClient.setMaxRetriesAndTimeout(1, 1000);

        // Create graphs
        mRenderer = new XYMultipleSeriesRenderer();
        mDataset = new XYMultipleSeriesDataset();

        mRenderer.setXTitle("\n\n\n\n" + getString(R.string.graph_time));
        mRenderer.setYTitle(getString(R.string.graph_temperature) + "\n\n");
        mRenderer.setAxisTitleTextSize(25.0f);
        mRenderer.setMargins(new int[]{0, 60, 40, 0});
        mRenderer.setYLabelsPadding(8.0f);
        mRenderer.setGridColor(getResources().getColor(R.color.color_charcoal_grey));
        mRenderer.setShowGridX(true);
        mRenderer.setShowGridY(true);
        mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setLabelsTextSize(20.0f);
        mRenderer.setAxesColor(getResources().getColor(R.color.color_charcoal_grey));
        mRenderer.setLabelsColor(getResources().getColor(android.R.color.primary_text_light));
        mRenderer.setXLabelsColor(getResources().getColor(R.color.color_charcoal_grey));
        mRenderer.setYLabelsColor(0, getResources().getColor(R.color.color_charcoal_grey));
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(100);
        mRenderer.setBackgroundColor(Color.TRANSPARENT);
        mRenderer.setShowLegend(false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setPanEnabled(false, false);

        mChart = ChartFactory.getTimeChartView(getActivity().getBaseContext(), mDataset, mRenderer, "yyyy/MM/dd\nHH:mm");
        mChart.setBackgroundColor(Color.TRANSPARENT);

        FrameLayout graphContainer = (FrameLayout) rootView.findViewById(R.id.graph_container);
        graphContainer.addView(mChart);

        mChart.repaint();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) view;

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);

        // Refresh fragment data
        refreshData();
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshData();
    }

    private void refreshData() {
        // Request data
        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "overview");
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        mClient.get(url, params, new OverviewHttpResponseHandler(url));
    }

    private void displayGraphError(String text) {
        mProgressBar.setVisibility(View.GONE);
        mChart.setVisibility(View.GONE);

        mEmptyHidden.setVisibility(View.VISIBLE);
        ((TextView) mEmptyHidden.findViewById(R.id.textView_error)).setText(text);
    }

    private class OverviewHttpResponseHandler extends JsonHttpResponseHandler {
        String mUrl;

        public OverviewHttpResponseHandler(String url) {
            mUrl = url;
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                // Read data from JSON
                int d = response.getInt("d");
                int h = response.getInt("h");
                int m = response.getInt("m");

                String OSName = response.getString("OSName");
                String OSArch = response.getString("OSArch");
                String hostname = response.getString("Hostname");
                String[] lastBoot = response.getString("LastBoot").split(" ");

                // Set data
                mTextViewOS.setText(OSName);
                mTextViewOSArch.setText(String.format(getString(R.string.arch), OSArch));
                mTextViewHostname.setText(String.format(getString(R.string.hostname), hostname));
                mTextViewUptime.setText(String.format(getString(R.string.uptime_since), d, h, m));
                mTextViewLastReboot.setText(String.format(getString(R.string.uptime_lastreboot), lastBoot[0], lastBoot[1]));
            } catch (JSONException e) {
                mTextViewOS.setText(getString(R.string.error_invalid_response));
                mTextViewOSArch.setText("");
                mTextViewHostname.setText("");
                mTextViewUptime.setText("");
                mTextViewLastReboot.setText("");
            }

            RequestParams params = new RequestParams();
            params.put("type", "history");
            params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

            mClient.get(mUrl, params, new HistoryHttpResponseHandler());
        }

        public void onFailure(Throwable e, JSONObject response) {
            mTextViewOS.setText(getString(R.string.error_could_not_connect_title));
            mTextViewOSArch.setText("");
            mTextViewHostname.setText("");
            mTextViewUptime.setText("");
            mTextViewLastReboot.setText("");

            displayGraphError(getString(R.string.error_no_refresh));

            mPullToRefreshLayout.setRefreshComplete();
        }
    }

    private class HistoryHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray response) {
            try {
                // Check if history is enabled
                if (response.length() == 0) {
                    displayGraphError(getString(R.string.history_disabled));
                    return;
                }

                // Read data from JSON
                TimeSeries[] series = new TimeSeries[response.length()];
                XYSeriesRenderer[] seriesRenderers = new XYSeriesRenderer[response.length()];

                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonLine = response.getJSONObject(i);

                    series[i] = new TimeSeries(jsonLine.getString("Name"));
                    seriesRenderers[i] = new XYSeriesRenderer();
                    seriesRenderers[i].setColor(getResources().getColor(R.color.color_holo_blue));
                    seriesRenderers[i].setLineWidth(1.5f);

                    mDataset.addSeries(series[i]);
                    mRenderer.addSeriesRenderer(seriesRenderers[i]);

                    JSONArray dataArray = jsonLine.getJSONArray("Data");
                    for (int e = 0; e < dataArray.length() ; e++) {
                        JSONObject point = dataArray.getJSONObject(e);
                        String x = point.getString("x");

                        if (e == 0) mTextViewHistorySince.setText(String.format(getString(R.string.temp_history), x));
                        series[i].add(mDateFormat.parse(x), point.getInt("y"));
                    }

                    // Paint graph and finish refreshing
                    mPullToRefreshLayout.setRefreshComplete();
                    mProgressBar.setVisibility(View.GONE);
                    mChart.repaint();
                }
            } catch (JSONException e) {
                displayGraphError(getString(R.string.error_invalid_response));
                e.printStackTrace();
            } catch (ParseException e) {
                displayGraphError(getString(R.string.error_invalid_response));
            }
        }

        public void onFailure(Throwable e, JSONObject response) {
            displayGraphError(getString(R.string.error_no_refresh));
        }
    }
}
