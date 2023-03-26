package com.tv.remote.smarttv.control.smartcast.vizio.remoteConfig.irRemote;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class ConsumerIrManagerCompat extends ConsumerIrManager {

    private android.hardware.ConsumerIrManager service;

    public ConsumerIrManagerCompat(Context context) {
        try {
            service = (android.hardware.ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
        }catch (NullPointerException asd){}catch (Exception asd){}
    }

    @Override
    public boolean hasIrEmitter() {
        return service.hasIrEmitter();
    }

    @Override
    public void transmit(int carrierFrequency, int[] pattern) {
        service.transmit(carrierFrequency, pattern);
    }

    @Override
    public android.hardware.ConsumerIrManager.CarrierFrequencyRange[] getCarrierFrequencies() {
        return service.getCarrierFrequencies();
    }
}



