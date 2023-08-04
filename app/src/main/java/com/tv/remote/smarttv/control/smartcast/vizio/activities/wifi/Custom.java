package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;


import com.squareup.okhttp.CertificatePinner;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class Custom implements X509TrustManager {
    // Replace the below pins with the actual SHA-256 fingerprints or public keys
    private static final String[] PINS = {
            "sha256/V/fU6Momogy9igcGCVa6ELoyAw/L5a+cCREgbQE03JU=",
    };

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (X509Certificate cert : chain) {
            String pin = CertificatePinner.pin(cert);
            if (isValidPin(pin)) {
                return;
            }
        }
        throw new CertificateException("Server certificate is not trusted.");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private boolean isValidPin(String pin) {
        for (String validPin : PINS) {
            if (validPin.equals(pin)) {
                return true;
            }
        }
        return false;
    }
}