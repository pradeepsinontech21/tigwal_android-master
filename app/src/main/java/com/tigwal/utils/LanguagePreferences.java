package com.tigwal.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class LanguagePreferences {

    Context _context;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    public static final String LAYOUT_ISRTL = "LAYOUT_ISRTL";
    public static final String SELCTED_LANGUAGE_ID = "SELCTED_LANGUAGE_ID";
    public static final String SHARED_PREF_NAME = "application";

    @Inject
    public LanguagePreferences(Context context) {
        this._context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    @SuppressLint("CommitPrefEdits")
    public static void putString(String name, String value) {
        editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }


    @SuppressLint("CommitPrefEdits")
    public void putBoolean(String name, boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public boolean getBoolean(String name, boolean b) {
        return sharedPreferences.getBoolean(name, false);
    }

    public static String getString(String name) {
        return sharedPreferences.getString(name, "");
    }

    public int getInt(String name) {
        return sharedPreferences.getInt(name, 0);
    }

    public void putTutorial(String name, boolean value) {
        editor.putBoolean(name, value);
        editor.apply();
    }

    public void clearAll() {
        sharedPreferences.edit()
                .clear()
                .apply();
    }

    public void putFloat(String name, float value) {

        editor = sharedPreferences.edit();
        editor.putFloat(name, value);
        editor.apply();
    }

    public float getFloat(String name) {
        return sharedPreferences.getFloat(name, 0f);
    }


    public void putIsRTL(boolean rtl) {
        editor.putBoolean(LAYOUT_ISRTL, rtl);
        editor.commit();
    }

    public boolean getIsRTL() {
        return sharedPreferences.getBoolean(LAYOUT_ISRTL, false);
    }


    public void putSelctedLanguage(String selectedid) {
        editor.putString(SELCTED_LANGUAGE_ID, selectedid);
        editor.commit();
    }

    public String getSelctedLanguage() {
        return sharedPreferences.getString(SELCTED_LANGUAGE_ID, "");
    }

}
