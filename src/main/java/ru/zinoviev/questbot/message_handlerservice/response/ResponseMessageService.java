package ru.zinoviev.questbot.message_handlerservice.response;

import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;

@Component
public class ResponseMessageService {

    public ResponseMessageDTO getEditMessage(TGMessageDTO message) {
        ResponseMessageDTO responseMessageDTO = getMessage(message);
        responseMessageDTO.setMessageType(MessageType.EDIT_MESSAGE);
        responseMessageDTO.setMessageId(message.getMessageId());
        return responseMessageDTO;
    }

    public ResponseMessageDTO getDeleteMessage(TGMessageDTO message) {
        ResponseMessageDTO responseMessageDTO = getMessage(message);
        responseMessageDTO.setMessageType(MessageType.DELETE);
        responseMessageDTO.setMessageId(message.getMessageId());
        return responseMessageDTO;
    }

    public ResponseMessageDTO getMessage(TGMessageDTO message) {
        return ResponseMessageDTO.builder()
                .messageType(MessageType.MESSAGE)
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .text("default text")
                .build();
    }
}
