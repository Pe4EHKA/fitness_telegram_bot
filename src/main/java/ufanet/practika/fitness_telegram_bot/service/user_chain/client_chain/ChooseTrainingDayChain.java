package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.Update;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChooseTrainingDayChain extends ClientBaseChain{
    private static final String BACK_TO_CHOOSING_DAY = "К выбору дня";
    private final DateTimeFormatter formatterByDays = DateTimeFormatter.ofPattern("dd.MM");

    public ChooseTrainingDayChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        if(callBackData.equals(APPOINTMENT)
            || callBackData.equals(BACK_TO_CHOOSING_DAY))
        {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime afterSevenDays = now.plusDays(7);
            List<Lesson> lessonsTraining = clientService.getLessonsBetweenDates(now, afterSevenDays);
            Map<String, String> buttons = lessonsTraining.stream()
                    .collect(Collectors.toMap(
                            el -> el.getStartDateTime().format(formatterByDays),
                            el -> el.getStartDateTime().toString()
                    ));
            buttons.put(BACK_TO_MAIN, BACK_TO_MAIN);
            String text = "Наше расписание занятий на 7 дней: ";

            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            executeEditMessage(text, chatId, messageId, buttons);
        } else{
            if(next != null){
                next.process(update);
            }
        }
    }
}
