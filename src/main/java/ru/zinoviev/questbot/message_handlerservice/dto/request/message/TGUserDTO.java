package ru.zinoviev.questbot.message_handlerservice.dto.request.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGUserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String nickName;
    private String languageCode;

    //scenario fields
    @JsonIgnore
    private Scenario scenario;
}
