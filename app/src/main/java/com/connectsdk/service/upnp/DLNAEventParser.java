package com.connectsdk.service.upnp;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class DLNAEventParser {
    private static final String ns = null;

    public JSONObject parse(InputStream in) throws XmlPullParserException, IOException, JSONException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            parser.setInput(in, (String) null);
            parser.nextTag();
            return readEvent(parser);
        } finally {
            in.close();
        }
    }

    private JSONObject readEvent(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        JSONObject event = new JSONObject();
        JSONArray instanceIDs = new JSONArray();
        JSONArray queueIDs = new JSONArray();
        parser.require(2, ns, "Event");
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                if (name.equals("InstanceID")) {
                    instanceIDs.put(readInstanceID(parser));
                } else if (name.equals("QueueID")) {
                    queueIDs.put(readQueueID(parser));
                } else {
                    skip(parser);
                }
            }
        }
        if (instanceIDs.length() > 0) {
            event.put("InstanceID", instanceIDs);
        }
        if (queueIDs.length() > 0) {
            event.put("QueueID", queueIDs);
        }
        return event;
    }

    private JSONArray readInstanceID(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        JSONArray instanceIDs = new JSONArray();
        JSONObject data = new JSONObject();
        parser.require(2, ns, "InstanceID");
        data.put("value", parser.getAttributeValue((String) null, "val"));
        instanceIDs.put(data);
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                instanceIDs.put(readEntry(parser.getName(), parser));
            }
        }
        return instanceIDs;
    }

    private JSONArray readQueueID(XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        JSONArray queueIDs = new JSONArray();
        JSONObject data = new JSONObject();
        parser.require(2, ns, "QueueID");
        data.put("value", parser.getAttributeValue((String) null, "val"));
        queueIDs.put(data);
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                queueIDs.put(readEntry(parser.getName(), parser));
            }
        }
        return queueIDs;
    }

    private JSONObject readEntry(String target, XmlPullParser parser) throws IOException, XmlPullParserException, JSONException {
        parser.require(2, ns, target);
        String value = parser.getAttributeValue((String) null, "val");
        String channel = parser.getAttributeValue((String) null, "channel");
        parser.nextTag();
        parser.require(3, ns, target);
        JSONObject data = new JSONObject();
        data.put(target, value);
        if (channel != null) {
            data.put("channel", channel);
        }
        return data;
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
