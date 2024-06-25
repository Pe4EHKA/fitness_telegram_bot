package ufanet.practika.fitness_telegram_bot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ufanet.practika.fitness_telegram_bot.config.BotConfig;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.Role;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.entity.UserRole;
import ufanet.practika.fitness_telegram_bot.repository.LessonRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private ClientService clientService;
    final BotConfig config;

    static final String HELP_TEXT = """
            This bot is created to administrate your fitness training.

            You can execute from main menu on the left or by typing command

            Type /start to see welcome message

            Type /mydata to see data stored about yourself

            Type /help to see this message again""";

    static final String SCHEDULE = "Посмотреть записи";
    static final String APPOINTMENT = "Записаться";
    static final String ERROR_MESSAGE = "Error occurred: ";
    static final String BACK_TO_MAIN = "Назад на главную";
    static final String CANCEL_LESSON = "Отменить занятие";
    static final String BACK_TO_LESSONS = "Назад к занятиям";

    final DateTimeFormatter formatterByDays = DateTimeFormatter.ofPattern("dd.MM.yy");
    final DateTimeFormatter formatterByHourAndMinutes = DateTimeFormatter.ofPattern("HH:mm");


    @Autowired
    private LessonRepository lessonRepository;

    public TelegramBot(BotConfig config) {
        super(config.getToken());
        this.config = config;
        // Настройка команд, которые будут доступны в меню
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored in the database"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(ERROR_MESSAGE + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    /*
    Обработка данных, полученных от пользователя
    */
    @Override
    public void onUpdateReceived(Update update) {
        // Если сообщение получено и оно не пустое, то
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            /*
            Отправка сообщения всем пользователям. Возможна только для администратора
             */
            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = clientService.getAllUsers();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {
                switch (messageText) {
                    case "/start":
                        registerAndEnter(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;
                    case "/mydata":
                        getAllDataUser(chatId);
                        break;
                    default:
                        prepareAndSendMessage(chatId, "Простите, команда не распознана");
                }
            }
            /*
            Обработка нажатия кнопок на клавиатуре, появляющейся под сообщением
             */
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(SCHEDULE)) {
                clientSchedule(chatId, messageId);
            }
            /*
            Берём занятия пользователя и сревяем id занятий с id переданным в callBackData
             */
            else if(getUserLessons(chatId).stream().map(el -> el.getId().toString()).toList().contains(callBackData)) {
                lessonAdditionalInfo(chatId, messageId, callBackData);
            }
            else if(callBackData.equals(BACK_TO_LESSONS)) {
                clientSchedule(chatId, messageId);
            }
            else if(callBackData.equals(BACK_TO_MAIN)) {
                mainWindow(chatId, messageId);
            } else if (callBackData.equals(APPOINTMENT)) {
                chooseDayOfTraining(chatId, messageId);
            }
            else if(callBackData.contains(CANCEL_LESSON)){
                cancelLesson(chatId, messageId, callBackData);
            }
        }
    }

    private void chooseDayOfTraining(long chatId, long messageId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime afterSevenDays = now.plusDays(7);
        List<Lesson> lessonsTraining = lessonRepository.findByStartDateTimeBetween(now, afterSevenDays);
        Map<String, String> buttons = new LinkedHashMap<>();
        for (Lesson lesson : lessonsTraining) {
            buttons.put(lesson.getStartDateTime().format(formatterByDays), lesson.getStartDateTime().toString());
        }
        buttons.put("Назад", BACK_TO_MAIN);
        String text = "Наше расписание занятий на неделю: ";
        executeEditMessage(text, chatId, messageId, buttons);

    }

    private void cancelLesson(long chatId, long messageId, String callBackData) {
            Optional<User> user = clientService.getUser(chatId);
            if (user.isPresent()) {
                String[] splitedCallBackData = callBackData.split(" ");
                int lessonId = Integer.parseInt(splitedCallBackData[splitedCallBackData.length - 1]);
                Lesson lesson = clientService.getLesson(lessonId);

                clientService.cancelLesson(user.get(), lesson);
                clientSchedule(chatId, messageId);
            }
    }
    private void lessonAdditionalInfo(long chatId, long messageId, String callBackData){
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
    }

    /*
    Получение всех данных о пользователе
     */
    private void getAllDataUser(long chatId) {
        Optional<User> user = clientService.getUser(chatId);
        String textToSend;
        if (user.isPresent()) {
            textToSend = user.get().toString();
            prepareAndSendMessage(chatId, textToSend);
        }
        else{
            textToSend = "Вы не зарегистрированы";
            prepareAndSendMessage(chatId, textToSend);
        }
    }

    /*
    Регистрация пользователя в БД
     */
    private void registerAndEnter(Message msg) {
        if(!clientService.isExistingUser(msg.getChatId())) {
            // Создание объекта пользователя
            User user = new User();
            user.setName(msg.getChat().getFirstName());
            user.setChatId(msg.getChatId());
            user.setTelegramUserName(msg.getChat().getUserName());
            user.setRegistrationDate(LocalDateTime.now());

            /*
            Написано для удобства разработки: создаёт роль, если её нет в таблице ролей.
            В конце они будут там уже записаны и этот код не понадобится
             */
            Role role;
            if(clientService.isExistingRole(UserRoles.CLIENT.toString())) {
                // Получение роли пользователя
                role = clientService.getRole(UserRoles.CLIENT.toString()); // нужно будет оставить только это
            } else{
                role = new Role();
                role.setRole(UserRoles.CLIENT.toString());
                clientService.registrateRole(role);
            }

            // Создание объекта, соединяющий два предыдущих объекта
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRole.setUser(user);

            clientService.registrateUser(userRole);
        }
    }
    private List<Lesson> getUserLessons(long chatId){
        Optional<User> user = clientService.getUser(chatId);
        List<Lesson> clientLessons = null;
        if(user.isPresent()) {
            clientLessons = clientService.getAllClientLessons(user.get());
        }
        return clientLessons;
    }

    private void clientSchedule(long chatId, long messageId){
        List<Lesson> clientLessons = getUserLessons(chatId);
        String textToSend = "Твоё расписание:";

        Map<String, String> clientButtons = clientLessons.stream()
                .collect(Collectors.toMap(Lesson::toString, el -> el.getId().toString()));
        clientButtons.put(BACK_TO_MAIN, BACK_TO_MAIN);

        executeEditMessage(textToSend, chatId, messageId, clientButtons);
    }
    /*
    Обработка команды /start
     */
    private void startCommandReceived(long chatId, String name)  {
        String answer = EmojiParser.parseToUnicode("Привет, " + name + ", рад тебя видеть!"
                + " :blush:");
        log.info("Replied to user: " + name);
        prepareAndSendMessage(chatId, answer);

        // Подготовка кнопок для сообщения
        Map<String, String> buttons = new HashMap<>();
        buttons.put(SCHEDULE, SCHEDULE);
        buttons.put(APPOINTMENT, APPOINTMENT);
        answer = "Выбери, что ты хочешь сделать:";
        prepareAndSendMessage(chatId, answer, buttons);
    }
    private void mainWindow(long chatId, long messageId) {
        // Подготовка кнопок для сообщения
        Map<String, String> buttons = new HashMap<>();
        buttons.put(SCHEDULE, SCHEDULE);
        buttons.put(APPOINTMENT, SCHEDULE);
        String textToSend = "Выбери, что ты хочешь сделать:";
        executeEditMessage(textToSend, chatId, messageId, buttons);
    }
    /*
    Создаёт кнопки для клавиатуры под сообщением и возвращает объект, который их содержит.
    buttons - map содержащий текст для конпки(ключ) и идентификатор(значение)
     */
    private InlineKeyboardMarkup getButtons(Map<String, String> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();

        // Множество элементом map сортируются по ключю и из нового потока создаётся LinkedHashSet
        LinkedHashSet<Map.Entry<String, String>> values = buttons.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toCollection(LinkedHashSet::new));
        for(var button : values) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            var keyBoardButton = new InlineKeyboardButton();
            keyBoardButton.setText(button.getKey());
            keyBoardButton.setCallbackData(button.getValue());

            row.add(keyBoardButton);
            buttonRows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(buttonRows);
        return inlineKeyboardMarkup;
    }
    /*
    Отправка сообщения с кнопками под ним
     */
    private void prepareAndSendMessage(long chatId, String text, Map<String, String> buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(getButtons(buttons));
        executeMessage(message);
    }
    /*
    Отправка сообщения
     */
    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    /*
    Изменяет прошлое сообщения на указанное новое
     */
    private void executeEditMessage(String text, long chatId, long messageId, Map<String, String> buttons) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);
        message.setReplyMarkup(getButtons(buttons));

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_MESSAGE + e.getMessage());
        }
    }

    // Отправка сообщения
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_MESSAGE + e.getMessage());
        }
    }
}