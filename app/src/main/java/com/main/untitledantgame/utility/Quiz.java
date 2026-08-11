package com.main.untitledantgame.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz {
    static List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
    static QuizQuestion selectedQuestion;
    static Random generator = new Random();

    public static void init(){
        questions.add(new QuizQuestion("Attivare youtube premium?", true));
    }

    public static String getQuestion(){
        selectedQuestion = questions.get(generator.nextInt(questions.size()));
        return selectedQuestion.question;
    }

    public static boolean answer(boolean answer){
        return selectedQuestion.isTrue == answer;
    }
}
