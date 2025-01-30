package ru.zinoviev.questbot.message_handlerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String languageCode;

    private String nickName;
    private Scenario role;
}
