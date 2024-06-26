package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ufanet.practika.fitness_telegram_bot.config.BotConfig;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.util.HashMap;
import java.util.Map;

public class AdditionalInfoChain extends ClientBaseChain {
    public AdditionalInfoChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(long chatId, long messageId, CallbackQuery callbackQuery) {
        String callBackData = callbackQuery.getData();

        if(getUserLessons(chatId).stream().map(el -> el.getId().toString()).toList().contains(callBackData)) {
            Lesson lesson = clientService.getLesson(Integer.parseInt(callBackData));

            Map<String, String> buttons = new HashMap<>();
            buttons.put(CANCEL_LESSON, CANCEL_LESSON + " " + lesson.getId().toString());
            buttons.put("Назад", BACK_TO_LESSONS);

            String lessonType = lesson.getLessonType().getType();
            String lessonStart = lesson.getStartDateTime().toString();
            String lessonEnd = lesson.getEndDateTime().toString();
            String instructor = lesson.getInstructor().getName();
            String textToSend = lessonType + "\n"
                    + lessonStart + "\n"
                    + lessonEnd + "\n"
                    + instructor + "\n";

            executeEditMessage(textToSend, chatId, messageId, buttons);
        } else{
            if(next != null){
                next.process(chatId, messageId, callbackQuery);
            }
        }
    }
}
