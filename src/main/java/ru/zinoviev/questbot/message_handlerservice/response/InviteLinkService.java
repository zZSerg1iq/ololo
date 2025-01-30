package ru.zinoviev.questbot.message_handlerservice.response;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class InviteLinkService {

    @Value("${inviteLink.salt}")
    private String inviteLinkSalt;
    private final Hashids hashids;
    private final int MIN_HASH_LEN = 10;

    public InviteLinkService() {
        this.hashids = new Hashids(inviteLinkSalt, MIN_HASH_LEN);
    }

    public String linkOf(long value) {
        return hashids.encode(value);
    }

    public List<Long> getDecodedString(String value) {
        String[] decodedInfo = value.split(":");
        if (decodedInfo.length < 2) {
            return null;
        }
        decodedInfo = decodedInfo[1].split("\\.");
        if (decodedInfo.length != 3) {
            return null;
        }
        List<Long> resultList = new ArrayList<>();

        for (int i = 0; i < decodedInfo.length; i++) {
            long[] res = hashids.decode(decodedInfo[i]);
            if (res != null && res.length > 0) {
                resultList.add(res[0]);
            }
        }
        return resultList;
    }

    public String getInviteLink(String questName, Long runningQuestId, long questOwnerId) {
        return linkOf(questName.chars().sum()) +
                "." +
                linkOf(Math.abs(runningQuestId)) +
                "." +
                linkOf(Math.abs(questOwnerId));
    }
}
