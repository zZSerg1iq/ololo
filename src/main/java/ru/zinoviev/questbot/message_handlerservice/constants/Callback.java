package ru.zinoviev.questbot.message_handlerservice.constants;

public class Callback {

    //Account menu
    public final String DROP_NICKNAME = "drop_nickname";
    public final String FRIEND_LIST = "friend_list";
    public final String USER_INFO = "user_info";
    public final String DELETE_ACCOUNT = "delete_account";

    //Quest menu
    public final String USER_QUEST_LIST = "quest_list";
    public final String RUNNING_QUEST_LIST = "running_quest_list";
    public final String RUN_QUEST_MENU = "run_my_quest";
    public final String CREATE_QUEST_MENU = "create_quest";
    public final String CREATE_QUIZ_QUEST = "create_quiz";
    public final String CREATE_LINEAR_QUEST = "create_linear";

    //user questList: select quest action
    public final String SELECTED_QUEST_MENU = "quest_Id";

    //selected quest menu
    public final String RUN_QUEST_CONFIRMATION = "run_quest";
    public final String STOP_SELECTED_QUEST = "stop_quest_id";
    public final String EDIT_SELECTED_QUEST = "edit_quest";
    public final String REMOVE_SELECTED_QUEST = "remove_quest";
    public final String PLAY_SELECTED_QUEST = "play_quest_id";

    public final String SELECTED_QUEST = "run_selected_quest";

    //friends menu
    public final String REMOVE_FRIEND = "friend_action_remove";
    public final String DISABLE_FRIEND = "friend_action_disable";

    public final String CANCEL = "cancel";
    

    public final String STOP_CREATING = "stop_quest_creating";
    public final String SAVE_QUEST_NAME = "q_name";
    public final String GET_FIRST_NODE = "get_first_quest_node";
    public final String LEAVE_QUEST_CONFORMATION = "quit_quest_confirm";
    public final String STOP_RUNNING_QUEST = "stop_quest_id";


}
