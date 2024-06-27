package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.Update;
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
    public void process(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();


        if(canParseInt(callBackData) && clientService.isLessonExists(chatId, Integer.parseInt(callBackData))) {
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
                next.process(update);
            }
        }
    }
}
