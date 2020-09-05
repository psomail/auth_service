package ru.logisticplatform.mq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.logisticplatform.dto.user.RoleDto;
import ru.logisticplatform.model.user.UserStatus;
import java.io.Serializable;
import java.util.List;


//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMq implements Serializable {

    String username;
    String firstName;
    String lastName;
    String email;
    String phone;
    List<RoleDto> roles;
    UserStatus userStatus;

//    public UserMq(){
//
//    }

//    public UserMq(@JsonProperty("username") String username,
//                  @JsonProperty("firstName") String firstName
//            ,
//                  @JsonProperty("lastName") String lastName,
//                  @JsonProperty("email") String email,
//                  @JsonProperty("phone") String phone,
//                  @JsonProperty("userStatus") UserStatus userStatus
//                  ){

//        this.username = username;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.phone = phone;
//        this.userStatus = userStatus;
//    }

//    public String getUsername() {
//        return username;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }

//    public String getLastName() {
//        return lastName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public UserStatus getUserStatus() {
//        return userStatus;
//    }

//    public static UserMq fromUser(User user){
//        UserMq userMq = new UserMq();
//        userMq.setUsername(user.getUsername());
//        userMq.setFirstName(user.getFirstName());
//
//        return userMq;
//    }

    @Override
    public String toString() {
        return "UserMq{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userStatus=" + userStatus +
                '}';
    }
}
