package ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.service.RunningQuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.MessageType;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.player.PLayerMessageService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.player.PlayerCallbackService;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.impl.user.UserCallbackService;

@Component("adminCallback")
@RequiredArgsConstructor
public class AdminCallbackService implements RoleScenario {

    @Lazy
    private final PlayerCallbackService playerCallbackService;

    @Lazy
    private final UserCallbackService userCallbackService;

    private final RunningQuestRepositoryService runningQuestRepositoryService;
    private final ReplyKeyboardService replyKeyboardService;

    @Override
    public ResponseUpdateDTO proceedScenario(TGUpdateDTO update) {
        String callbackData = update.getCallbackQuery().getData();
        if (callbackData.contains(":")) {
            callbackData = callbackData.split(":")[0];
        }

        return switch (callbackData) {
            case "get_first_quest_node", "quit_quest_confirm" -> playerCallbackService.proceedScenario(update);

            case "stop_quest_id" -> userCallbackService.proceedScenario(update);

            default -> getDefaultMessage(update.getMessage(),
                    "AdminCallbackService: Мне незнакома такая команда", MessageType.EDIT_MESSAGE);
        };
    }


}