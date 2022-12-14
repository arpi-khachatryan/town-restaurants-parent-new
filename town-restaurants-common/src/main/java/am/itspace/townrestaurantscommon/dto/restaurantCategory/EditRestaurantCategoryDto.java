package am.itspace.townrestaurantscommon.dto.restaurantCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditRestaurantCategoryDto {

    @NotBlank(message = "Name is mandatory")
    @Schema(description = "The length of name should be between 2 and 15.",
            example = "Italian",
            minLength = 2,
            maxLength = 15,
            pattern = "^[A-Za-z]{2,15}$")
    private String name;
}
