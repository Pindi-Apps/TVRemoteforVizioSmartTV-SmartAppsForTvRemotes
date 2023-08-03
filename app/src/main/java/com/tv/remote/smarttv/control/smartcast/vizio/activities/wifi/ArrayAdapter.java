package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tv.remote.smarttv.control.smartcast.vizio.R;


public class ArrayAdapter extends android.widget.ArrayAdapter<DeviceModel> {
    int layout;

    public ArrayAdapter(Context context, int i) {
        super(context, i);
        this.layout = i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(getContext(), this.layout, null);
        }
        DeviceModel deviceModelVar = (DeviceModel) getItem(i);
        TextView textView = (TextView) view.findViewById(R.id.DeviceName);
        TextView textView2 = (TextView) view.findViewById(R.id.DeviceIP);
        if (deviceModelVar != null) {
            textView.setText(deviceModelVar.device());
        }
        if (deviceModelVar != null) {
            textView2.setText(deviceModelVar.hostAddress());
        }
        return view;
    }
}
