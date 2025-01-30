package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.service.PlayerRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.RunningQuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.player.PLayerMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.player.PlayerCallbackService;

import java.util.List;

@Component("adminMessage")
@RequiredArgsConstructor
public class AdminMessageService implements RoleScenario {

    private final PLayerMessageService pLayerMessageService;
    private final PlayerRepositoryService playerRepositoryService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService replyKeyboardService;
    private final RunningQuestRepositoryService runningQuestRepositoryService;
    private final PlayerCallbackService playerCallback;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {

        if (update.getPollAnswerDTO() != null) {
            return pLayerMessageService.proceedScenario(update);
        }

        String command = update.getMessage().getText();
        TGMessageDTO message = update.getMessage();

        return switch (command) {
            case "Выйти из квеста" -> pLayerMessageService.proceedScenario(update);

            case "Остановить квест" -> stopQuest(update);

            case "Игроки" -> playersMenu(message);
            case "Квест" -> questMenu(message);

            //on unexpected command
            default -> getDefaultMessage(update.getMessage(),
                    "AdminMessageService: Мне незнакома такая команда", MessageType.MESSAGE);
        };
    }


    private ResponseUpdateDTO stopQuest(TGUpdateDTO update) {
        QuestInfo runningQuestInfo = runningQuestRepositoryService.getQuestInfoByPlayerId(update.getFrom().getUserId());

        ResponseMessageDTO stopRunningQuestConfirmationMessage = messageService.getMessage(update.getMessage());
        stopRunningQuestConfirmationMessage.setReplyMarkup(
                replyKeyboardService.getStopRunningQuestConfirmationInlineKeyboard(runningQuestInfo));
        stopRunningQuestConfirmationMessage.setText("После остановки квеста результаты игроков не успевших его завершить не будут зарегистрированы. " +
                "Вы уверены, что хотите остановить квест?");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(stopRunningQuestConfirmationMessage))
                .build();
    }

//    private ResponseUpdateDTO stopQuest(TGUpdateDTO update) {
//        Long questId = Long.parseLong(update.getCallbackQuery().getData().split(":")[1]);
//
//        QuestInfo questInfo = runningQuestRepositoryService.getQuestInfoByQuestId(questId);
//
//        ResponseMessageDTO stopRunningQuestConfirmationMessage = messageService.getMessage(update.getMessage());
//        stopRunningQuestConfirmationMessage.setReplyMarkup(
//                replyKeyboardService.getStopRunningQuestConfirmationKeyboard(questInfo));
//        stopRunningQuestConfirmationMessage.setText("После остановки квеста результаты игроков не успевших его завершить не будут зарегистрированы. " +
//                "Вы уверены, что хотите остановить квест?");
//
//        return ResponseUpdateDTO.builder()
//                .messageList(List.of(stopRunningQuestConfirmationMessage))
//                .build();
//    }


    private ResponseUpdateDTO playersMenu(TGMessageDTO message) {
        return null;
    }

    private ResponseUpdateDTO questMenu(TGMessageDTO message) {
        return null;
    }
}