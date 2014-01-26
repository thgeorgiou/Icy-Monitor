package com.sakisds.icymonitor.fragments.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Thanasis Georgiou on 16/12/13.
 */
public class HistoryGraphFragment extends Fragment implements Button.OnClickListener {
    public static final String KEY_HISTORY_FILE = "com.sakisds.icymonitor.key.historyfile";

    // Chart
    private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;

    // Date format
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private SharedPreferences mSettings;

    private View mProgressBar, mErrorView;
    private Button mButtonZoomIn, mButtonZoomOut;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historygraph, container, false);

        mProgressBar = rootView.findViewById(R.id.progressBar_historyGraph);
        mErrorView = rootView.findViewById(R.id.emptyhidden);

        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, Context.MODE_PRIVATE);

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
        mRenderer.setZoomEnabled(true, false);
        mRenderer.setPanEnabled(true, false);
        mRenderer.setExternalZoomEnabled(true);

        mChart = ChartFactory.getTimeChartView(getActivity().getBaseContext(), mDataset, mRenderer, "yyyy/MM/dd\nHH:mm");
        mChart.setBackgroundColor(Color.TRANSPARENT);

        FrameLayout graphContainer = (FrameLayout) rootView.findViewById(R.id.graph_container);
        graphContainer.addView(mChart);

        mChart.repaint();

        // Buttons
        mButtonZoomIn = (Button) rootView.findViewById(R.id.button_zoom_in);
        mButtonZoomOut = (Button) rootView.findViewById(R.id.button_zoom_out);
        mButtonZoomIn.setOnClickListener(this);
        mButtonZoomOut.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Refresh fragment data
        refreshData();
    }

    private void refreshData() {
        // Request data
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 2000);

        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "historyfile");
        params.put("file", getArguments().getString(KEY_HISTORY_FILE));
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        client.get(url, params, new HistoryHttpResponseHandler());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_zoom_in:
                mChart.zoomIn();
                break;
            case R.id.button_zoom_out:
                mChart.zoomOut();
                break;
        }
    }

    private void displayGraphError(String text) {
        mProgressBar.setVisibility(View.GONE);
        mChart.setVisibility(View.GONE);

        mErrorView.setVisibility(View.VISIBLE);
        ((TextView) mErrorView.findViewById(R.id.textView_error)).setText(text);
    }

    private class HistoryHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                JSONArray dataArray = response.getJSONArray("Data");

                TimeSeries[] series = new TimeSeries[dataArray.length()];
                XYSeriesRenderer[] seriesRenderers = new XYSeriesRenderer[dataArray.length()];

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject jsonLine = dataArray.getJSONObject(i);

                    series[i] = new TimeSeries(jsonLine.getString("Name"));
                    seriesRenderers[i] = new XYSeriesRenderer();
                    seriesRenderers[i].setColor(getResources().getColor(R.color.color_holo_blue));
                    seriesRenderers[i].setLineWidth(1.5f);

                    mDataset.addSeries(series[i]);
                    mRenderer.addSeriesRenderer(seriesRenderers[i]);

                    JSONArray dataArray2 = jsonLine.getJSONArray("Data");

                    int step = 1;
                    if (dataArray2.length() > 250) step = 2;
                    else if (dataArray2.length() > 500) step = 3;
                    else if (dataArray2.length() > 750) step = 4;
                    else if (dataArray2.length() > 1000) step = 5;

                    for (int e = 0; e < dataArray2.length(); e += step) {
                        JSONObject point = dataArray2.getJSONObject(e);
                        String x = point.getString("x");

                        series[i].add(mDateFormat.parse(x), point.getInt("y"));
                    }
                }

                mProgressBar.setVisibility(View.GONE);
                mButtonZoomIn.setVisibility(View.VISIBLE);
                mButtonZoomOut.setVisibility(View.VISIBLE);

                mChart.repaint();
            } catch (JSONException e) {
                displayGraphError(getString(R.string.error_invalid_response));
            } catch (ParseException e) {
                displayGraphError(getString(R.string.error_invalid_response));
            }
        }

        public void onFailure(Throwable e, JSONObject response) {
            displayGraphError(getString(R.string.error_no_refresh));
        }
    }
}
