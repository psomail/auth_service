package ru.logisticplatform.service.user;

import ru.logisticplatform.dto.user.UpdatePasswordUserDto;
import ru.logisticplatform.dto.user.UpdateUserDto;
import ru.logisticplatform.model.user.Role;
import ru.logisticplatform.model.user.UserStatus;
import ru.logisticplatform.model.user.User;

import java.util.List;

/**
 * Service interface for class {@link User}.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

public interface UserService{

    User signUp(User user);

    Boolean activateUser(String code);

    User updateUser(UpdateUserDto updateUserDto);
    User updateUserStatus(User user, UserStatus userStatus);
    User updatePasswordUser(UpdatePasswordUserDto updatePasswordUserDto);

    List<User> getAll();

    User findByUsername(String username);

    User findById(Long id);



    void delete(Long id);
    void delete(String userName);

    List<User> findByStatus(UserStatus userStatus);
}

