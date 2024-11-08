package com.example.myapplication;

import android.content.Context;
import android.provider.Settings;

/**
 * Use to easily acquired AndroidID to be used by other classes
 */
public class DeviceUtils {
    /**
     * Retrieval of AndroidID
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
