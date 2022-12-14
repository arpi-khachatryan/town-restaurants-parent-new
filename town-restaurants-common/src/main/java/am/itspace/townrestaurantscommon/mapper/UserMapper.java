package am.itspace.townrestaurantscommon.mapper;

import am.itspace.townrestaurantscommon.dto.user.CreateUserDto;
import am.itspace.townrestaurantscommon.dto.user.UserOverview;
import am.itspace.townrestaurantscommon.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapToEntity(CreateUserDto createUserDto);

    UserOverview mapToResponseDto(User user);

    List<UserOverview> mapToResponseDtoList(List<User> users);
}
