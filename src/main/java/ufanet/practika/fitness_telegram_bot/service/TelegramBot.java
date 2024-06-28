package ufanet.practika.fitness_telegram_bot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ufanet.practika.fitness_telegram_bot.config.BotConfig;
import ufanet.practika.fitness_telegram_bot.entity.Role;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.entity.UserRole;
import ufanet.practika.fitness_telegram_bot.service.user_strategy.ClientStrategy;
import ufanet.practika.fitness_telegram_bot.service.user_strategy.UserContext;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final ClientService clientService;
    static final String HELP_TEXT = """
            This bot is created to administrate your fitness training.

            You can execute from main menu on the left or by typing command

            Type /start to see welcome message

            Type /mydata to see data stored about yourself

            Type /help to see this message again""";
    static final String ERROR_MESSAGE = "Error occurred: ";
    private final BotConfig botConfig;
    private final UserContext userContext;

    @Autowired
    public TelegramBot(BotConfig config, ClientService clientService) {
        super(config.getToken());

        this.botConfig = config;
        this.userContext = new UserContext();
        this.clientService = clientService;

        // Настройка команд, которые будут доступны в меню
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать работу с ботом"));
        listOfCommands.add(new BotCommand("/mydata", "Получить свои данные"));
        listOfCommands.add(new BotCommand("/help", "Подсказка"));

        try {
            execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(ERROR_MESSAGE + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
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
            switch (messageText) {
                case "/start":
                    registerAndEnter(update.getMessage());
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

        // Обработка нажатия кнопок на клавиатуре, появляющейся под сообщением
        userContext.execute(update);
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
        Optional<User> optionalUser = clientService.getUser(msg.getChatId());
        User user;

        if(optionalUser.isEmpty()) {
            // Создание объекта пользователя
            user = new User();
            user.setName(msg.getChat().getFirstName());
            user.setChatId(msg.getChatId());
            user.setTelegramUserName(msg.getChat().getUserName());
            user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));

            // Настройка роли пользователя - клиент по-умолчанию
            Role role = clientService.getRole(UserRoles.CLIENT.toString());

            // Создание объекта, соединяющий два предыдущих объекта
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRole.setUser(user);

            clientService.registrateUser(userRole);
        } else {
            user = optionalUser.get();
        }

        String name = user.getName();
        String answer = EmojiParser.parseToUnicode("Привет, " + name + ", рад тебя видеть!"
                + " :blush:");
        log.info("Replied to user: " + name);
        prepareAndSendMessage(msg.getChatId(), answer);

        // Настройка стратегии
        userContext.setUserStrategy(new ClientStrategy(clientService, this));
    }
    /*
    Отправка сообщения
     */
    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_MESSAGE + e.getMessage());
        }
    }
}