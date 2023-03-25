package com.viziotv.smartcast.remote.control.activities.wifiPackage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.viziotv.smartcast.remote.control.R;


public class ListViewAdapter extends ArrayAdapter<WifiRemoteUtils> {
    int F0;

    public ListViewAdapter(Context context, int i) {
        super(context, i);
        this.F0 = i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(getContext(), this.F0, null);
        }
        WifiRemoteUtils wifiRemoteUtilsVar = (WifiRemoteUtils) getItem(i);
        TextView textView = (TextView) view.findViewById(R.id.DeviceName);
        TextView textView2 = (TextView) view.findViewById(R.id.DeviceIP);
        if (wifiRemoteUtilsVar != null) {
            textView.setText(wifiRemoteUtilsVar.getDeviceName());
        }
        if (wifiRemoteUtilsVar != null) {
            textView2.setText(wifiRemoteUtilsVar.b());
        }
        return view;
    }
}
