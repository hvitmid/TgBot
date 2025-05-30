package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bot extends TelegramLongPollingBot {

    final private String BOT_TOKEN = "7934559303:AAGMEZwPwWoTSt6RTANhvwJF8otdXDtMEb4";
    final private String BOT_NAME = "TP_study_bot";

    //  !!!  Объект Storage storage реализуйте так , чтобы можно было выводить из него String в сообщение пользователю
    Storage storage;


    //посредник Mediator работает с многими пользователями и вопросами.
    //сторедж - хранилище вопросов. коллекция или файл, чё хотим

    //апи телеграм часто обновляют - проверить на сайт зайти всё ли работает


    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    // Константы для меню
    private static final String START_COMMAND = "/start";
    private static final String MENU_MESSAGE = "Выберите режим:\n" +
            "1. /exam - Прохождение экзамена\n" +
            "2. /study - Изучение вопросов\n" +
            "3. /learn - Обучеecho \"# TgBot\" >> README.mdние до правильного ответа";

    private Map<Long, String> userModes = new HashMap<>();
    private Map<Long, Examinator> examinators = new HashMap<>();
    private SetQuestion questionSet;


    Bot() {
        try {
            questionSet = new SetQuestion("questions.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().toLowerCase();

            UserSession session = userSessions.computeIfAbsent(chatId, key -> new UserSession());

            if (text.equals(START_COMMAND)) {
                startMenu(chatId, session);
                return;
            }

            if (session.getState() == SessionState.MENU) {
                handleMenuChoice(chatId, text, session);
                return;
            }

            if (session.getState() == SessionState.EXAM) {
                handleExamAnswer(chatId, text, session);
            } else if (session.getState() == SessionState.STUDY) {
                handleStudyAnswer(chatId, text, session);
            } else if (session.getState() == SessionState.LEARN) {
                handleLearnAnswer(chatId, text, session);
            }
        }
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//        try{
//            if(update.hasMessage() && update.getMessage().hasText())
//            {
//                //Извлекаем из объекта сообщение пользователя
//                Message inMess = update.getMessage();
//                //Достаем из inMess id чата пользователя
//                String chatId = inMess.getChatId().toString();
//                //Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
//                String response = parseMessage(inMess.getText());
//                //Создаем объект класса SendMessage - наш будущий ответ пользователю
//                SendMessage outMess = new SendMessage();
//
//                //Добавляем в наше сообщение id чата а также наш ответ
//                outMess.setChatId(chatId);
//                outMess.setText(response);
//
//                //Отправка в чат
//                execute(outMess);
//
//            }
//
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//
//    }


    //ввод сообщения
    public String parseMessage(String textMsg) {
        String response;

        //Сравниваем текст пользователя с нашими командами, на основе этого формируем ответ
        if(textMsg.equals("/start"))
            response = "Привет";
        else if(textMsg.equals("/get"))
            response = storage.getRandQuote();
        //ответ
        else
            response = "Сообщение не распознано";

        return response;

    }

    private String parseMessage(String textMsg, UserSession session) {
        String response = "Сообщение не распознано";

        switch (textMsg) {
            case "/start":
                response = "Привет! Выберите режим:\n" +
                        "1. exam - Прохождение экзамена\n" +
                        "2. study - Изучение вопросов\n" +
                        "3. learn - Обучение до правильного ответа";
                session.setState(SessionState.MENU); // Переводим в меню
                break;

            case "exam":
                if (session.getState() == SessionState.MENU) {
                    startExam(chatId, session);
                    response = "Экзамен начался! Введите количество вопросов (например, 5)";
                } else {
                    response = "Сначала выберите режим через /start";
                }
                break;

            case "study":
                if (session.getState() == SessionState.MENU) {
                    startStudy(chatId, session);
                    response = "Изучение началось. Введите любой символ для следующего вопроса";
                } else {
                    response = "Сначала выберите режим через /start";
                }
                break;

            case "learn":
                if (session.getState() == SessionState.MENU) {
                    startLearn(chatId, session);
                    response = "Обучение началось. Ответьте на вопрос:";
                } else {
                    response = "Сначала выберите режим через /start";
                }
                break;

            default:
                // Обработка действий в режимах
                if (session.getState() == SessionState.EXAM) {
                    handleExamAnswer(chatId, textMsg, session);
                    response = "Ответ принят. Осталось вопросов: " +
                            (session.getTotalQuestions() - session.getCurrentQuestionIndex() - 1);
                } else if (session.getState() == SessionState.STUDY) {
                    handleStudyAnswer(chatId, textMsg, session);
                    response = "Правильный ответ будет показан после завершения";
                } else if (session.getState() == SessionState.LEARN) {
                    handleLearnAnswer(chatId, textMsg, session);
                }
        }

        return response;
    }






    private void sendResponse(Long chatId, String response) throws TelegramApiException {
        SendMessage outMess = new SendMessage();
        outMess.setChatId(chatId.toString());
        outMess.setText(response);
        execute(outMess);
    }


}
