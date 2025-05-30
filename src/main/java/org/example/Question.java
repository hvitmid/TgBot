package org.example;

import java.util.ArrayList;

public class Question {

    private String question; // Текст вопроса
    private String answergood; // ответ вопроса??



//    private ArrayList<String> answergood; // Список правильных ответов
//    private ArrayList<String> badanswer; // Список неправильных ответов
    private Integer type; // Категория вопроса

    // Конструктор
    public Question(String question) {
        this.question = question;
        this.answergood = answergood;
//        this.badanswer = new ArrayList<>();
    }

//    // Метод для добавления правильного ответа
//    public int addTrue(String answer) {
//        answergood.add(answer);
//        return answergood.size();
//    }
//
//    // Метод для добавления неправильного ответа
//    public int addFalse(String answer) {
//        badanswer.add(answer);
//        return badanswer.size();
//    }


    public String getAnswergood() {
        return answergood;
    }
    public String getQuestion() {
        return question;
    }
    public boolean checkAnswer(String answer){
        return answergood.equalsIgnoreCase(answer);
    }




//    // Геттер для списка неправильных ответов
//    public ArrayList<String> getBadanswer() {
//        return badanswer;
//    }
}