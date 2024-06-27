package ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.LessonRegistration;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;

import java.time.LocalDateTime;
import java.util.Map;

public class SingUpToLessonChain extends MainWindowChain{

    public SingUpToLessonChain(ClientService clientService, TelegramBot telegramBot) {
        super(clientService, telegramBot);
    }

    @Override
    public void process(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callBackData = callbackQuery.getData();

        if(isSingUp(callBackData)){
            long chatId = callbackQuery.getMessage().getChatId();
            long messageId = callbackQuery.getMessage().getMessageId();
            signUpToLesson(chatId, messageId, callBackData);

            executeSendMessage(chatId, textToSend, buttons);
        } else{
            if(next != null){
                next.process(update);
            }
        }
    }
    // Проверяет, была ли нажата именно кнопка записи на занятие
    private boolean isSingUp(String callBackData){
        return callBackData.contains(SIGN_UP_LESSON);
    }
    // Запись на занятие
    private void signUpToLesson(long chatId, long messageId, String callBackData){
        // Регистрация записи
        int lessonId = Integer.parseInt(callBackData.split("/")[1]);

        Lesson lesson = clientService.getLesson(lessonId);
        User user = clientService.getUser(chatId).get();

        LessonRegistration lessonRegistration = new LessonRegistration();
        lessonRegistration.setUser(user);
        lessonRegistration.setLesson(lesson);
        lessonRegistration.setRegistrationDateTime(LocalDateTime.now());

        // Увеличение кол-ва занятых мест
        lesson.setOccupiedPlaces(lesson.getOccupiedPlaces() + 1);
        clientService.registrateLesson(lesson, lessonRegistration);

        // Вывод об успешном выполнении операции
        String textToEdit = "Вы успешно записаны на занятие!\nДата и время начала занятия: " +
                lesson.getStartDateTime().format(formatterDateAndTime);
        executeEditMessage(textToEdit, chatId, messageId, Map.of());
    }
}
