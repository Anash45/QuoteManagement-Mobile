package com.example.helloworld;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }
}
