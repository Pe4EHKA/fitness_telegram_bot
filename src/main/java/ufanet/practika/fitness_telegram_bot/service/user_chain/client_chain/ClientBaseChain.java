package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;
import ufanet.practika.fitness_telegram_bot.service.user_chain.UserChain;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class ClientBaseChain implements UserChain {
    static final String SCHEDULE = "Посмотреть записи";
    static final String APPOINTMENT = "Записаться";
    static final String ERROR_MESSAGE = "Error occurred: ";
    static final String BACK_TO_MAIN = "Назад на главную";
    static final String CANCEL_LESSON = "Отменить занятие";
    static final String BACK_TO_LESSONS = "Назад к занятиям";

    protected UserChain next;
    protected ClientService clientService;
    protected TelegramBot telegramBot;

    public ClientBaseChain(ClientService clientService, TelegramBot telegramBot) {
        this.clientService = clientService;
        this.telegramBot = telegramBot;
    }

    public static ClientBaseChain link(ClientBaseChain first, ClientBaseChain... chain){
        ClientBaseChain head = first;
        for(ClientBaseChain nextChain : chain){
            head.next = nextChain;
            head = nextChain;
        }
        return first;
    }

    protected void executeEditMessage(String text, long chatId, long messageId, Map<String, String> buttons) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);
        message.setReplyMarkup(getButtons(buttons));

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_MESSAGE + e.getMessage());
        }
    }

    protected InlineKeyboardMarkup getButtons(Map<String, String> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();

        // Множество элементом map сортируются по ключю и из нового потока создаётся LinkedHashSet
        LinkedHashSet<Map.Entry<String, String>> values = buttons.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toCollection(LinkedHashSet::new));
        for(var button : values) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            var keyBoardButton = new InlineKeyboardButton();
            keyBoardButton.setText(button.getKey());
            keyBoardButton.setCallbackData(button.getValue());

            row.add(keyBoardButton);
            buttonRows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(buttonRows);
        return inlineKeyboardMarkup;
    }

    protected List<Lesson> getUserLessons(long chatId){
        Optional<User> user = clientService.getUser(chatId);
        List<Lesson> clientLessons = null;
        if(user.isPresent()) {
            clientLessons = clientService.getAllClientLessons(user.get());
        }
        return clientLessons;
    }

    protected void clientSchedule(long chatId, long messageId){
        List<Lesson> clientLessons = getUserLessons(chatId);
        String textToSend = "Твоё расписание:";

        Map<String, String> clientButtons = clientLessons.stream()
                .collect(Collectors.toMap(Lesson::toString, el -> el.getId().toString()));
        clientButtons.put(BACK_TO_MAIN, BACK_TO_MAIN);

        executeEditMessage(textToSend, chatId, messageId, clientButtons);
    }
}
