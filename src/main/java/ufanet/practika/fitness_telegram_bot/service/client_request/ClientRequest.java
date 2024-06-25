package ufanet.practika.fitness_telegram_bot.service.client_request;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public interface ClientRequest {
    void setNext(List<ClientRequest> clientRequests);
    void processRequest(CallbackQuery callbackQuery);
}
