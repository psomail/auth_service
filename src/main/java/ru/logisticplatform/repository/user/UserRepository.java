package ru.logisticplatform.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.logisticplatform.model.user.UserStatus;
import ru.logisticplatform.model.user.User;

import java.util.Date;
import java.util.List;

/**
 * Repository interface that extends {@link JpaRepository} for class {@link User}.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String name);

    User findByActivationCode(String activationCode);

    List<User> findByUserStatus(UserStatus userStatus);

    void deleteByUsername(String username);
    void deleteByUserStatus(UserStatus userStatus);
    void deleteAllByUserStatusAndCreatedBefore(UserStatus userStatus, Date createdBefore);
}
