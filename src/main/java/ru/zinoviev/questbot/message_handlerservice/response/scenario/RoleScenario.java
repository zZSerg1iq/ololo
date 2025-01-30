package ru.zinoviev.questbot.message_handlerservice.response.scenario;

import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;

import java.util.List;

public interface RoleScenario {
    ResponseUpdateDTO proceedScenario(TGUpdateDTO update);

    default ResponseUpdateDTO getDefaultMessage(TGMessageDTO message, String messageText, MessageType type){
        return ResponseUpdateDTO
                .builder()
                .messageList(
                        List.of(
                                ResponseMessageDTO.builder()
                                        .messageType(type)
                                        .messageId(message.getMessageId())
                                        .chatId(message.getChatId())
                                        .text(messageText)
                                        .build()
                        )
                )
                .build();
    }
}
