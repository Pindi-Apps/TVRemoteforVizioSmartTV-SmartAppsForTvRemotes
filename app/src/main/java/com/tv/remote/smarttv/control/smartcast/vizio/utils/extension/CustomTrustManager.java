package com.tv.remote.smarttv.control.smartcast.vizio.utils.extension;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {


    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (X509Certificate x509Certificate : chain) {
            if (x509Certificate.getIssuerDN().getName().contains("SmartViewSDK") && x509Certificate.getType().equalsIgnoreCase("X.509")) {
                return;
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}