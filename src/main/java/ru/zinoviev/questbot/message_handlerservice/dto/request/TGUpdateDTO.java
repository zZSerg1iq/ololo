package ru.zinoviev.questbot.message_handlerservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGUpdateDTO {

    private TGUserDTO from;

    private TGMessageDTO message;
    private Integer updateId;
    private TGCallbackQueryDTO callbackQuery;
    private TGPollAnswerDTO pollAnswerDTO;

}
