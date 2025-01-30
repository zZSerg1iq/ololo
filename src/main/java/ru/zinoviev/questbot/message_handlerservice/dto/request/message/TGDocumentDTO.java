package ru.zinoviev.questbot.message_handlerservice.dto.request.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGDocumentDTO {
    private String fileId;
    private String fileUniqueId;
    private String fileName;
    private String mimeType;
    private Long fileSize;
}
