package ru.zinoviev.questbot.message_handlerservice.domain.service;

import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestType;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUserDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.RegistrationStatus;

import java.util.List;

public interface UserRepositoryService {

    RegistrationStatus getUserStatus(TGUpdateDTO tgUser);

    void updateUserRegistrationStatus(TGUserDTO tgUser, RegistrationStatus registrationComplete);

    List<UserInfo> getFriends(Long userId);

    void deleteUser(Long userId);

    UserInfo getUserInfoByUserId(Long userId);
}
