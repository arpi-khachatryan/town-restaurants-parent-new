package am.itspace.townrestaurantscommon.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String address;

    @Email
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Size(min = 8, max = 9, message = "Phone number must be at least 8 characters")
    private String phone;

    @NotBlank(message = "Category is mandatory")
    private Integer restaurantCategoryId;

    private Double deliveryPrice;

    private List<String> pictures;
}