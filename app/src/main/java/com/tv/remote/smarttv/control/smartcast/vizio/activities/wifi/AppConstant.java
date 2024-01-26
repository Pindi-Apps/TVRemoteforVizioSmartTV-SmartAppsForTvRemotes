package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import com.connectsdk.device.ConnectableDevice;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.GalleryAdapter;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.ImageFolder;

import java.io.File;
import java.util.ArrayList;

public class AppConstant {

    public static final String vizioMatric = "VizioRemote";
    public static File localPhoto  = null;
    public static ImageFolder imageFolderPath=null;
    public static ArrayList<GalleryImagesModel> photosList = new ArrayList<>();
    public static ArrayList<GalleryImagesModel> videoList = new ArrayList<>();
    public static ArrayList<GalleryImagesModel> audiosList = new ArrayList<>();


    public static final String format = "%s%s";
    public static final String PreferenceName = "VizioRemote";

    public static final  String keyPress = "KEYPRESS";
    public static ConnectableDevice connectableDevice = null;
    public static final String iconUrl = "https://play-lh.googleusercontent.com/OMuRAG-ILX5XiYr7Yza2T4tMS5cv06t2klj9JXyKUuJfG2h5wt5Lo60f3k6a6Blix8uF=w480-h960-rw";

}
