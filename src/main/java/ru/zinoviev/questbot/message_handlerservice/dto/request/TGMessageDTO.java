package ru.zinoviev.questbot.message_handlerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGMessageDTO {

    private Integer messageId;
    private String messageType;
    private Long chatId;
    private Integer date;
    private String text;
    private String mediaGroupId;

    //private TGUserDTO from;
    private TGPollDTO poll;
    private TGContactDTO contact;
    private TGAnimationDTO animation;
    private TGDocumentDTO document;
    private TGLocationDTO location;
    private TGVideoDTO video;
    private TGVoiceDTO voice;
    private TGStickerDTO sticker;
    private List<TGPhotoSizeDTO> photo;
}
