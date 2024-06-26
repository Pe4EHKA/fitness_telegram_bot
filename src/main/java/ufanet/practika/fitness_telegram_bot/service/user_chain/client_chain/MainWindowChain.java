package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.util.HashMap;
import java.util.Map;

public class MainWindowChain extends ClientBaseChain {
    public MainWindowChain(ClientService clientService, TelegramBot bot) {
        super(clientService, bot);
    }
    @Override
    public void process(long chatId, long messageId, CallbackQuery callbackQuery) {
        if((callbackQuery == null) || callbackQuery.getData().equals(BACK_TO_MAIN) ) {
            // Подготовка кнопок для сообщения
            Map<String, String> buttons = new HashMap<>();
            buttons.put(SCHEDULE, SCHEDULE);
            buttons.put(APPOINTMENT, APPOINTMENT);
            String textToSend = "Выбери, что ты хочешь сделать:";
            if(callbackQuery != null) {
                executeEditMessage(textToSend, chatId, messageId, buttons);
            } else{
                prepareAndSendMessage(chatId, textToSend, buttons);
            }
        } else{
            if(next != null){
                next.process(chatId, messageId, callbackQuery);
            }
        }
    }
}
