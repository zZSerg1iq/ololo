package ru.zinoviev.questbot.message_handlerservice.dto;

import lombok.Builder;
import lombok.Data;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;

@Builder
@Data
public class UserInfo {
    private long id;
    private String firstName;
    private String userName;
    private String nickName;
    private String lastName;
    private int points;
    private int friendCount;
    private int questCount;
    private Scenario scenario;
}
