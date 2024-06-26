package ufanet.practika.fitness_telegram_bot.service.user_strategy;

import org.telegram.telegrambots.meta.api.objects.Update;
import ufanet.practika.fitness_telegram_bot.service.ClientService;
import ufanet.practika.fitness_telegram_bot.service.TelegramBot;
import ufanet.practika.fitness_telegram_bot.service.user_chain.client_chain.*;


public class ClientStrategy implements UserStrategy{
    private final ClientService clientService;
    private final TelegramBot telegramBot;

    public ClientStrategy(ClientService clientService, TelegramBot telegramBot) {
        this.clientService = clientService;
        this.telegramBot = telegramBot;
    }

    @Override
    public void execute(Update update) {
        ClientBaseChain baseChain = ClientBaseChain.link(
                new MainWindowChain(clientService, telegramBot),
                new ScheduleClientChain(clientService, telegramBot),
                new AdditionalInfoChain(clientService, telegramBot),
                new CancelLessonChain(clientService, telegramBot)
        );

        long chatId = update.getMessage().getChatId();
        long messageId = update.getMessage().getMessageId();
        baseChain.process(chatId, messageId, update.getCallbackQuery());
    }
}
