package ru.zinoviev.questbot.message_handlerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGPollAnswerDTO {

    private String pollId;
    private List<Integer> optionIds;

}
