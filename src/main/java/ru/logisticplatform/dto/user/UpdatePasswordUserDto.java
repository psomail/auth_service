package ru.logisticplatform.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePasswordUserDto {
    String username;
    String password;
    String passwordNew;
    String passwordConfirm;
}