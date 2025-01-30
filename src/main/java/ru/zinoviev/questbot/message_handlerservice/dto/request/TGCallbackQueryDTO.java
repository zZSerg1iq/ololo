package ru.zinoviev.questbot.message_handlerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGUserDTO;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGCallbackQueryDTO {

    private String id;
    private String data;
    //private TGUserDTO from;
    //private TGMessageDTO message;
}
