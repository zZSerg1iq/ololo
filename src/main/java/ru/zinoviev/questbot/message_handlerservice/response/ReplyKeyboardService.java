package ru.zinoviev.questbot.message_handlerservice.response;

import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUserDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard.ResponseKeyboardMarkupDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard.ResponseKeyboardRowDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.KeyboardType;

import java.util.List;

@Component
public class ReplyKeyboardService {

    private final String REPLY_BUTTON_ACCOUNT = "Аккаунт";
    private final String REPLY_BUTTON_QUESTS = "Квесты";
    private final String REPLY_BUTTON_HELP = "Справка";
    private final String REPLY_BUTTON_QUEST_CREATION_STOP = "Завершить";
    private final String REPLY_BUTTON_LEAVE_QUEST = "Покинуть квест";
    private final String REPLY_BUTTON_STOP_RUNNING_QUEST = "Остановить квест";
    private final String REPLY_BUTTON_PLAYERS_MANAGEMENT = "Управление: Игроки";
    private final String REPLY_BUTTON_QUEST_MANAGEMENT = "Управление: Квест";

    private final String INLINE_BUTTON_REGISTRATION_USE_NAME = "Использовать имя ";
    private final String INLINE_BUTTON_REGISTRATION_ENTER_ANOTHER_NAME = "Придумать другое имя";

    private final String INLINE_BUTTON_STOP_CREATION_YES = "Да";
    private final String INLINE_BUTTON_STOP_CREATION_CANCEL = "Нет, хочу продолжить";

    private final String INLINE_BUTTON_CONFIRM_QUEST_NAME_YES = "Оставить это название";
    private final String INLINE_BUTTON_CONFIRM_QUEST_NAME_CANCEL = "Подумаю еще";

    private final String INLINE_BUTTON_QUEST_OPTION_RUN = "Запустить";
    private final String INLINE_BUTTON_QUEST_OPTION_EDIT = "Изменить";
    private final String INLINE_BUTTON_QUST_OPTION_DELETE = "Удалить";

    private final String INLINE_BUTTON_RUNNING_QUEST_STOP_ACTION = "Остановить";
    private final String INLINE_BUTTON_RUNNING_QUEST_PLAY_ACTION = "Участвовать";

    private final String INLINE_BUTTON_QUEST_MAIN_MENU_SHOW_ALL = "Все квесты";
    private final String INLINE_BUTTON_QUEST_MAIN_MENU_CREATE = "Создать квест";
    private final String INLINE_BUTTON_QUEST_MAIN_MENU_RUNNING = "Запущенные квесты";
    private final String INLINE_BUTTON_QUEST_MAIN_MENU_RUN_QUEST = "Запустить квест";

    private final String INLINE_BUTTON_CREATE_QUEST_POLL_TYPE = "Викторину (квиз)";
    private final String INLINE_BUTTON_CREATE_QUEST_LINEAR_TYPE = "Линейный";

    private final String INLINE_BUTTON_RUN_QUEST_CONFIRM = "Да, давайте сделаем это!";
    private final String INLINE_BUTTON_PLAY_QUEST_CONFIRM = "Начать испытание";
    private final String INLINE_BUTTON_QUIT_QUEST_CONFIRM = "Да, я хочу выйти";
    private final String INLINE_BUTTON_STOP_QUEST_CONFIRM = "Остановить квест '";

    private final String INLINE_BUTTON_CANCEL_ACTION = "Отмена";


    public ResponseKeyboardMarkupDTO getUserReplyKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.REPLY_ADD);
        markup.addReplyButton(REPLY_BUTTON_ACCOUNT);
        markup.addReplyButton(REPLY_BUTTON_QUESTS);
        markup.addReplyButton(REPLY_BUTTON_HELP);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public ResponseKeyboardMarkupDTO getUserQuestCreationReplyKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.REPLY_ADD);
        markup.addReplyButton(REPLY_BUTTON_QUEST_CREATION_STOP);
        markup.addReplyButton(REPLY_BUTTON_HELP);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public ResponseKeyboardMarkupDTO getPlayerReplyKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.REPLY_ADD);
        markup.addReplyButton(REPLY_BUTTON_LEAVE_QUEST);
        return markup;
    }

    public ResponseKeyboardMarkupDTO getAdminReplyKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.REPLY_ADD);
        markup.addReplyButton(REPLY_BUTTON_LEAVE_QUEST);
        markup.addReplyButton(REPLY_BUTTON_STOP_RUNNING_QUEST);
        markup.addReplyButton(REPLY_BUTTON_PLAYERS_MANAGEMENT);
        markup.addReplyButton(REPLY_BUTTON_QUEST_MANAGEMENT);
        return markup;
    }

    public ResponseKeyboardMarkupDTO getConfirmCustomNameInlineKeyboard(String customName, TGUserDTO from) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + customName, "registration_apply_name");
        markup.addInlineButton(INLINE_BUTTON_REGISTRATION_ENTER_ANOTHER_NAME, "registration_custom_name");
        if (from.getFirstName() != null) {
            markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + from.getFirstName(), "registration_name_first");
        }
        if (from.getUserName() != null) {
            markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + from.getUserName(), "registration_name_username");
        }
        if (from.getLastName() != null) {
            markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + from.getLastName(), "registration_name_last");
        }
        return markup;
    }

    public ResponseKeyboardMarkupDTO getRegistrationOfferSelectNameInlineKeyboard(TGUserDTO from) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        if (from.getFirstName() != null) {
            markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + from.getFirstName(), "registration_name_first");
        }
        if (from.getUserName() != null) {
            markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + from.getUserName(), "registration_name_username");
        }
        if (from.getLastName() != null) {
            markup.addInlineButton(INLINE_BUTTON_REGISTRATION_USE_NAME + from.getLastName(), "registration_name_last");
        }
        markup.addInlineButton(INLINE_BUTTON_REGISTRATION_ENTER_ANOTHER_NAME, "registration_custom_name");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getConfirmQuestCreationStopInlineKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_STOP_CREATION_YES, "stop_quest_creating");
        markup.addInlineButton(INLINE_BUTTON_STOP_CREATION_CANCEL, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getConfirmQuestNameInlineKeyboard(String name) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_CONFIRM_QUEST_NAME_YES, "q_name:" + name);
        markup.addInlineButton(INLINE_BUTTON_CONFIRM_QUEST_NAME_CANCEL, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO replyKeyboardRemoveAction() {
        return ResponseKeyboardMarkupDTO.builder()
                .keyboardType(KeyboardType.REPLY_REMOVE)
                .removeKeyboard(true)
                .build();
    }

    public ResponseKeyboardMarkupDTO getUserQuestListInlineKeyboard(List<QuestInfo> questInfoList) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);

        for (QuestInfo questInfo : questInfoList) {
            markup.addInlineButton(questInfo.getQuestName(), "quest_Id:" + questInfo.getId());
        }
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getQuestOptionsInlineKeyboard(long questId) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_QUEST_OPTION_RUN, "run_quest:" + questId);
        markup.addInlineButton(INLINE_BUTTON_QUEST_OPTION_EDIT, "edit_quest:" + questId);
        markup.addInlineButton(INLINE_BUTTON_QUST_OPTION_DELETE, "remove_quest:" + questId);
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getRunningQuestOptionsInlineKeyboard(long questId) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_RUNNING_QUEST_STOP_ACTION, "stop_quest_id:" + questId);
        markup.addInlineButton(INLINE_BUTTON_RUNNING_QUEST_PLAY_ACTION, "play_quest_id:" + questId);
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getQuestMainMenuInlineKeyboard(Long runningQuestCount) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_QUEST_MAIN_MENU_SHOW_ALL, "quest_list");
        markup.addInlineButton(INLINE_BUTTON_QUEST_MAIN_MENU_CREATE, "create_quest");
        if (runningQuestCount > 0) {
            markup.addInlineButton(INLINE_BUTTON_QUEST_MAIN_MENU_RUNNING, "running_quest_list");
        }
        markup.addInlineButton(INLINE_BUTTON_QUEST_MAIN_MENU_RUN_QUEST, "run_my_quest");
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getCreateQuestTypeMenuInlineKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_CREATE_QUEST_POLL_TYPE, "create_quiz");
        //markup.addInlineButton(INLINE_BUTTON_CREATE_QUEST_LINEAR_TYPE, "create_linear");
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getSelectQuestToRunMenuInlineKeyboard(List<QuestInfo> questInfoList) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);

        for (QuestInfo questInfo : questInfoList) {
            markup.addInlineButton("▶\uFE0F " + questInfo.getQuestName(), "run_quest:" + questInfo.getId());
        }
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }


    public ResponseKeyboardMarkupDTO getConfirmRunQuestInlineKeyboard(long questId) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_RUN_QUEST_CONFIRM, "run_selected_quest:" + questId);
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getFirstQuestNodeInlineKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_PLAY_QUEST_CONFIRM, "get_first_quest_node");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getQuitQuestConfirmationInlineKeyboard() {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_QUIT_QUEST_CONFIRM, "quit_quest_confirm");
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getStopRunningQuestConfirmationInlineKeyboard(QuestInfo questInfo) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);
        markup.addInlineButton(INLINE_BUTTON_STOP_QUEST_CONFIRM + questInfo.getQuestName() + "'", "stop_quest_id:" + questInfo.getId());
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }

    public ResponseKeyboardMarkupDTO getRunningQuestInlineKeyboard(List<QuestInfo> runningQuestList) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);

        for (QuestInfo questInfo : runningQuestList) {
            markup.addInlineButton("⏹\uFE0F " + questInfo.getQuestName(), "stop_quest_id:" + questInfo.getId());
        }
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;
    }


    public ResponseKeyboardMarkupDTO getFriendListInlineKeyboard(List<UserInfo> friendList) {
        ResponseKeyboardMarkupDTO markup = new ResponseKeyboardMarkupDTO(KeyboardType.INLINE);

        for (UserInfo userInfo : friendList) {
            markup.addInlineButton("a","b");
        }
        markup.addInlineButton(INLINE_BUTTON_CANCEL_ACTION, "cancel");
        return markup;

//        friendList.forEach(friend -> {
//            ResponseKeyboardRowDTO keyboardRow = new ResponseKeyboardRowDTO();
//            keyboardRow.addInlineButton(friend.getUserName(), "-unused-" + friend.getUserId());
//            keyboardRow.addInlineButton("Удалить", "friend_action_remove:" + friend.getUserId());
//            keyboardRow.addInlineButton("Деактивировать", "friend_action_disable:" + friend.getUserId());
//            replyMarkup.addRow(keyboardRow);
//        });

    }

}
