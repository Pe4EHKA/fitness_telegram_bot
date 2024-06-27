package ufanet.practika.fitness_telegram_bot.service.user_strategy;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Setter
public class UserContext {
    private UserStrategy userStrategy;

    public void execute(Update update) {
        userStrategy.execute(update);
    }
}
