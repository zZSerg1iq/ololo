package ru.zinoviev.questbot.message_handlerservice.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.*;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.UserRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.service.UserRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.RegistrationStatus;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.mapper.PersonMapper;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private final UserRepository userRepository;
    private final PersonMapper personMapper;

    @Override
    @Transactional
    public RegistrationStatus getUserStatus(TGUpdateDTO update) {

        TGUserDTO userDTO = update.getFrom();

        Optional<BotUser> optionalUser = userRepository.findByTelegram(userDTO.getUserId());
        BotUser entityBotUser;

        if (optionalUser.isEmpty()) {
            entityBotUser = personMapper.toEntity(userDTO)
                    .toBuilder()
                    .registrationStatus(RegistrationStatus.NOT_REGISTERED)
                    .scenario(Scenario.USER)
                    .userAccountStatus(AccountStatus.ACTIVE)
                    .build();

            userRepository.save(entityBotUser);
            return RegistrationStatus.NOT_REGISTERED;
        }
        entityBotUser = optionalUser.get();
        userDTO.setScenario(entityBotUser.getScenario());
        return entityBotUser.getRegistrationStatus();
    }

    @Override
    @Transactional
    public void updateUserRegistrationStatus(TGUserDTO telegramUser, RegistrationStatus status) {
        BotUser dbBotUser = userRepository
                .findByTelegram(telegramUser.getUserId())
                .orElseThrow(() -> new IllegalStateException("user not found"));
        dbBotUser.setRegistrationStatus(status);
        dbBotUser = personMapper.setUserNamesInfo(dbBotUser, telegramUser);
        userRepository.save(dbBotUser);
    }

    public void addFriend(TGUserDTO userDTO, BotUser user){
        UserFriend friend = new UserFriend();
        friend.setUserName("");
        friend.setQuestBotUser(user);

        if (!user.getFriendList().contains(friend)) {
            user.getFriendList().add(friend);
        }
    }

    @Override
    @Transactional
    public List<UserInfo> getFriends(Long userId) {
        BotUser botUser = userRepository.findByTelegram(userId)
                .orElseThrow(() -> new IllegalStateException("user not found"));

        return botUser.getFriendList().stream()
                .map(personMapper::friendToUserInfo)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        BotUser botUser = userRepository.findByTelegram(userId)
                .orElseThrow(() -> new IllegalStateException("user not found"));
        userRepository.delete(botUser);
    }

    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        BotUser user = userRepository.findByTelegram(userId)
                .orElseThrow(() -> new IllegalStateException("user not found"));

        return  personMapper.entityToUserInfo(user);
    }
}
