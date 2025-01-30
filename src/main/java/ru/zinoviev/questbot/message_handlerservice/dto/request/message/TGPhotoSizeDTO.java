package ru.zinoviev.questbot.message_handlerservice.dto.request.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGPhotoSizeDTO {
    private String fileId;
    private String fileUniqueId;
    private Integer width;
    private Integer height;
    private Integer fileSize;
    private String filePath;
}
