package com.connectsdk.service.netcast;


import com.connectsdk.service.CastService;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NetcastVolumeParser extends DefaultHandler {
    public final String LEVEL = "level";
    public final String MAX_LEVEL = "maxLevel";
    public final String MIN_LEVEL = "minLevel";
    public final String MUTE = CastService.CAST_SERVICE_MUTE_SUBSCRIPTION_NAME;
    public String value = null;
    public JSONObject volumeStatus = new JSONObject();

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) throws SAXException {
        try {
            if (str3.equalsIgnoreCase(CastService.CAST_SERVICE_MUTE_SUBSCRIPTION_NAME)) {
                this.volumeStatus.put(CastService.CAST_SERVICE_MUTE_SUBSCRIPTION_NAME, Boolean.parseBoolean(this.value));
            } else if (str3.equalsIgnoreCase("minLevel")) {
                this.volumeStatus.put("minLevel", Integer.parseInt(this.value));
            } else if (str3.equalsIgnoreCase("maxLevel")) {
                this.volumeStatus.put("maxLevel", Integer.parseInt(this.value));
            } else if (str3.equalsIgnoreCase("level")) {
                this.volumeStatus.put("level", Integer.parseInt(this.value));
            }
            this.value = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] cArr, int i, int i2) throws SAXException {
        this.value = new String(cArr, i, i2);
    }

    public JSONObject getVolumeStatus() {
        return this.volumeStatus;
    }
}





//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//
//public class NetcastVolumeParser extends DefaultHandler {
//    public JSONObject volumeStatus;
//
//    public String value;
//
//    public final String MUTE = "mute";
//    public final String MIN_LEVEL = "minLevel";
//    public final String MAX_LEVEL = "maxLevel";
//    public final String LEVEL = "level";
//
//    public NetcastVolumeParser() {
//        volumeStatus = new JSONObject();
//        value = null;
//    }
//
//    @Override
//    public void endElement(String uri, String localName, String qName) throws SAXException {
//        try {
//            if (qName.equalsIgnoreCase(MUTE)) {
//                volumeStatus.put(MUTE, Boolean.parseBoolean(value));
//            }
//            else if (qName.equalsIgnoreCase(MIN_LEVEL)) {
//                volumeStatus.put(MIN_LEVEL, Integer.parseInt(value));
//            }
//            else if (qName.equalsIgnoreCase(MAX_LEVEL)) {
//                volumeStatus.put(MAX_LEVEL, Integer.parseInt(value));
//            }
//            else if (qName.equalsIgnoreCase(LEVEL)) {
//                volumeStatus.put(LEVEL, Integer.parseInt(value));
//            }
//            value = null;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void characters(char[] ch, int start, int length) throws SAXException {
//        value = new String(ch, start, length);
//    }
//
//    public JSONObject getVolumeStatus() {
//        return volumeStatus;
//    }
//}
