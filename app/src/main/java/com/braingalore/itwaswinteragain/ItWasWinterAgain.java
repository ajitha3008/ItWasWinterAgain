package com.braingalore.itwaswinteragain;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
/**
 * Created by ajitha3008 on 10/11/17.
 */

public class ItWasWinterAgain extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, "ca-app-pub-7486671799772140~4755231021");
    }
}
