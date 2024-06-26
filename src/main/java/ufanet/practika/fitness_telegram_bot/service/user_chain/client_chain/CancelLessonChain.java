package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ufanet.practika.fitness_telegram_bot.config.BotConfig;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.util.Optional;

public class CancelLessonChain extends ClientBaseChain {
    public CancelLessonChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(long chatId, long messageId, CallbackQuery callbackQuery) {
        String callBackData = callbackQuery.getData();

        Optional<User> user = clientService.getUser(chatId);
        if (user.isPresent()) {
            String[] splitedCallBackData = callBackData.split(" ");
            int lessonId = Integer.parseInt(splitedCallBackData[splitedCallBackData.length - 1]);
            Lesson lesson = clientService.getLesson(lessonId);

            clientService.cancelLesson(user.get(), lesson);
            clientSchedule(chatId, messageId);
        } else{
            if(next != null){
                next.process(chatId, messageId, callbackQuery);
            }
        }
    }
}
