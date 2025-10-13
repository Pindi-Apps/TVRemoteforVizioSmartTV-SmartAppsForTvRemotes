package com.connectsdk.service.upnp;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class DLNANotifyParser {
    private static final String ns = null;

    public JSONArray parse(InputStream in) throws XmlPullParserException, IOException, JSONException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            parser.setInput(in, (String) null);
            parser.nextTag();
            return readPropertySet(parser);
        } finally {
            in.close();
        }
    }

    private JSONArray readPropertySet(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        JSONArray propertyset = new JSONArray();
        parser.require(2, ns, "e:propertyset");
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                if (parser.getName().equals("e:property")) {
                    propertyset.put(readProperty(parser));
                } else {
                    skip(parser);
                }
            }
        }
        return propertyset;
    }

    private JSONObject readProperty(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        JSONObject property = new JSONObject();
        parser.require(2, ns, "e:property");
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                if (name.equals("LastChange")) {
                    InputStream stream = null;
                    try {
                        stream = new ByteArrayInputStream(readText(parser).getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                    property.put("LastChange", new DLNAEventParser().parse(stream));
                } else {
                    property = readPropertyData(name, parser);
                }
            }
        }
        return property;
    }

    private JSONObject readPropertyData(String target, XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        JSONObject data = new JSONObject();
        parser.require(2, ns, target);
        data.put(target, readText(parser));
        parser.require(3, ns, target);
        return data;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.next() != 4) {
            return "";
        }
        String result = parser.getText();
        parser.nextTag();
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() == 2) {
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case 2:
                        depth++;
                        break;
                    case 3:
                        depth--;
                        break;
                }
            }
            return;
        }
        throw new IllegalStateException();
    }
}
