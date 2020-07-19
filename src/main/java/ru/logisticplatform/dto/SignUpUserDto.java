package ru.logisticplatform.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.logisticplatform.model.user.Status;

import java.util.List;

/**
 * DTO class for user requests by ROLE_USER
 *
 * @author Sergei Perminov
 * @version 1.0
 */

//@FieldDefaults(level = AccessLevel.PRIVATE)
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
    Status status;
    List<RoleDto> roles;
    List<UserTypeDto> userTypes;
}