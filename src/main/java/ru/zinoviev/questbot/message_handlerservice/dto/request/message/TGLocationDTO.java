package ru.zinoviev.questbot.message_handlerservice.dto.request.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGLocationDTO {
    private Double longitude;
    private Double latitude;
    private Integer livePeriod;

}
