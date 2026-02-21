package com.main.untitledantgame.utility;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.main.untitledantgame.utility.JsonLoader;

import com.main.untitledantgame.map.Generator;
import java.util.ArrayList;
import java.util.List;

public class GeneratorLoader {

    public static List<Generator> loadGenerators(Context context, String filename) {

        JsonArray jsonArray = JsonLoader.loadJsonArray(context, filename);

        if (jsonArray == null) return null;

        Gson gson = new Gson();

        List<Generator> generators = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {

            Generator generator = gson.fromJson(
                    jsonArray.get(i),
                    Generator.class
            );

            generators.add(generator);
        }

        return generators;
    }
}