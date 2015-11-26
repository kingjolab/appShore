package com.decoration.appshore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by wangyu on 24/11/15.
 */

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setKeyValue(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(String key) {
        return prefs.getString(key, "");
    }

    public void delete(String session) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(session);
        editor.commit();
    }
}
