package ru.zinoviev.questbot.message_handlerservice.dto.request.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGAnimationDTO {
    private String fileId;
    private String fileUniqueId;
    private Integer width;
    private Integer height;
    private Integer duration;
    private Long fileSize;
    private String fileName;
    private String mimeType;

    private TGPhotoSizeDTO thumbnail;
}
