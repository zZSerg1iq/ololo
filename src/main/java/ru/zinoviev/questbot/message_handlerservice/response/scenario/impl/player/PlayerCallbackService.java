package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.player;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.service.PlayerRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;

import java.util.List;

@Component("playerCallback")
@RequiredArgsConstructor
public class PlayerCallbackService implements RoleScenario {

    private final PlayerRepositoryService playerRepositoryService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService replyKeyboardService;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        String callbackData = update.getCallbackQuery().getData();
        if (callbackData.contains(":")) {
            callbackData = callbackData.split(":")[0];
        }

        return switch (callbackData) {
            case "get_first_quest_node" -> getNodeEvent(update.getMessage());
            case "quit_quest_confirm" -> quitQuest(update.getMessage());

            //on unexpected command
            default -> getDefaultMessage(update.getMessage(),
                    "PlayerCallbackService: Мне незнакома такая команда", MessageType.EDIT_MESSAGE);
        };
    }


    private ResponseUpdateDTO getNodeEvent(TGMessageDTO message) {
        List<ResponseMessageDTO> questNodeMessages = playerRepositoryService.getNodeEventByPlayerId(message);

        return ResponseUpdateDTO.builder()
                .messageList(questNodeMessages)
                .build();
    }

    private ResponseUpdateDTO quitQuest(TGMessageDTO message) {
        playerRepositoryService.quitQuest(message.getChatId());

        ResponseMessageDTO leaveQuestEditMessage = messageService.getEditMessage(message);
        leaveQuestEditMessage.setText("Вы покинули квест");

        ResponseMessageDTO userKeyboardMessage = messageService.getMessage(message);
        userKeyboardMessage.setReplyMarkup(replyKeyboardService.getUserReplyKeyboard());
        userKeyboardMessage.setText("Для возвращения воспользуйтесь ссылкой, или (если квест ваш) - меню 'Мои квесты'");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(leaveQuestEditMessage, userKeyboardMessage))
                .build();
    }
}