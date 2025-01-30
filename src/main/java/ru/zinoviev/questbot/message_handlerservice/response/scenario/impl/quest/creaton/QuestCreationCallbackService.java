package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.quest.creaton;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestStatus;
import ru.zinoviev.questbot.message_handlerservice.domain.service.QuestRepositoryService;
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

@Component("questCreationCallback")
@RequiredArgsConstructor
public class QuestCreationCallbackService implements RoleScenario {

    private final QuestRepositoryService questRepositoryService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService keyboardService;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        String callbackData = update.getCallbackQuery().getData();
        if (callbackData.contains(":")){
            callbackData = callbackData.split(":")[0];
        }

        QuestInfo questInfo = questRepositoryService.getQuestInfoByUserId(update.getMessage().getChatId());

        return switch (callbackData) {
            case "stop_quest_creating" -> questCreationStop(update, questInfo);

            //set quest name
            case "q_name" -> questConfirmName(update.getMessage(), update.getCallbackQuery().getData(),  questInfo);

            case "cancel" -> getDefaultMessage(update.getMessage(),
                    "Вы отменили действие", MessageType.EDIT_MESSAGE);
            //on unexpected command
            default -> getDefaultMessage(update.getMessage(),
                    "UserCallbackService: Мне незнакома такая команда", MessageType.EDIT_MESSAGE);
        };
    }

    private ResponseUpdateDTO questConfirmName(TGMessageDTO message, String callbackData, QuestInfo questInfo) {
        if (!questInfo.getQuestStatus().equals(QuestStatus.EDITING_NAME)){
            return getDefaultMessage(message,
                    "Вы уже завершили этап ввода имени квеста", MessageType.EDIT_MESSAGE);
        }

        long questId = questInfo.getId();
        String questName = callbackData.split(":")[1];
        questRepositoryService.addQuestName(questId, questName);

        ResponseMessageDTO responseMessage = messageService.getEditMessage(message);
        responseMessage.setText("И завершающий этап! Ведите описание квеста, оно будет показано игрокам при запуске.");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage))
                .build();
    }



    private ResponseUpdateDTO questCreationStop(TGUpdateDTO update, QuestInfo questInfo) {
        TGMessageDTO requestMessage = update.getMessage();
        long userId = update.getFrom().getUserId();

        //quest is empty
        if (!questRepositoryService.finishQuestCreating(questInfo.getId())){
            questRepositoryService.removeEmptyQuest(userId, questInfo.getId());

            ResponseMessageDTO keyboardDelete = messageService.getMessage(requestMessage);
            keyboardDelete.setReplyMarkup(keyboardService.getUserReplyKeyboard());
            keyboardDelete.setText("\uD83D\uDE22");

            ResponseMessageDTO message1 = messageService.getEditMessage(requestMessage);
            message1.setText("Вы всегда будем ждать вас снова!");

            return ResponseUpdateDTO.builder()
                    .messageList(List.of(keyboardDelete, message1))
                    .build();
        }

        ResponseMessageDTO keyboardDelete = messageService.getMessage(requestMessage);
        keyboardDelete.setReplyMarkup(keyboardService.replyKeyboardRemoveAction());
        keyboardDelete.setText("\uD83D\uDC4D");

        ResponseMessageDTO message1 = messageService.getEditMessage(requestMessage);
        message1.setText("Отлично, теперь придумайте название для квеста. Внимание! ДЛИНА НАЗВАНИЯ НЕ ДОЛЖНА ПРЕВЫШАТЬ 30 СИМВОЛОВ. Название квеста должно отражать его суть - просто и понятно. Например 'Тест по географии европы' или 'Знание правил русского языка'.");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(keyboardDelete, message1))
                .build();
    }


}