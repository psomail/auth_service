package ru.logisticplatform.rest.user.admin;


import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.logisticplatform.dto.RestMessageDto;
import ru.logisticplatform.dto.user.SignUpUserDto;
import ru.logisticplatform.dto.user.UpdatePasswordUserDto;
import ru.logisticplatform.dto.user.UpdateUserDto;
import ru.logisticplatform.dto.user.UserDto;
import ru.logisticplatform.dto.user.admin.AdminUserDto;
import ru.logisticplatform.dto.utils.ObjectMapperUtils;
import ru.logisticplatform.model.RestMessage;
import ru.logisticplatform.model.user.User;
import ru.logisticplatform.model.user.UserStatus;
import ru.logisticplatform.service.RestMessageService;
import ru.logisticplatform.service.user.UserService;

import java.util.List;

/**
 * REST controller for User {@link User} connected requests.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {

    private final UserService userService;
    private final RestMessageService restMessageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserRestControllerV1(UserService userService
                                ,RestMessageService restMessageService
                                ,BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userService = userService;
        this.restMessageService= restMessageService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     *
     * @param authentication
     * @param updateUserDto
     * @return
     */
    @PutMapping(value = "/update/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(Authentication authentication, @RequestBody UpdateUserDto updateUserDto) {
        HttpHeaders headers = new HttpHeaders();

        if (updateUserDto == null) {
            RestMessage restMessage = this.restMessageService.findByCode("S001");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.BAD_REQUEST);
        }

        User user = this.userService.findByUsername(updateUserDto.getUsername());

        User authUser = this.userService.findByUsername(authentication.getName());

        if(user == null || user.getUserStatus() == UserStatus.NOT_ACTIVE || user.getUserStatus() == UserStatus.DELETED){
            RestMessage restMessage = this.restMessageService.findByCode("U002");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        if(!user.getUsername().equals(authUser.getUsername())){
            RestMessage restMessage = this.restMessageService.findByCode("S001");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.BAD_REQUEST);
        }

        User updatedUser = this.userService.updateUser(updateUserDto);

        UserDto updatedUserDto = ObjectMapperUtils.map(updatedUser, UserDto.class);

        return new ResponseEntity<UserDto>(updatedUserDto, HttpStatus.OK);
    }

    /**
     *
     * @param authentication
     * @param updatePasswordUserDto
     * @return
     */
    @PutMapping(value = "/update/password/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePasswordUser(Authentication authentication, @RequestBody UpdatePasswordUserDto updatePasswordUserDto) {
        HttpHeaders headers = new HttpHeaders();

        if (updatePasswordUserDto == null) {
            RestMessage restMessage = this.restMessageService.findByCode("S001");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.BAD_REQUEST);
        }

        User user = this.userService.findByUsername(updatePasswordUserDto.getUsername());
        User authUser = this.userService.findByUsername(authentication.getName());

        if (user == null || user.getUserStatus() == UserStatus.NOT_ACTIVE || user.getUserStatus() == UserStatus.DELETED) {
            RestMessage restMessage = this.restMessageService.findByCode("U002");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        if (!user.getUsername().equals(authUser.getUsername())) {
            RestMessage restMessage = this.restMessageService.findByCode("S001");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.BAD_REQUEST);
        }

        if (!bCryptPasswordEncoder.matches(updatePasswordUserDto.getPassword(), user.getPassword())){
            RestMessage restMessage = this.restMessageService.findByCode("U008");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.BAD_REQUEST);
        }

        if(!updatePasswordUserDto.getPasswordNew().equals(updatePasswordUserDto.getPasswordConfirm())){
            RestMessage restMessage = this.restMessageService.findByCode("U009");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.BAD_REQUEST);
        }

        User updatedUser =  this.userService.updatePasswordUser(updatePasswordUserDto);

        UserDto updatedUserDto = ObjectMapperUtils.map(updatedUser, UserDto.class);

        RestMessage restMessage = this.restMessageService.findByCode("U010");
        RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

        return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.OK);
    }

    /**
     *
     * @param authentication
     * @return
     */
    @GetMapping(value = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMe(Authentication authentication/*, Principal principal*/) {

        User user = this.userService.findByUsername(authentication.getName());

        if (user == null || user.getUserStatus() == UserStatus.NOT_ACTIVE || user.getUserStatus() == UserStatus.DELETED){

            RestMessage restMessage = this.restMessageService.findByCode("U002");

            RestMessageDto restMessageDto= ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        UserDto userDto = ObjectMapperUtils.map(user, UserDto.class);

        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

}
