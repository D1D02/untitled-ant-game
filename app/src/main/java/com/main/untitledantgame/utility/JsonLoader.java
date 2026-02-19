package com.main.untitledantgame.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonLoader { //Per caricare i Json

    private static final String TAG = "JsonLoader";

    public static JsonArray loadJsonArray(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(is);

            return JsonParser.parseReader(reader).getAsJsonArray();

        } catch (Exception e) {
            Log.d(TAG, "Error with parsing json file");
            return null;
        }
    }
}
