package org.example;

import java.util.ArrayList;
import java.util.List;

public class Examinator {
    private List<Question> questions;
    private List<Boolean> answers;
    private int currentQuestionIndex;

    public Examinator(List<Question> questions) {
        this.questions = new ArrayList<>(questions);
        this.answers = new ArrayList<>();
        this.currentQuestionIndex = 0;
    }

    public String action() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex).getQuestion();
        }
        return end();
    }

    public boolean check(String answer) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        boolean isCorrect = currentQuestion.checkAnswer(answer);
        answers.add(isCorrect);
        currentQuestionIndex++;
        return isCorrect;
    }

    public String end() {
        int correctCount = 0;
        for (Boolean answer : answers) {
            if (answer) correctCount++;
        }
        return "Тест завершен. Вы ответили правильно " + correctCount + " из " + questions.size() + " раз ";
    }
}