package ru.zinoviev.questbot.message_handlerservice.domain.service;

import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;

import java.util.List;

public interface PlayerRepositoryService {
    List<ResponseMessageDTO> getNodeEventByPlayerId(TGMessageDTO userTelegramId);

    List<ResponseMessageDTO> registerAnswerAndContinue(TGUpdateDTO update);

    void quitQuest(Long chatId);
}
