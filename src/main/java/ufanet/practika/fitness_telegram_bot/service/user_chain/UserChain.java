package ufanet.practika.fitness_telegram_bot.service.user_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface UserChain {
    void process(long chatId, long messageId, CallbackQuery callbackQuery);
}
