package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.util.HashMap;
import java.util.Map;

public class SingUpLessonInfoChain extends ClientBaseChain{
    public SingUpLessonInfoChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callBackData = callbackQuery.getData();

        if(isContainsLessonId(callBackData)){
            Lesson lesson = clientService.getLesson(Integer.parseInt(callBackData));
            User instructor = lesson.getInstructor();

            String text = lesson.getLessonType().getType() + "\n***\n" +
                    lesson.getLessonType().getDescription() + "\n***\nВаш тренер:\n" + instructor.getName() + "\n***\n" +
                    instructor.getBio();
            Map<String, String> buttons = new HashMap<>();
            buttons.put(SIGN_UP_LESSON, SIGN_UP_LESSON + "/" + lesson.getId());
            buttons.put(BACK_TO_LESSONS_SCHEDULE_DAY, BACK_TO_LESSONS_SCHEDULE_DAY + "/" + lesson.getId());

            long chatId = callbackQuery.getMessage().getChatId();
            long messageId = callbackQuery.getMessage().getMessageId();
            executeEditMessage(text, chatId, messageId, buttons);
        } else{
            if(next != null){
                next.process(update);
            }
        }
    }
    private boolean isContainsLessonId(String callBackData){
        try {
            int lessonId = Integer.parseInt(callBackData);
            return clientService.isLessonExistsById(lessonId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
