package ufanet.practika.fitness_telegram_bot.service.user_chain;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserChain {
    void process(Update update);
}
