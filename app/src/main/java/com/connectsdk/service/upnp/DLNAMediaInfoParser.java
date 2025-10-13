package com.connectsdk.service.upnp;

import android.util.Xml;

import com.connectsdk.core.ImageInfo;
import com.connectsdk.core.MediaInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class DLNAMediaInfoParser {
    private static final String ALBUM = "upnp:album";
    private static final String APOS = "&amp;apos;";
    private static final String ARTIST = "r:albumArtist";
    private static final String CREATOR = "dc:creator";
    private static final String GENRE = "upnp:genre";
    private static final String GT = "&gt;";
    private static final String LT = "&lt;";
    private static final String RADIOTITLE = "r:streamContent";
    private static final String THUMBNAIL = "upnp:albumArtURI";
    private static final String TITLE = "dc:title";

    private static String getData(String str, String data) {
        if (str.contains(toEndTag(data))) {
            return toString(str.substring(str.indexOf(toStartTag(data)) + toStartTag(data).length(), str.indexOf(toEndTag(data))));
        }
        if (str.contains(LT)) {
            return "";
        }
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            parser.setInput(new StringReader(str));
            for (int eventType = parser.nextTag(); eventType != 1; eventType = parser.next()) {
                if (eventType == 2 && parser.getName().equals(data) && parser.next() == 4) {
                    return parser.getText();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return "";
    }

    public static MediaInfo getMediaInfo(String str) {
        String url = getURL(str);
        String title = getTitle(str);
        String mimeType = getMimeType(str);
        String description = getArtist(str) + "\n" + getAlbum(str);
        String iconUrl = getThumbnail(str);
        ArrayList<ImageInfo> list = new ArrayList<>();
        list.add(new ImageInfo(iconUrl));
        return new MediaInfo(url, mimeType, title, description, list);
    }

    public static MediaInfo getMediaInfo(String str, String baseUrl) {
        String iconUrl;
        String url = getURL(str);
        String title = getTitle(str);
        String mimeType = getMimeType(str);
        String description = getArtist(str) + "\n" + getAlbum(str);
        String iconUrl2 = getThumbnail(str);
        try {
            new URL(iconUrl2).openConnection().connect();

//            ((URLConnection) FirebasePerfUrlConnection.instrument(new URL(iconUrl2).openConnection())).connect();
            iconUrl = iconUrl2;
        } catch (Exception e) {
            iconUrl = baseUrl + iconUrl2;
        }
        ArrayList<ImageInfo> list = new ArrayList<>();
        list.add(new ImageInfo(iconUrl));
        return new MediaInfo(url, mimeType, title, description, list);
    }

    public static String getTitle(String str) {
        if (!getData(str, RADIOTITLE).equals("")) {
            return getData(str, RADIOTITLE);
        }
        return getData(str, TITLE);
    }

    public static String getArtist(String str) {
        return getData(str, CREATOR);
    }

    public static String getAlbum(String str) {
        return getData(str, ALBUM);
    }

    public static String getGenre(String str) {
        return getData(str, GENRE);
    }

    public static String getThumbnail(String str) {
        return URLDecoder.decode(getData(str, THUMBNAIL));
    }

    public static String getMimeType(String str) {
        if (!str.contains("protocolInfo")) {
            return "";
        }
        int startInd = str.indexOf("*:") + 2;
        return str.substring(startInd, str.substring(startInd).indexOf(":") + startInd);
    }

    public static String getURL(String str) {
        if (!str.contains(LT)) {
            return getData(str, "res");
        }
        if (str.contains(toEndTag("res"))) {
            return URLDecoder.decode(str.substring(str.substring(str.indexOf("&lt;res")).indexOf(GT) + str.indexOf("&lt;res") + GT.length(), str.indexOf(toEndTag("res"))));
        }
        return "";
    }

    private static String toStartTag(String str) {
        return LT + str + GT;
    }

    private static String toEndTag(String str) {
        return toStartTag("/" + str);
    }

    private static String toString(String text) {
        StringBuilder sb = new StringBuilder();
        if (!text.contains(APOS)) {
            return text;
        }
        sb.append(text.substring(0, text.indexOf(APOS)));
        sb.append("'");
        sb.append(text.substring(text.indexOf(APOS) + APOS.length()));
        return sb.toString();
    }
}
