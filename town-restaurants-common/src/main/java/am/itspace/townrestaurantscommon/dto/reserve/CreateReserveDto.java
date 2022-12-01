package am.itspace.townrestaurantscommon.dto.reserve;

import am.itspace.townrestaurantscommon.entity.Restaurant;
import am.itspace.townrestaurantscommon.entity.User;
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

    private LocalDateTime reservedAt;

    @NotBlank(message = "Date is mandatory")
    private LocalDate reservedFor;

    private Restaurant restaurant;

    @NotBlank(message = "User is mandatory")
    private User user;

    @NotBlank(message = "Host count is mandatory")
    private int hostCount;
}
