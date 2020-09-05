package ru.logisticplatform.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.logisticplatform.model.user.UserStatus;

import java.util.List;

/**
 * DTO class for user requests by ROLE_USER
 *
 * @author Sergei Perminov
 * @version 1.0
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpUserDto {
    Long id;
    String username;
    String firstName;
    String lastName;
    String email;
    String phone;
    String password;
    List<RoleDto> roles;
}