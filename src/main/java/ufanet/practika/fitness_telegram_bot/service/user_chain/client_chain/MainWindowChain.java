package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MainWindowChain extends ClientBaseChain {
    protected final Map<String, String> buttons = new HashMap<>();
    protected final String textToSend = "Выбери, что ты хочешь сделать:";

    public MainWindowChain(ClientService clientService, TelegramBot bot) {
        super(clientService, bot);

        buttons.put(SCHEDULE, SCHEDULE);
        buttons.put(APPOINTMENT, APPOINTMENT);
    }
    @Override
    public void process(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId;
        long messageId;

        if((callbackQuery == null)){
            chatId = update.getMessage().getChatId();
            executeSendMessage(chatId, textToSend, buttons);
        }
        else if (callbackQuery.getData().equals(BACK_TO_MAIN)){
            chatId = callbackQuery.getMessage().getChatId();
            messageId = callbackQuery.getMessage().getMessageId();
            executeEditMessage(textToSend, chatId, messageId, buttons);
        }
        else{
            if(next != null){
                next.process(update);
            }
        }
    }
}
