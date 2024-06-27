package ufanet.practika.fitness_telegram_bot.service.user_strategy;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserStrategy {
    void execute(Update update);
}
