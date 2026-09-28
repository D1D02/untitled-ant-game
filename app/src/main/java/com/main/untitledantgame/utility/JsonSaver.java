package com.main.untitledantgame.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.main.untitledantgame.map.Statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;



public class JsonSaver {

    private static final String TAG = "JsonSaver";
    private static final String STATS_FILE_NAME = "statistics.json";
    private static final String COINS_FILE_NAME = "coins.json";
    private static final String SKINS_FILE_NAME = "skins.json";

    public static void saveStatistics(Context context, Statistics stats) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray statisticsArray = loadStatisticsArray(context);

        JsonObject statisticsJson = gson.toJsonTree(stats).getAsJsonObject();

        statisticsArray.add(statisticsJson);

        try (FileOutputStream fos = context.openFileOutput(STATS_FILE_NAME,Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos,StandardCharsets.UTF_8)) {

            gson.toJson(statisticsArray, writer);

        } catch (Exception e) {
        }
    }

    public static JsonArray loadStatisticsArray(Context context) {

        File file = new File(context.getFilesDir(),STATS_FILE_NAME);

        if (!file.exists()) {
            return new JsonArray();
        }

        try (FileInputStream fis = context.openFileInput(STATS_FILE_NAME);
             InputStreamReader reader = new InputStreamReader(fis,StandardCharsets.UTF_8)) {

            return JsonParser.parseReader(reader).getAsJsonArray();

        } catch (Exception e) {
            return new JsonArray();
        }
    }

    public static Statistics getStatistics(Context context,int index) {

        JsonArray statisticsArray = loadStatisticsArray(context);

        if (index < 0 || index >= statisticsArray.size()) {
            return null;
        }

        Gson gson = new Gson();

        return gson.fromJson(statisticsArray.get(index),Statistics.class);
    }

    public static Statistics getHighestLevelGame(Context context) {

        JsonArray games = loadStatisticsArray(context);

        if (games.size() == 0) {
            return null;
        }

        Gson gson = new Gson();

        Statistics highestLevelGame = null;

        for (int i = 0; i < games.size(); i++) {

            Statistics currentGame = gson.fromJson(games.get(i),Statistics.class);

            if (highestLevelGame == null || currentGame.level > highestLevelGame.level) {
                highestLevelGame = currentGame;
            }
        }

        return highestLevelGame;
    }

    public static int getTotalGames(Context context) {

        return loadStatisticsArray(context).size();
    }

    public static Statistics getTotalStatistics(Context context) {

        JsonArray games = loadStatisticsArray(context);

        Statistics total = new Statistics();

        Gson gson = new Gson();

        for (int i = 0; i < games.size(); i++) {

            Statistics currentGame = gson.fromJson(
                    games.get(i),
                    Statistics.class
            );

            total.ants_spawned += currentGame.ants_spawned;
            total.pheromones_spawned += currentGame.pheromones_spawned;
            total.picked_food += currentGame.picked_food;
            total.obstacle_moved += currentGame.obstacle_moved;
            total.enemies_killed += currentGame.enemies_killed;
            total.coin += currentGame.coin;
        }

        return total;
    }

    public static int getTotalAntsSpawned(Context context) {

        return getTotalStatistics(context).ants_spawned;
    }

    public static int getTotalPheromonesSpawned(Context context) {

        return getTotalStatistics(context).pheromones_spawned;
    }

    public static int getTotalPickedFood(Context context) {

        return getTotalStatistics(context).picked_food;
    }

    public static int getTotalObstacleMoved(Context context) {

        return getTotalStatistics(context).obstacle_moved;
    }

    public static int getTotalEnemiesKilled(Context context) {

        return getTotalStatistics(context).enemies_killed;
    }

    public static int getTotalCoins(Context context) {

        return getTotalStatistics(context).coin;
    }

    public static int getCoins(Context context) {

        File file = new File(
                context.getFilesDir(),
                COINS_FILE_NAME
        );

        if (!file.exists()) {
            return 0;
        }

        try (FileInputStream fis =
                     context.openFileInput(COINS_FILE_NAME);
             InputStreamReader reader =
                     new InputStreamReader(fis, StandardCharsets.UTF_8)) {

            JsonObject json = JsonParser
                    .parseReader(reader)
                    .getAsJsonObject();

            return json.get("coins").getAsInt();

        } catch (Exception e) {
            Log.e(TAG, "Errore durante il caricamento delle monete", e);
            return 0;
        }
    }

    public static void setCoins(Context context, int coins) {

        if (coins < 0) {
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("coins", coins);

        try (FileOutputStream fos =
                     context.openFileOutput(
                             COINS_FILE_NAME,
                             Context.MODE_PRIVATE
                     );
             OutputStreamWriter writer =
                     new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(json, writer);

        } catch (Exception e) {
            Log.e(TAG, "Errore durante il salvataggio delle monete", e);
        }
    }

    public static void addCoins(Context context, int amount, Statistics stats) {

        if (amount <= 0) {
            return;
        }

        int currentCoins = getCoins(context);

        stats.coin += amount;

        setCoins(context, currentCoins + amount);
    }

    public static boolean spendCoins(Context context, int amount) {

        if (amount <= 0) {
            return false;
        }

        int currentCoins = getCoins(context);

        if (currentCoins < amount) {
            return false;
        }

        setCoins(context, currentCoins - amount);

        return true;
    }

    public static void setSkins(
            Context context,
            int[] skinPrices,
            int selectedSkin
    ) {

        // Controllo array
        if (skinPrices == null || skinPrices.length != 3) {
            return;
        }

        // Controllo selectedSkin
        if (selectedSkin < 0 || selectedSkin > 2) {
            return;
        }

        JsonObject json = new JsonObject();

        JsonArray pricesArray = new JsonArray();

        for (int price : skinPrices) {
            pricesArray.add(price);
        }

        json.add("skinPrices", pricesArray);
        json.addProperty("selectedSkin", selectedSkin);

        try (FileOutputStream fos =
                     context.openFileOutput(
                             SKINS_FILE_NAME,
                             Context.MODE_PRIVATE
                     );
             OutputStreamWriter writer =
                     new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(json, writer);

        } catch (Exception e) {
            Log.e(TAG, "Errore durante il salvataggio delle skin", e);
        }
    }


    public static int[] getSkins(
            Context context,
            int[] selectedSkin
    ) {

        File file = new File(
                context.getFilesDir(),
                SKINS_FILE_NAME
        );

        // Valori di default
        int[] defaultPrices = {0, 0, 0};

        if (selectedSkin != null && selectedSkin.length > 0) {
            selectedSkin[0] = 0;
        }

        if (!file.exists()) {
            return defaultPrices;
        }

        try (FileInputStream fis =
                     context.openFileInput(SKINS_FILE_NAME);
             InputStreamReader reader =
                     new InputStreamReader(fis, StandardCharsets.UTF_8)) {

            JsonObject json = JsonParser
                    .parseReader(reader)
                    .getAsJsonObject();

            JsonArray pricesArray = json
                    .getAsJsonArray("skinPrices");

            int[] skinPrices = new int[3];

            for (int i = 0; i < 3; i++) {
                skinPrices[i] = pricesArray.get(i).getAsInt();
            }

            int skin = json
                    .get("selectedSkin")
                    .getAsInt();

            if (skin >= 0 && skin <= 2) {
                if (selectedSkin != null && selectedSkin.length > 0) {
                    selectedSkin[0] = skin;
                }
            }

            return skinPrices;

        } catch (Exception e) {
            Log.e(TAG, "Errore durante il caricamento delle skin", e);
            return defaultPrices;
        }
    }
}