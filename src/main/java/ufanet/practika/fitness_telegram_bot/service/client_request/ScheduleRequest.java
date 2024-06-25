package ufanet.practika.fitness_telegram_bot.service.client_request;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public class ScheduleRequest extends ClientBaseRequest {
    @Override
    public void setNext(List<ClientRequest> clientRequests) {
    }

    @Override
    public void processRequest(CallbackQuery callbackQuery) {
    }
}
