package ru.logisticplatform.rest.user.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.logisticplatform.dto.RestMessageDto;
import ru.logisticplatform.dto.utils.ObjectMapperUtils;
import ru.logisticplatform.dto.user.UserDto;
import ru.logisticplatform.model.RestMessage;
import ru.logisticplatform.model.user.UserStatus;
import ru.logisticplatform.model.user.User;
import ru.logisticplatform.service.RestMessageService;
import ru.logisticplatform.service.user.RoleService;
import ru.logisticplatform.service.user.UserService;


/**
 * REST controller for {@link User} connected requests.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

@RestController
@RequestMapping("/api/v1/customers/")
public class CustomerRestControllerV1 {

    private final UserService userService;
    private final RoleService roleService;
    private final RestMessageService restMessageService;

    @Autowired
    public CustomerRestControllerV1(UserService userService
                                        , RoleService roleService
                                        , RestMessageService restMessageService) {
        this.userService = userService;
        this.roleService = roleService;
        this.restMessageService = restMessageService;
    }

    /**
     *
     * @param authentication
     * @return
     */

    @GetMapping(value = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMe(Authentication authentication/*, Principal principal*/) {
        //System.out.println(principal.getName());

        User user = this.userService.findByUsername(authentication.getName());

        if (user == null || user.getUserStatus() == UserStatus.DELETED){

            RestMessage restMessage = this.restMessageService.findById(2L);

            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        UserDto userDto = ObjectMapperUtils.map(user, UserDto.class);

        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

    /**
     *
     * @param userName
     * @return
     */

    @GetMapping(value = "/contractors/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getContractor(@PathVariable("name") String userName){

        if(userName == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = this.userService.findByUsername(userName);

        if (user == null || user.getUserStatus() != UserStatus.ACTIVE
                || this.roleService.findUserRole(user, "ROLE_CUSTOMER")
                || this.roleService.findUserRole(user, "ROLE_ADMIN")){

            RestMessage restMessage = this.restMessageService.findByCode("U003");

            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        UserDto userDto = ObjectMapperUtils.map(user, UserDto.class);

        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

    /**
     *
     * @param authentication
     * @return
     */

    @DeleteMapping(value = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMe(Authentication authentication/*, Principal principal*/) {

        //System.out.println(principal.getName());

        User user = this.userService.findByUsername(authentication.getName());

        if (user == null || user.getUserStatus() == UserStatus.DELETED) {

            RestMessage restMessage = this.restMessageService.findByCode("U002");

            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        this.userService.updateUserStatus(user, UserStatus.DELETED);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
