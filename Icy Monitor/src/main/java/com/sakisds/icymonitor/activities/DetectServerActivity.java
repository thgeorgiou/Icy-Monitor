package com.sakisds.icymonitor.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.dataobj.MulticastComputer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Created by Thanasis Georgiou on 15/11/13.
 */
public class DetectServerActivity extends ListActivity implements View.OnClickListener {

    MulticastSocket mSocket;
    WifiManager.MulticastLock mMulticastLock;
    AsyncMulticastReceiver mAsyncReceiver;
    ArrayList<MulticastComputer> mData;
    Activity mContext = this;

    boolean mIOSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detect_server);

        // Initialize list
        mData = new ArrayList<MulticastComputer>();

        // Prepare list and button
        Button button = (Button) findViewById(R.id.button_manual);
        button.setOnClickListener(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String name = ((TextView) v.findViewById(R.id.text_name)).getText().toString();
                String address = ((TextView) v.findViewById(R.id.text_address)).getText().toString();

                Intent intent = new Intent(mContext, AddServerActivity.class);
                intent.putExtra(AddServerActivity.EXTRA_NAME, name);
                intent.putExtra(AddServerActivity.EXTRA_ADDRESS, address);

                startActivity(intent);
                finish();
            }
        });

        // Acquire multicast lock
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mMulticastLock = wifi.createMulticastLock("multicastLock");
        mMulticastLock.setReferenceCounted(true);
        mMulticastLock.acquire();

        mIOSuccess = true;
        try {
            mSocket = new MulticastSocket(9584);
            InetAddress group = InetAddress.getByName("224.6.7.8");
            mSocket.joinGroup(group);
        } catch (IOException e) {
            mIOSuccess = false;
        }
    }

    public Context getContext() {
        return this;
    }

    public void onPause() {
        if (mAsyncReceiver != null) {
            mAsyncReceiver.cancel(true);
        }

        super.onPause();
    }

    public void onResume() {
        if (mIOSuccess) {
            mAsyncReceiver = new AsyncMulticastReceiver();
            mAsyncReceiver.execute();
        }

        super.onResume();
    }

    public void onStop() {
        if (mAsyncReceiver != null) {
            mAsyncReceiver.cancel(true);
        }

        super.onStop();
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, AddServerActivity.class));
        finish();
    }

    private class AsyncMulticastReceiver extends AsyncTask<Void, MulticastComputer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()) {
                DatagramPacket packet;
                byte[] buffer = new byte[512];
                packet = new DatagramPacket(buffer, buffer.length);

                try {
                    mSocket.receive(packet);
                } catch (IOException e) {
                    cancel(true);
                }

                String data = new String(packet.getData());

                if (data.substring(0, 11).equals("IcyMonitor-")) {
                    data = data.substring(11).replaceAll("[^A-Za-z0-9()\\[\\]]", "");
                    String ip = packet.getAddress().toString().substring(1);

                    publishProgress(new MulticastComputer(data, ip));
                }
            }
            return null;
        }

        protected void onProgressUpdate(MulticastComputer... server) {
            boolean doNothing = false;

            for (MulticastComputer computer : mData) {
                if (computer.getAddress().equals(server[0].getAddress())) {
                    doNothing = true;
                    break;
                }
            }

            if (!doNothing) {
                mData.add(server[0]);
                MulticastComputer[] arrayData = new MulticastComputer[mData.size()];
                mData.toArray(arrayData);

                setListAdapter(new ServersListArrayAdapter(getContext(), arrayData));
            }
        }

        protected void onPostExecute(Void nothing) {
            // Release multicast lock
            if (mMulticastLock != null) {
                mMulticastLock.release();
                mMulticastLock = null;
            }
        }
    }

    private class ServersListArrayAdapter extends ArrayAdapter<MulticastComputer> {

        private final Context mContext;
        private final MulticastComputer[] mListData;

        public ServersListArrayAdapter(Context context, MulticastComputer[] data) {
            super(context, R.layout.list_item_multicastcomputer, data);
            mListData = data;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_multicastcomputer, parent, false);

            ((TextView) rowView.findViewById(R.id.text_name)).setText(mListData[position].getName());
            ((TextView) rowView.findViewById(R.id.text_address)).setText(mListData[position].getAddress());

            return rowView;
        }
    }
}
