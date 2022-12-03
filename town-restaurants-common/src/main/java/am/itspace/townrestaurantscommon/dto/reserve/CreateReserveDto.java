package am.itspace.townrestaurantscommon.dto.reserve;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReserveDto {

    private String reservedDate;
    private String reservedTime;
    private int peopleCount;
    private String phoneNumber;
    private Integer restaurantId;
}
