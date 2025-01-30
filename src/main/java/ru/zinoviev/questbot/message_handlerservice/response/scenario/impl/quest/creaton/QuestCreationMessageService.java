package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.quest.creaton;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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

@Component("questCreationMessage")
@RequiredArgsConstructor
public class QuestCreationMessageService implements RoleScenario {

    private final QuestRepositoryService questRepositoryService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService keyboardService;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        long userId = update.getFrom().getUserId();
        QuestInfo questInfo = questRepositoryService.getQuestInfoByUserId(userId);

        return switch (questInfo.getQuestStatus()){
            case UNDER_CONSTRUCTION -> selectAction(update.getMessage(), questInfo);
            case EDITING_NAME -> addQuestName(update.getMessage(), questInfo);
            case EDITING_DESCRIPTION -> addQuestDescription(update.getMessage(), questInfo);
            default -> getDefaultMessage( update.getMessage(), "Что то пошло не так", MessageType.MESSAGE);
        };
    }

    private ResponseUpdateDTO selectAction(TGMessageDTO message, QuestInfo questInfo) {
        String command = message.getText();

        if (command != null) {
            return switch (command) {
                case "Завершить" -> confirmExitQuestCreation(message);
                case "Помощь" -> showQuestCreationHelp(message);
                default -> getDefaultMessage(message,
                        "Мне незнакома такая команда", MessageType.MESSAGE);
            };
        }

        return switch (questInfo.getQuestType()){
            case POLL -> addPollQuestNode(message, questInfo);
            case LINEAR -> addLinearQuestNode(message, questInfo);
        };
    }

    private ResponseUpdateDTO addQuestName(TGMessageDTO message, QuestInfo questInfo) {
        if (message.getText() == null){
            return getDefaultMessage(message,
                    "Я ожидаю совсем не это :)", MessageType.MESSAGE);
        }
        if (message.getText().length() > 30){
            return getDefaultMessage(message,
                    "Ваше название слишком длинное: "+message.getText().length() , MessageType.MESSAGE);
        }

        return addQuestName(message);
    }

    private ResponseUpdateDTO addQuestDescription(TGMessageDTO message, QuestInfo questInfo) {
        if (message.getText() == null){
            return getDefaultMessage(message,
                    "Я ожидаю совсем не это :)", MessageType.MESSAGE);
        }

        return addQuestDescription(message, questInfo.getId());
    }

    private ResponseUpdateDTO confirmExitQuestCreation(TGMessageDTO message) {
        ResponseMessageDTO responseMessage = messageService.getMessage(message);
        responseMessage.setText("Вы уверены что хотите завершить создание квеста?");
        responseMessage.setReplyMarkup(new ReplyKeyboardService().getConfirmQuestCreationStopInlineKeyboard());

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage))
                .build();
    }

    private ResponseUpdateDTO showQuestCreationHelp(TGMessageDTO message) {
        ResponseMessageDTO responseMessage = messageService.getMessage(message);
        responseMessage.setText("ПОООООООООООООООООООООООООООООМООООООООООООООООООГАААААААААААААААААЮЮЮЮЮЮЮЮЮЮЮЮЮЮЮЮЮЮ");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage))
                .build();
    }

    private ResponseUpdateDTO addPollQuestNode(TGMessageDTO message, QuestInfo questInfo) {

        if (message.getPoll() != null) {
            if (questRepositoryService.addPollQuestNode(message.getPoll(), questInfo.getId())) {
                return getDefaultMessage(message, "Новый этап квеста добавлен! \uD83D\uDE0E", MessageType.MESSAGE);
            } else {
                return getDefaultMessage(message, "Произошла ошибка. Элемент квеста не был добавлен. Возможно вы указали некорректные индексы верных ответов или не указали их вовсе. Пожалуйста, попробуйте снова.", MessageType.MESSAGE);
            }
        }

        return getDefaultMessage( message, "Это немного не то, что я ожидаю во время создания такого квеста. Если возникают затруднения, воспользуйтесь кнопкой 'Помощь'", MessageType.MESSAGE);
    }

    private ResponseUpdateDTO addLinearQuestNode(TGMessageDTO message, QuestInfo questInfo) {
        if (message.getPoll() != null) {
            questRepositoryService.addLinearQuestNode(message, questInfo.getId());
            return getDefaultMessage(message, "Новый этап квеста добавлен! \uD83D\uDE0E", MessageType.MESSAGE);
        }

        return null;
    }

    private ResponseUpdateDTO addQuestName(TGMessageDTO message) {
        ResponseMessageDTO responseMessage = messageService.getMessage(message);
        responseMessage.setReplyMarkup(keyboardService.getConfirmQuestNameInlineKeyboard(message.getText()));
        responseMessage.setText("Квест: '"+message.getText()+"' ");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage))
                .build();
    }

    private ResponseUpdateDTO addQuestDescription(TGMessageDTO message, Long questId) {
        questRepositoryService.addQuestDescriptionAndStopCreatingQuest(questId, message.getText());

        ResponseMessageDTO responseMessage = messageService.getMessage(message);
        responseMessage.setReplyMarkup(keyboardService.getUserReplyKeyboard());
        responseMessage.setText("Описание добавлено");

        ResponseMessageDTO responseMessage2 = messageService.getMessage(message);
        responseMessage2.setText("Вы завершили создание квеста! \uD83D\uDE0C");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage,responseMessage2))
                .build();
    }





}
