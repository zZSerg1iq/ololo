package ru.zinoviev.questbot.message_handlerservice.mapper;

import org.springframework.stereotype.Component;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.UserFriend;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUserDTO;

import java.util.List;

@Component
public class PersonMapper {

    public BotUser toEntity(TGUserDTO telegramUser) {
        return BotUser.builder()
                .telegram(telegramUser.getUserId())
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .userName(telegramUser.getUserName())
                .languageCode(telegramUser.getLanguageCode())
                .nickName(telegramUser.getNickName())
                .build();
    }

    public UserInfo entityToUserInfo(BotUser user) {
        return UserInfo.builder()
                .id(user.getId())
                .friendCount(user.getFriendList().size())
                .scenario(user.getScenario())
                .questCount(user.getQuestList().size())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .points(user.getPoints())
                .build();
    }

    public UserInfo friendToUserInfo(UserFriend friend) {
        return UserInfo.builder()
                .id(friend.getId())
                .firstName(friend.getFirstName())
                .lastName(friend.getLastName())
                .userName(friend.getUserName())
                .build();
    }

    public BotUser setUserNamesInfo(BotUser botUser, TGUserDTO telegramUser) {
        return botUser.toBuilder()
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .userName(telegramUser.getUserName())
                .nickName(telegramUser.getNickName())
                .languageCode(telegramUser.getLanguageCode())
                .build();
    }

    public List<ResponseUserDTO> toDtoList(List<UserFriend> friendList) {
        return friendList.stream().map(e -> ResponseUserDTO.builder()
               .userId(e.getId())
               .firstName(e.getFirstName())
               .lastName(e.getLastName())
               .userName(e.getUserName())
               .build()).toList();
    }

    public BotUser updateEntity(BotUser botUser, TGUserDTO telegramUser) {
        return botUser.toBuilder()
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .userName(telegramUser.getUserName())
                .languageCode(telegramUser.getLanguageCode())
                .nickName(telegramUser.getNickName())
                .build();
    }

    public TGUserDTO updateDto(TGUserDTO telegramUser, BotUser botUser) {
        return telegramUser.toBuilder()
                .firstName(botUser.getFirstName())
                .lastName(botUser.getLastName())
                .userName(botUser.getUserName())
                .languageCode(botUser.getLanguageCode())
                .nickName(botUser.getNickName())
                .build();
    }
}
