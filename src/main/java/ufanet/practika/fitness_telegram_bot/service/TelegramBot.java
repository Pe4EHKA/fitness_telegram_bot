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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ufanet.practika.fitness_telegram_bot.config.BotConfig;
import ufanet.practika.fitness_telegram_bot.entity.Role;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.entity.UserRole;
import ufanet.practika.fitness_telegram_bot.repository.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientService clientService;
    final BotConfig config;

    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities of Telegram Bots.\n\n" +
            "You can execute from main menu on the left or by typing command\n\n" +
            "Type /start to see welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_MESSAGE = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        super(config.getToken());
        this.config = config;
        // Настройка команд, которые будут доступны в меню
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/register", "register your self in database"));
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
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {
                switch (messageText) {
                    case "/start":
                        register(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;
                    case "/register":
                        register(update.getMessage());
                        break;
                    case "/mydata":
                        getAllDataUser(chatId);
                        break;
                    default:
                        prepareAndSendMessage(chatId, "Sorry, command was not recognized");
                }
            }
            /*
            Обработка нажатия кнопок на клавиатуре, появляющейся под сообщением
             */
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(YES_BUTTON)){
                String text = "You pressed YES button";
                executeEditMessage(text, chatId, messageId);

            } else if (callBackData.equals(NO_BUTTON)) {
                String text = "You pressed NO button";
                executeEditMessage(text, chatId, messageId);
            }
        }
    }

    /*
    Получение данных о пользователе по его chat id
     */
    private void getAllDataUser(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (userRepository.findByChatId(chatId).isEmpty()) {
            message.setText("You are not registered yet");
        } else {
            User user = userRepository.findByChatId(chatId).get();
            message.setText(user.toString());
        }
        executeMessage(message);
    }

    /*
    Регистрация пользователя в БД
     */
    private void register(Message msg) {
        if(!clientService.isExistingUser(msg.getChatId())) {
            // Создание объекта пользователя
            User user = new User();
            user.setName(msg.getChat().getFirstName());
            user.setChatId(msg.getChatId());
            user.setTelegramUserName(msg.getChat().getUserName());
            user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));

            /*
            Написано для удобства разработки: создаёт роль, если её нет в таблице ролей.
            В конце они будут там уже записаны и этот код не понадобится
             */
            Role role = null;
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

    /*
    Обработка команды /start
     */
    private void startCommandReceived(long chatId, String name)  {
        String answer = EmojiParser.parseToUnicode("Привет, " + name + ", рад тебя видеть!" + " :blush:");
        log.info("Replied to user: " + name);
        startCommandMessage(chatId, answer);
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
    Отправка сообщения после нажатия /start
     */
    private void startCommandMessage(long chatId, String textToSend)  {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    /*
    Изменяет прошлое сообщения на указанное новое
     */
    private void executeEditMessage(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);

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
