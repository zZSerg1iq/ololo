package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.service.RunningQuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.impl.QuestRepositoryServiceImpl;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard.ResponseKeyboardMarkupDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.KeyboardType;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.response.InviteLinkService;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;

import java.util.List;

@Component("userMessage")
@RequiredArgsConstructor
public class UserMessageService implements RoleScenario {

    private final ResponseMessageService messageService;
    private final ReplyKeyboardService replyKeyboardService;
    private final RunningQuestRepositoryService runningQuestService;
    private final InviteLinkService inviteLinkService;
    private final QuestRepositoryServiceImpl questRepositoryServiceImpl;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        if (update.getMessage().getText() == null) {
            return getDefaultMessage(update.getMessage(),
                    "UserMessageService: я не знаю такой команды", MessageType.MESSAGE);
        }

        String messageText = update.getMessage().getText().toLowerCase();
        TGMessageDTO message = update.getMessage();

        if (messageText.startsWith("qlink:")) {
            return playQuest(message);
        }

        return switch (messageText) {
            case "аккаунт" -> getAccountMenu(message);
            case "квесты" -> getQuestMenu(message);

            default -> getDefaultMessage(update.getMessage(),
                    "UserMessageService: я не знаю такой команды", MessageType.MESSAGE);
        };
    }

    private ResponseUpdateDTO playQuest(TGMessageDTO message) {
        List<Long> result = inviteLinkService.getDecodedString(message.getText());
        UserInfo userInfo = questRepositoryServiceImpl.playQuest(message.getChatId(), result.get(1));

        ResponseUpdateDTO responseUpdateDTO = new ResponseUpdateDTO();

        if (userInfo == null){
            ResponseMessageDTO responseMessage = messageService.getMessage(message);
            responseMessage.setText("Вы уже завершили этот квест");
            responseUpdateDTO.setMessageList(List.of(responseMessage));
        } else {
            ResponseMessageDTO responseMessage = messageService.getMessage(message);
            responseMessage.setReplyMarkup(replyKeyboardService.getFirstQuestNodeInlineKeyboard());
            responseMessage.setText("Вы участвуете в квесте");

            ResponseMessageDTO startQuestKeyBoardMessage = messageService.getMessage(message);
            startQuestKeyBoardMessage.setReplyMarkup(userInfo.getScenario().equals(Scenario.ADMIN) ?
                    replyKeyboardService.getAdminReplyKeyboard() :
                    replyKeyboardService.getPlayerReplyKeyboard());
            startQuestKeyBoardMessage.setText("Когда будете готовы, нажмите 'Начать испытание'");

            responseUpdateDTO.setMessageList(List.of(responseMessage, startQuestKeyBoardMessage));
        }

        return responseUpdateDTO;
    }

    private ResponseUpdateDTO getAccountMenu(TGMessageDTO message) {
        ResponseMessageDTO responseMessage = messageService.getMessage(message);

        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton("Статистика", "user_info");
        markup.addInlineButton("Изменить имя", "drop_nickname");
        //markup.addInlineButton("Мои друзья", "friend_list");
        //markup.addInlineButton("Удалить аккаунт", "delete_account");
        markup.addInlineButton("Отмена", "cancel");
        responseMessage.setReplyMarkup(markup);

        responseMessage.setText("Пожалуйста, выберите действие");

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(responseMessage))
                .build();
    }

    private ResponseUpdateDTO getQuestMenu(TGMessageDTO message) {
        Long runningQuestCount = runningQuestService.findRunningQuestCountByUserTelegramId(message.getChatId());

        System.out.println(runningQuestCount);
        ResponseMessageDTO responseMessage = messageService.getMessage(message);
        responseMessage.setReplyMarkup(replyKeyboardService.getQuestMainMenuInlineKeyboard(runningQuestCount));
        responseMessage.setText("Пожалуйста, выберите действие");

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(responseMessage))
                .build();
    }


}