package ru.zinoviev.questbot.message_handlerservice.dto.request.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGVoiceDTO {
    private String fileId;
    private String fileUniqueId;
    private Integer duration;
    private Long fileSize;
    private String mimeType;
}
