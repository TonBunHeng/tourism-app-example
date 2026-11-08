package com.example.smarttourismapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

/**
 * Utility to manage app localization dynamically (Khmer / English).
 */
public class LocaleHelper {

    public static Context setLocale(Context context, String language) {
        new AppPreference(context).setLanguage(language);
        return updateResources(context, language);
    }

    public static Context applyLocale(Context context) {
        String language = new AppPreference(context).getLanguage();
        return updateResources(context, language);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            return context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }
}
