package ru.logisticplatform.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.logisticplatform.dto.RestMessageDto;
import ru.logisticplatform.dto.user.ActivatingUserDto;
import ru.logisticplatform.dto.user.AuthenticationRequestDto;
import ru.logisticplatform.dto.user.SignUpUserDto;
import ru.logisticplatform.dto.utils.ObjectMapperUtils;
import ru.logisticplatform.model.RestMessage;
import ru.logisticplatform.model.user.User;
import ru.logisticplatform.model.user.UserStatus;
import ru.logisticplatform.mq.ProducerRabbitMq;
import ru.logisticplatform.security.jwt.JwtTokenProvider;
import ru.logisticplatform.service.RestMessageService;
import ru.logisticplatform.service.user.RoleService;
import ru.logisticplatform.service.user.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for {@link User} registrated requests.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

@RestController
@RequestMapping("/api/v1/sign/")
public class SignRestControllerV1 {

    private final ProducerRabbitMq producerRabbitMq;

    private final UserService userService;
    private final RoleService roleService;
    private final RestMessageService restMessageService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SignRestControllerV1(UserService userService, RoleService roleService
                                , AuthenticationManager authenticationManager
                                , JwtTokenProvider jwtTokenProvider
                                , ProducerRabbitMq producerRabbitMq
                                , RestMessageService restMessageService) {

        this.userService = userService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.producerRabbitMq = producerRabbitMq;
        this.restMessageService = restMessageService;
    }


    /**
     *
     * @param userDto
     * @return
     */

    @PostMapping(value = "up", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUpUser(@RequestBody SignUpUserDto userDto){
        HttpHeaders headers = new HttpHeaders();

        if (userDto == null) {
            RestMessage restMessage = this.restMessageService.findByCode("S001");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }

        User user = this.userService.findByUsername(userDto.getUsername());

        if(user != null) {
            if (user.getUserStatus() == UserStatus.ACTIVE
                    || user.getUserStatus() == UserStatus.NOT_ACTIVE) {

                RestMessage restMessage = this.restMessageService.findByCode("U005");
                RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

                return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.FOUND);
            }

            if (user != null && user.getUserStatus() == UserStatus.DELETED) {

                /** TODO Later
                 *
                 */
            }
        }

        userDto.getRoles().forEach(role -> role.setId(roleService.findByRoleName(role.getName()).getId()));
        User userNew = ObjectMapperUtils.map(userDto, User.class);
        this.userService.signUp(userNew);

        ActivatingUserDto activatingUserDto = ObjectMapperUtils.map(userNew, ActivatingUserDto.class);

        return new ResponseEntity<>(activatingUserDto, headers, HttpStatus.CREATED);
    }


    @GetMapping(value = "/activate/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void activate(Model model, @PathVariable("code") String code){
        if(!StringUtils.isEmpty(code)){
            this.userService.activateUser(code);
            //return new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND);
        }

       // return new ResponseEntity<String>("User successfully activated", HttpStatus.FOUND);
    }


    /**
     *
     * @param requestDto
     * @return
     */

    @PostMapping(value = "in", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto requestDto) {

        try {
            String username = requestDto.getUsername();
            String password = requestDto.getPassword();

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(username, password);

            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            User user = userService.findByUsername(username);

            if (user == null || user.getUserStatus() == UserStatus.DELETED) {

                RestMessage restMessage = this.restMessageService.findByCode("U002");
                RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);

                return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
            }

            String token = jwtTokenProvider.createToken(username, user.getRoles());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {

            RestMessage restMessage = this.restMessageService.findByCode("U006");
            RestMessageDto restMessageDto = ObjectMapperUtils.map(restMessage, RestMessageDto.class);
            //throw new BadCredentialsException("Invalid username or password");
            return new ResponseEntity<RestMessageDto>(restMessageDto, HttpStatus.NOT_FOUND);
        }
    }

}
