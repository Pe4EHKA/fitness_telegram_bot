package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

public class ScheduleClientChain extends ClientBaseChain {
    public ScheduleClientChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(long chatId, long messageId, CallbackQuery callbackQuery) {
        if(callbackQuery.getData().equals(SCHEDULE) || callbackQuery.getData().equals(BACK_TO_LESSONS)) {
            clientSchedule(chatId, messageId);
        } else{
            if(next != null){
                next.process(chatId, messageId, callbackQuery);
            }
        }
    }
}
