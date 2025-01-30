package ru.zinoviev.questbot.message_handlerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard.ResponseKeyboardMarkupDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessageDTO {

    private MessageType messageType;

    //SendMessage
    private Long chatId;
    private String inlineMessageId;
    private String text;
    private String parseMode;
    private ResponseKeyboardMarkupDTO replyMarkup;
    private ResponseUserDTO responseUserDTO;

    //EditedMessage
    private Integer messageId;
    private ResponsePollDTO pollDTO;
}
