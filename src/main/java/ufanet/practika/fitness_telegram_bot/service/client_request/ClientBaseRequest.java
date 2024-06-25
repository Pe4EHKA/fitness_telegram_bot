package ufanet.practika.fitness_telegram_bot.service.client_request;

import ufanet.practika.fitness_telegram_bot.service.ClientService;

import java.util.List;

public abstract class ClientBaseRequest implements ClientRequest {
    private List<ClientRequest> clientRequest;

    private ClientService clientService;
}