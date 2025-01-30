package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestType;
import ru.zinoviev.questbot.message_handlerservice.response.InviteLinkService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.QuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.RunningQuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.UserRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUserDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard.ResponseKeyboardMarkupDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard.ResponseKeyboardRowDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.KeyboardType;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;
import ru.zinoviev.questbot.message_handlerservice.enums.RegistrationStatus;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.response.RegistrationService;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.user.service.FriendService;

import java.util.List;

@Component("userCallback")
@RequiredArgsConstructor
public class UserCallbackService implements RoleScenario {

    private final RegistrationService registrationService;
    private final UserRepositoryService userService;
    private final QuestRepositoryService questService;
    private final FriendService friendService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService replyKeyboardService;
    private final RunningQuestRepositoryService runningQuestService;
    private final InviteLinkService inviteLinkService;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        String callbackData = update.getCallbackQuery().getData();
        if (callbackData.contains(":")) {
            callbackData = callbackData.split(":")[0];
        }

        TGMessageDTO message = update.getMessage();

        return switch (callbackData) {
            //Account menu
            case "drop_nickname" -> dropNickname(update);
            case "friend_list" -> getFriendList(update);
            case "user_info" -> getUserInfo(update);

            //case "delete_account" -> deleteMyAccount(update);

            //Quest menu
            case "quest_list" -> getUserOwnQuestList(message);
            case "running_quest_list" -> showRunningQuestList(message);
            case "run_my_quest" -> runQuestMenu(message);
            case "create_quest" -> createQuestMenu(message);
            case "create_quiz" -> createQuizz(update);
            case "create_linear" -> createLinear(update);

            //user questList: select quest action
            case "quest_Id" -> selectedQuestAction(update);

            //selected quest menu
            case "run_quest" -> runSelectedQuestConfirm(update);
            case "stop_quest_id" -> stopSelectedQuest(update);
            case "edit_quest" -> editSelectedQuest(message);
            case "remove_quest" -> removeSelectedQuest(update);
            case "play_quest_id" -> playSelectedQuest(update);

            case "run_selected_quest" -> runSelectedQuest(update);

            //friends menu
            case "friend_action_remove", "friend_action_disable" -> friendService.selectAction(update);

            case "cancel" -> getDefaultMessage(update.getMessage(), "Вы отменили действие", MessageType.EDIT_MESSAGE);

            //on unexpected command
            default ->
                    getDefaultMessage(update.getMessage(), "UserCallbackService: Мне незнакома такая команда", MessageType.EDIT_MESSAGE);
        };
    }

    private ResponseUpdateDTO showRunningQuestList(TGMessageDTO message) {
        List<QuestInfo> runningQuestList = runningQuestService.findRunningUserQuestsByUserTelegramId(message.getChatId());

        System.out.println(runningQuestList);

        ResponseMessageDTO runningQuestMessage = messageService.getEditMessage(message);
        runningQuestMessage.setReplyMarkup(replyKeyboardService.getRunningQuestInlineKeyboard(runningQuestList));
        runningQuestMessage.setText("Выберите квест, который хотите остановить:");


        return ResponseUpdateDTO.builder()
                .messageList(List.of(runningQuestMessage))
                .build();
    }

    private ResponseUpdateDTO playSelectedQuest(TGUpdateDTO update) {
        String[] callbackDataAndQuestId = update.getCallbackQuery().getData().split(":");
        if (callbackDataAndQuestId.length != 2) {
            return getDefaultMessage(update.getMessage(), "Какая-то ошибка в получении данных квеста", MessageType.EDIT_MESSAGE);
        }
        long questId = Long.parseLong(callbackDataAndQuestId[1]);
        Long userTelegramId = update.getFrom().getUserId();

        ResponseUpdateDTO responseUpdateDTO = new ResponseUpdateDTO();

        UserInfo userInfo = questService.playQuest(userTelegramId, questId);

        if (userInfo == null){
            ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
            responseMessage.setText("Вы уже завершили этот квест");
            responseUpdateDTO.setMessageList(List.of(responseMessage));
        } else {
            ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
            responseMessage.setReplyMarkup(replyKeyboardService.getFirstQuestNodeInlineKeyboard());
            responseMessage.setText("Вы участвуете в квесте");

            ResponseMessageDTO startQuestKeyBoardMessage = messageService.getMessage(update.getMessage());
            startQuestKeyBoardMessage.setReplyMarkup(userInfo.getScenario().equals(Scenario.ADMIN) ?
                    replyKeyboardService.getAdminReplyKeyboard() :
                    replyKeyboardService.getPlayerReplyKeyboard());
            startQuestKeyBoardMessage.setText("Когда будете готовы, нажмите 'Начать испытание'");

            responseUpdateDTO.setMessageList(List.of(responseMessage, startQuestKeyBoardMessage));
        }

        return responseUpdateDTO;
    }

    private ResponseUpdateDTO getUserInfo(TGUpdateDTO update) {
        Long userId = update.getFrom().getUserId();
        UserInfo userInfo = userService.getUserInfoByUserId(userId);

        ResponseMessageDTO userInfoDto = messageService.getEditMessage(update.getMessage());
        userInfoDto.setText(
                "Имя:" + userInfo.getNickName() + " \n" +
                        "Заработано очков:" + userInfo.getPoints() + " \n" +
                        "Друзей: " + userInfo.getFriendCount() + " \n" +
                        "Квестов: " + userInfo.getQuestCount() + " \n"
        );
        return ResponseUpdateDTO.builder()
                .messageList(List.of(userInfoDto))
                .build();
    }

    private ResponseUpdateDTO stopSelectedQuest(TGUpdateDTO update) {
        String[] callbackDataAndQuestId = update.getCallbackQuery().getData().split(":");
        if (callbackDataAndQuestId.length != 2) {
            return getDefaultMessage(update.getMessage(), "Какая-то ошибка в получении данных квеста", MessageType.EDIT_MESSAGE);
        }
        long questId = Long.parseLong(callbackDataAndQuestId[1]);

        runningQuestService.stopRunningQuest(questId);

        ResponseMessageDTO editMessage = messageService.getEditMessage(update.getMessage());
        editMessage.setText("Квест завершен! \nВсем игрокам были начислены заработанные баллы.");

        ResponseMessageDTO userReplyKeyboardMessage = messageService.getMessage(update.getMessage());
        userReplyKeyboardMessage.setReplyMarkup(replyKeyboardService.getUserReplyKeyboard());
        userReplyKeyboardMessage.setText("\uD83E\uDD73");


        return ResponseUpdateDTO.builder()
                .messageList(List.of(editMessage, userReplyKeyboardMessage))
                .build();
    }

    private ResponseUpdateDTO runSelectedQuestConfirm(TGUpdateDTO update) {
        String[] callbackDataAndQuestId = update.getCallbackQuery().getData().split(":");
        if (callbackDataAndQuestId.length != 2) {
            return getDefaultMessage(update.getMessage(), "Какая-то ошибка в получении данных квеста", MessageType.EDIT_MESSAGE);
        }
        long questId = Long.parseLong(callbackDataAndQuestId[1]);

        ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
        responseMessage.setReplyMarkup(replyKeyboardService.getConfirmRunQuestInlineKeyboard(questId));
        responseMessage.setText("Запустить квест?");

        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO runSelectedQuest(TGUpdateDTO update) {
        String[] callbackDataAndQuestId = update.getCallbackQuery().getData().split(":");
        if (callbackDataAndQuestId.length != 2) {
            return getDefaultMessage(update.getMessage(), "Какая-то ошибка в получении данных квеста", MessageType.EDIT_MESSAGE);
        }

        long questId = Long.parseLong(callbackDataAndQuestId[1]);
        long userId = update.getMessage().getChatId();

        QuestInfo questInfo = questService.runQuest(questId, userId);

        ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
        responseMessage.setText("\uD83C\uDF6D Квест '"+questInfo.getQuestName()+"' запущен! \uD83C\uDF6D");

        ResponseMessageDTO linkMessage = messageService.getMessage(update.getMessage());
        linkMessage.setText("Ссылка, которой можно поделиться или ввести самому:\n\n" +
                "qlink:"+inviteLinkService.getInviteLink(questInfo.getQuestName(), questInfo.getId(), userId)+
        "\n\nЕй можно поделиться со с");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage, linkMessage))
                .build();
    }

    private ResponseUpdateDTO editSelectedQuest(TGMessageDTO message) {
        //TODO +++++++++++++++++++++++++++++++++
        return getDefaultMessage(message, "UserCallbackService: Мне незнакома такая команда", MessageType.EDIT_MESSAGE);
    }

    private ResponseUpdateDTO removeSelectedQuest(TGUpdateDTO update) {
        long questId = Long.parseLong(update.getCallbackQuery().getData().split(":")[1]);
        questService.removeQuest(questId);

        TGMessageDTO message = update.getMessage();
        return getDefaultMessage(message, "Ваш квест будет удален окончательно через 60 дней. За это время вы всегда можете восстановить его, если вдруг передумали.", MessageType.EDIT_MESSAGE);
    }

    private ResponseUpdateDTO selectedQuestAction(TGUpdateDTO update) {
        long questId = Long.parseLong(update.getCallbackQuery().getData().split(":")[1]);

        QuestInfo runningQuestInfo = runningQuestService.getQuestInfoByQuestId(questId);

        ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
        responseMessage.setText("Выберите действие, которое хотите выполнить");
        responseMessage.setReplyMarkup(
                runningQuestInfo != null ?
                        replyKeyboardService.getRunningQuestOptionsInlineKeyboard(runningQuestInfo.getId()) :
                        replyKeyboardService.getQuestOptionsInlineKeyboard(questId)
        );


        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO createLinear(TGUpdateDTO update) {
        TGUserDTO user = update.getFrom();
        questService.startQuestCreating(user.getUserId(), QuestType.LINEAR);

        ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
        responseMessage.setText("Запущено создание линейного квеста");

        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO createQuizz(TGUpdateDTO update) {
        TGUserDTO user = update.getFrom();
        questService.startQuestCreating(user.getUserId(), QuestType.POLL);

        //edit message
        ResponseMessageDTO createQuestStartedMessage = messageService.getEditMessage(update.getMessage());
        createQuestStartedMessage.setText("--Запущено создание квиз-квеста--");

        //new keyboardMessage
        ResponseMessageDTO addQuestCreationKeyboard = messageService.getMessage(update.getMessage());
        addQuestCreationKeyboard.setReplyMarkup(replyKeyboardService.getUserQuestCreationReplyKeyboard());
        addQuestCreationKeyboard.setText("Создание квиза очень просто:\n" +
                "Введите вопрос и с каждой новой строки варианты ответов. Самой последней строкой напишите 'ok:' и номера верных ответов.\n\n" +
                "Пример:\n\n" +
                "Какое из следующих живых существ умеет летать?\n" +
                "Кошка\n" +
                "Птица\n" +
                "Бабочка\n" +
                "Собака\n" +
                "ok:2,3\n\n\n" +
                "Какие зрачки у шиншиллы?\n" +
                "Круглые\n" +
                "Квадратные\n" +
                "Горизонтальные\n" +
                "Вертикальные\n" +
                "ok:4\n");

        ResponseMessageDTO createQuestStartedMessage2 = messageService.getMessage(update.getMessage());
        createQuestStartedMessage2.setText("Так же вы можете присылать вопросы квеста с помощью инструмента 'Голосоввание', в котором так же надо указать последним вариантом верные ответы в фаормате 'ОК:2,4...' " +
                "\n\n Когда закончите, нажмите кнопку 'Завершить'.");


        return ResponseUpdateDTO.builder()
                .messageList(List.of(createQuestStartedMessage, addQuestCreationKeyboard, createQuestStartedMessage2))
                .build();
    }

    private ResponseUpdateDTO deleteMyAccount(TGUpdateDTO update) {
        userService.deleteUser(update.getFrom().getUserId());

        ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
        responseMessage.setText("ResponseUpdate - deleteMyAccount");

        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO runQuestMenu(TGMessageDTO message) {
        List<QuestInfo> questInfoList = questService.getQuestInfoListByUserId(message.getChatId());
        List<QuestInfo> runningQuestInfo = runningQuestService.findRunningUserQuestsByUserTelegramId(
                message.getChatId());

        if (questInfoList.isEmpty() && runningQuestInfo.isEmpty()) {
            return getDefaultMessage(message, "К сожалению у вас нет ни одного квеста, который можно было бы запустить.", MessageType.EDIT_MESSAGE);
        }

        questInfoList.removeAll(runningQuestInfo);
        if (questInfoList.isEmpty()) {
            return getDefaultMessage(message, "Все ваши квесты уже запущены", MessageType.EDIT_MESSAGE);
        }

        ResponseMessageDTO responseMessage = messageService.getEditMessage(message);
        responseMessage.setReplyMarkup(replyKeyboardService.getSelectQuestToRunMenuInlineKeyboard(questInfoList));
        responseMessage.setText("Список квестов, которые можно запустить");

        return ResponseUpdateDTO.builder()
                .messageList(List.of(responseMessage))
                .build();
    }

    private ResponseUpdateDTO createQuestMenu(TGMessageDTO message) {
        ResponseMessageDTO responseMessage = messageService.getEditMessage(message);
        responseMessage.setReplyMarkup(replyKeyboardService.getCreateQuestTypeMenuInlineKeyboard());
        responseMessage.setText("Выберите тип квеста");

        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO getUserOwnQuestList(TGMessageDTO message) {
        List<QuestInfo> questInfoList = questService.getQuestInfoListByUserId(message.getChatId());

        if (questInfoList.isEmpty()) {
            return getDefaultMessage(message, "К сожалению у вас пока нет ни одного квеста", MessageType.EDIT_MESSAGE);
        }

        ResponseMessageDTO responseMessage = messageService.getEditMessage(message);
        responseMessage.setText("Вот список ваших квестов!");
        responseMessage.setReplyMarkup(replyKeyboardService.getUserQuestListInlineKeyboard(questInfoList));

        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO getFriendList(TGUpdateDTO update) {
        List<UserInfo> friendList = userService.getFriends(update.getFrom().getUserId());

        ResponseMessageDTO responseMessage = messageService.getEditMessage(update.getMessage());
        responseMessage.setReplyMarkup(replyKeyboardService.getFriendListInlineKeyboard(friendList));
        responseMessage.setText("Друзья добавленные в список будут получать приглашение на запущенный вами квест автоматически.");

        return ResponseUpdateDTO.builder().messageList(List.of(responseMessage)).build();
    }

    private ResponseUpdateDTO dropNickname(TGUpdateDTO update) {
        update.getCallbackQuery().setData("registration_custom_name");
        return registrationService.getNextRegistrationStep(update, RegistrationStatus.REGISTRATION_CONFIRM_NAME);
    }
}
