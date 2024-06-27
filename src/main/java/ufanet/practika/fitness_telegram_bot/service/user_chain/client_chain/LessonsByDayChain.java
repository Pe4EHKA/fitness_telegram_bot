package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LessonsByDayChain extends ClientBaseChain{
    public LessonsByDayChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callBackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        long messageId = callbackQuery.getMessage().getMessageId();

        if(isDayContainsLessons(callBackData)){
            lessonsInDay(chatId, messageId, callBackData);
        }
        else if(callBackData.contains(BACK_TO_LESSONS_SCHEDULE_DAY)){
            lessonsInDay(chatId, messageId, clientService
                    .getLesson(Integer.parseInt(callBackData.split("/")[1]))
                    .getStartDateTime().toString()
            );
        }
        else{
            if(next != null){
                next.process(update);
            }
        }
    }

    private void lessonsInDay(long chatId, long messageId, String callBackData){
        List<Lesson> lessonsByDay = clientService.getAllAvailableLessonsByDay(LocalDateTime.parse(callBackData));
        Map<String, String> buttons = lessonsByDay.stream().collect(Collectors.toMap(
                el -> el.getStartDateTime().format(formatterByTime) + " "
                        + el.getLessonType().getType(),
                el -> el.getId().toString()
        ));

        String text = "Наше расписание занятий на " + LocalDateTime.parse(callBackData).format(formatterByDay) + ": ";
        buttons.put(BACK_TO_LESSONS_SCHEDULE_WEEK, BACK_TO_LESSONS_SCHEDULE_WEEK);

        executeEditMessage(text, chatId, messageId, buttons);
    }
    private boolean isDayContainsLessons(String callBackData){
        try {
            LocalDateTime day = LocalDateTime.parse(callBackData);
            return !clientService.getAllAvailableLessonsByDay(day).isEmpty();
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
