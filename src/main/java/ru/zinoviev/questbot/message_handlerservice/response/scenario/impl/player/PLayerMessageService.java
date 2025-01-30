package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.service.PlayerRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.RunningQuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;

import java.util.List;

@Component("playerMessage")
@RequiredArgsConstructor
public class PLayerMessageService implements RoleScenario {

    private final PlayerRepositoryService playerRepositoryService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService replyKeyboardService;


    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        String command = update.getMessage().getText();

        if (update.getPollAnswerDTO() != null) {
            return processAndContinue(update);
        }

        return switch (command) {
            case "Выйти из квеста" -> quitQuest(update);

            //on unexpected command
            default -> getDefaultMessage(update.getMessage(),
                    "PLayerMessageService: Мне незнакома такая команда", MessageType.MESSAGE);
        };
    }


    private ResponseUpdateDTO quitQuest(TGUpdateDTO update) {
        ResponseMessageDTO responseMessage = messageService.getMessage(update.getMessage());
        responseMessage.setText("После выхода из квеста ваше участие в нем будет приостановлено а заработанные очки " +
                "сохранены. Вы всегда можете вернуться и продолжить проходить квест, воспользовавшись меню квестов или ссылкой на этот квест.\n\n" +
                "Вы уверены что хотите покинуть квест?");
        responseMessage.setReplyMarkup(replyKeyboardService.getQuitQuestConfirmationInlineKeyboard());

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage))
                .build();
    }




    private ResponseUpdateDTO processAndContinue(TGUpdateDTO update) {
        List<ResponseMessageDTO> responseMessageDTOs = playerRepositoryService.registerAnswerAndContinue(update);
        return ResponseUpdateDTO.builder()
                .messageList(responseMessageDTOs)
                .build();
    }


}