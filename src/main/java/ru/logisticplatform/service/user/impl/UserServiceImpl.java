package ru.logisticplatform.service.user.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.logisticplatform.dto.user.UpdatePasswordUserDto;
import ru.logisticplatform.dto.user.UpdateUserDto;
import ru.logisticplatform.dto.utils.ObjectMapperUtils;
import ru.logisticplatform.model.user.User;
import ru.logisticplatform.model.user.UserStatus;
import ru.logisticplatform.mq.ProducerRabbitMq;
import ru.logisticplatform.mq.UserMq;
import ru.logisticplatform.repository.user.UserRepository;
import ru.logisticplatform.service.RestMessageService;
import ru.logisticplatform.service.user.MailSender;
import ru.logisticplatform.service.user.UserService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link UserService} interface.
 * Wrapper for {@link UserRepository} + business logic.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailSender mailSender;
    private final RestMessageService restMessageService;
    private final ProducerRabbitMq producerRabbitMq;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Value("${hostname}")
    private String hostname;

    @Autowired
    public UserServiceImpl(UserRepository userRepository
                            ,BCryptPasswordEncoder passwordEncoder
                            ,MailSender mailSender
                            ,RestMessageService restMessageService
                            ,ProducerRabbitMq producerRabbitMq
                            ,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.restMessageService = restMessageService;
        this.producerRabbitMq = producerRabbitMq;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     *
     * @param user
     * @return
     */

    @Override
    public User signUp(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setUserStatus(UserStatus.NOT_ACTIVE);

        if(!StringUtils.isEmpty(user.getEmail())){

            String message = String.format(restMessageService.findByCode("M002").getDescription()
                                                ,user.getUsername()
                                                ,hostname
                                                ,user.getActivationCode());

            mailSender.send(user.getEmail()
                                        ,restMessageService.findByCode("M001").getDescription()
                                        ,message);

            log.info("IN UserServiceImpl signUp() - an email with an activation code has been sent to the user: {}"
                    ,user.getUsername());
        }

        User signUpUser = userRepository.save(user);
        log.info("IN UserServiceImpl signUp() - user: {} successfully registered", signUpUser.getUsername());

        return signUpUser;
    }

    @Override
    public Boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if(user == null)  {
            return false;
        }

        user.setActivationCode(null);
        user.setUserStatus(UserStatus.ACTIVE);

        User userActivated = userRepository.save(user);
        log.info("IN UserServiceImpl activateUser() - user: {} successfully activated", user.getUsername());

        if(!StringUtils.isEmpty(user.getEmail())){

            String message = String.format(restMessageService.findByCode("M004").getDescription());
            mailSender.send(user.getEmail()
                    ,restMessageService.findByCode("M003").getDescription()
                    ,message);

            log.info("IN UserServiceImpl activateUser() - \n" +
                            "an email has been sent confirming user activation user: {}"
                    ,user.getUsername());
        }

        UserMq userMq = ObjectMapperUtils.map(userActivated, UserMq.class);
        producerRabbitMq.createUserProduce(userMq);

        log.info("IN UserServiceImpl activateUser() - object user: {} sent to RabbitMq", user.getUsername());

        return true;
    }


    @Override
    public User updateUser(UpdateUserDto updateUserDto) {

        User updateUser = this.userRepository.findByUsername(updateUserDto.getUsername());

        updateUser.setFirstName(updateUserDto.getFirstName());
        updateUser.setLastName(updateUserDto.getLastName());
        updateUser.setEmail(updateUserDto.getEmail());
        updateUser.setPhone(updateUserDto.getPhone());

        User updatedUser = userRepository.save(updateUser);

        log.info("IN UserServiceImpl updateUser() - user: {} successfully updated", updatedUser.getUsername());

        UserMq userMq = ObjectMapperUtils.map(updatedUser, UserMq.class);
        producerRabbitMq.updateUserProduce(userMq);
        log.info("IN UserServiceImpl updateUser() - object user: {} sent to RabbitMq to update", userMq.getUsername());


        return updatedUser;
    }

    @Override
    public User updateUserStatus(User user, UserStatus userStatus) {

        user.setUserStatus(userStatus);

        User updatedUser = userRepository.save(user);

        log.info("IN UserServiceImpl updateUserStatus() - user: {} successfully set userStatus: {}", updatedUser, userStatus);

        return updatedUser;
    }

    @Override
    public User updatePasswordUser(UpdatePasswordUserDto updatePasswordUserDto) {

        User updateUser = this.userRepository.findByUsername(updatePasswordUserDto.getUsername());

        updateUser.setPassword(bCryptPasswordEncoder.encode(updatePasswordUserDto.getPasswordNew()));

        User updatedUser = userRepository.save(updateUser);

        log.info("IN UserServiceImpl updatePasswordUser() - password for user: {} successfully updated", updatedUser.getUsername());

        return updatedUser;
    }

    @Override
    public List<User> getAll() {

        List<User> users = userRepository.findAll();

        log.info("IN UserServiceImpl getAll");
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        log.info("IN UserServiceImpl findByUsername() - user: {} found by userName: {}", user, username);
        return user;
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null){
            log.warn("IN UserServiceImpl findById() - no user found by id: {}", id);
            return null;
        }

        log.info("IN UserServiceImpl findById() - user: {} found by id: {}", user.getUsername(), id);
        return user;
    }

    @Override
    public void delete(Long id) {

        User userDelete = userRepository.findById(id).orElse(null);

        UserMq userMq = ObjectMapperUtils.map(userDelete, UserMq.class);
        producerRabbitMq.deleteUserProduce(userMq);
        log.info("IN UserServiceImpl delete() - object user: {} sent to RabbitMq to delete", userMq.getUsername());

        userRepository.deleteById(id);
        log.info("IN UserServiceImpl delete {}", id);
    }

    @Override
    public void delete(String userName) {

        User userDelete = userRepository.findByUsername(userName);

        UserMq userMq = ObjectMapperUtils.map(userDelete, UserMq.class);
        producerRabbitMq.deleteUserProduce(userMq);
        log.info("IN UserServiceImpl delete() - object user: {} sent to RabbitMq to delete", userMq.getUsername());

        userRepository.delete(userDelete);
        log.info("IN UserServiceImpl delete {}", userName);
    }

    /**
     *
     */

    @Scheduled(initialDelay = 10000, fixedDelayString = "${schedule.delete.not.active.user}")
    @Transactional
    public void deleteSchedule(){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        Date previousDay = calendar.getTime();
        userRepository.deleteAllByUserStatusAndCreatedBefore(UserStatus.NOT_ACTIVE, previousDay);
        log.info("IN UserServiceImpl deleteSchedule() - deleted NOT_ACTIVE users");
    }

    @Override
    public List<User> findByStatus(UserStatus userStatus) {
        return userRepository.findByUserStatus(userStatus);
    }

}
