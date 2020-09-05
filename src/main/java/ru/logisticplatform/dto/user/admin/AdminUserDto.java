package ru.logisticplatform.dto.user.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.logisticplatform.dto.user.RoleDto;
import ru.logisticplatform.dto.user.UserDto;
import ru.logisticplatform.model.user.UserStatus;

import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserDto extends UserDto {
    Long id;
    List<RoleDto> roles;
    UserStatus userStatus;
}
