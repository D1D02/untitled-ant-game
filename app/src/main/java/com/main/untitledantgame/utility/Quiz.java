package com.main.untitledantgame.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz {
    static List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
    static QuizQuestion selectedQuestion;
    static Random generator = new Random();

    public static void init(Context game){
        JsonArray quizJson = JsonLoader.loadJsonArray(game, "quiz.json");
        if (quizJson == null) {
            Log.d("ECS", "Entities not loaded!");
        } else {
            Log.d("ECS", "Entities loaded! Elements: " + quizJson.size());
        }

        for (JsonElement element : quizJson) {

            JsonObject obj = element.getAsJsonObject();

            QuizQuestion q = new QuizQuestion(obj.get("question").getAsString(), obj.get("answer").getAsBoolean());

            questions.add(q);
        }
    }

    public static String getQuestion(){
        selectedQuestion = questions.get(generator.nextInt(questions.size()));
        return selectedQuestion.question;
    }

    public static boolean answer(boolean answer){
        return selectedQuestion.isTrue == answer;
    }
}
