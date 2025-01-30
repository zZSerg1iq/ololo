package ru.zinoviev.questbot.message_handlerservice.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.service.impl.UserRepositoryServiceImpl;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.RegistrationStatus;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.response.scenario.RoleScenario;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestUpdateHandlerService {

    private final RegistrationService registrationService;
    private final UserRepositoryServiceImpl userRepositoryServiceImpl;
    private final Map<String, RoleScenario> roleScenarioServiceMap;

    public ResponseUpdateDTO getResponse(TGUpdateDTO update) {
        RegistrationStatus status = userRepositoryServiceImpl.getUserStatus(update);

        //Регистрация пользователя, если она не завершена
        if (status != RegistrationStatus.REGISTRATION_COMPLETE) {
            return registrationService.getNextRegistrationStep(update, status);
        }

        Scenario scenario = update.getFrom().getScenario();

        if (update.getCallbackQuery() != null) {
            return switch (scenario) {
                case USER -> roleScenarioServiceMap.get("userCallback").proceedScenario(update);
                case PLAYER -> roleScenarioServiceMap.get("playerCallback").proceedScenario(update);
                case ADMIN -> roleScenarioServiceMap.get("adminCallback").proceedScenario(update);
                case CREATING -> roleScenarioServiceMap.get("questCreationCallback").proceedScenario(update);
            };
        }

        return switch (scenario) {
            case USER -> roleScenarioServiceMap.get("userMessage").proceedScenario(update);
            case PLAYER -> roleScenarioServiceMap.get("playerMessage").proceedScenario(update);
            case ADMIN -> roleScenarioServiceMap.get("adminMessage").proceedScenario(update);
            case CREATING -> roleScenarioServiceMap.get("questCreationMessage").proceedScenario(update);
        };

    }

}
