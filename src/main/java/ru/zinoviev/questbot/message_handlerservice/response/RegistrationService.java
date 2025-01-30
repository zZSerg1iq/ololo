package ru.zinoviev.questbot.message_handlerservice.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.constants.ResponseMessages;
import ru.zinoviev.questbot.message_handlerservice.domain.service.UserRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.RegistrationStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepositoryService userRepositoryService;
    private final ResponseMessageService messageService;
    private final ReplyKeyboardService replyKeyboardService;

    public ResponseUpdateDTO getNextRegistrationStep(TGUpdateDTO update, RegistrationStatus status) {
        ResponseUpdateDTO result;

        if (update.getCallbackQuery() != null) {
            result = switch (update.getCallbackQuery().getData()) {
                case "registration_name_first" -> acceptDefaultName(update, update.getFrom().getFirstName());
                case "registration_name_last" -> acceptDefaultName(update, update.getFrom().getLastName());
                case "registration_name_username" -> acceptDefaultName(update, update.getFrom().getUserName());
                case "registration_custom_name" -> selectCustomName(update);
                case "registration_apply_name" -> acceptCustomName(update);
                default -> onError(update.getMessage());
            };

        } else {
            result = switch (status) {
                case NOT_REGISTERED -> performRegistration(update);
                case REGISTRATION_CONFIRM_NAME -> confirmCustomName(update);
                default -> throw new IllegalStateException("Unexpected value: " + status);
            };
        }

        return result;
    }

    private ResponseUpdateDTO performRegistration(TGUpdateDTO updateDTO) {

        ResponseMessageDTO messageDTO = messageService.getMessage(updateDTO.getMessage());
        messageDTO.setReplyMarkup(replyKeyboardService.getRegistrationOfferSelectNameInlineKeyboard(updateDTO.getFrom()));
        messageDTO.setText(ResponseMessages.REGISTRATION_HALLO_MESSAGE);

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(messageDTO))
                .build();
    }


    private ResponseUpdateDTO selectCustomName(TGUpdateDTO updateDTO) {
        userRepositoryService.updateUserRegistrationStatus(updateDTO.getFrom(),
                RegistrationStatus.REGISTRATION_CONFIRM_NAME);

        ResponseMessageDTO messageDTO = messageService.getEditMessage(updateDTO.getMessage());
        messageDTO.setText(ResponseMessages.REGISTRATION_HALLO_MESSAGE);

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(messageDTO))
                .build();
    }


    private ResponseUpdateDTO confirmCustomName(TGUpdateDTO updateDTO) {
        if (updateDTO.getMessage().getText() == null) {
            return onError(updateDTO.getMessage());
        }

        ResponseMessageDTO messageDTO = messageService.getMessage(updateDTO.getMessage());
        messageDTO.setReplyMarkup(replyKeyboardService.getConfirmCustomNameInlineKeyboard(
                updateDTO.getMessage().getText(), updateDTO.getFrom()));
        messageDTO.setText(ResponseMessages.REGISTRATION_ENTER_NAME_CAPTION + updateDTO.getMessage().getText());

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(messageDTO))
                .build();

    }

    private ResponseUpdateDTO acceptDefaultName(TGUpdateDTO updateDTO, String name) {
        updateDTO.getFrom().setNickName(name);
        return completeTheRegistration(updateDTO);
    }

    private ResponseUpdateDTO acceptCustomName(TGUpdateDTO updateDTO) {
        String customName = updateDTO.getMessage().getText().substring(ResponseMessages.REGISTRATION_ENTER_NAME_CAPTION.length());

        updateDTO.getFrom().setNickName(customName);
        return completeTheRegistration(updateDTO);
    }

    private ResponseUpdateDTO completeTheRegistration(TGUpdateDTO updateDTO) {
        userRepositoryService.updateUserRegistrationStatus(updateDTO.getFrom(),
                RegistrationStatus.REGISTRATION_COMPLETE);

        //сообщение о завершении регистрации
        ResponseMessageDTO regCompleteMessage = messageService.getEditMessage(updateDTO.getMessage());
        regCompleteMessage.setText(ResponseMessages.REGISTRATION_COMPLETE_MESSAGE1 + updateDTO.getFrom().getNickName());

        //сообщение с клавиатурой управления
        ResponseMessageDTO addUserKeyboardMessage = messageService.getMessage(updateDTO.getMessage());
        addUserKeyboardMessage.setReplyMarkup(replyKeyboardService.getUserReplyKeyboard());
        //addUserKeyboardMessage.setResponseUserDTO(ResponseUserDTO.builder().role(Scenario.USER).build());
        addUserKeyboardMessage.setText(ResponseMessages.REGISTRATION_COMPLETE_MESSAGE2);

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(regCompleteMessage, addUserKeyboardMessage))
                .build();
    }

    public ResponseUpdateDTO onError(TGMessageDTO message) {
        ResponseMessageDTO messageDTO = messageService.getMessage(message);
        messageDTO.setText(ResponseMessages.REGISTRATION_ON_ERROR);

        return ResponseUpdateDTO
                .builder()
                .messageList(List.of(messageDTO))
                .build();
    }

}
