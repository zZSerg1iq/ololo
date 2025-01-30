package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendService {

    private final ResponseMessageService messageService;

    public ResponseUpdateDTO selectAction(TGUpdateDTO update) {
        TGMessageDTO message = update.getMessage();

        ResponseMessageDTO responseMessage = messageService.getEditMessage(message);
        responseMessage.setText("FriendService - selectAction");

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(responseMessage))
                .build();
    }

}
