package ru.logisticplatform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.logisticplatform.security.jwt.JwtConfigurer;
import ru.logisticplatform.security.jwt.JwtTokenProvider;

/**
 * Security configuration class for JWT based Spring Security application.
 *
 * @author Sergei Perminov
 * @version 1.0
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String ADMIN_ENDPOINT = "/api/v1/admins/**";
    private static final String LOGIN_ENDPOINT = "/api/v1/sign/**";
    private static final String USER_ENDPOINT = "/api/v1/users/**";
    private static final String CUSTOMER_ENDPOINT = "/api/v1/customers/**";
    private static final String CONTRACTOR_ENDPOINT = "/api/v1/contractors/**";

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()//new
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .antMatchers(USER_ENDPOINT).hasAnyRole("ADMIN", "CONTRACTOR", "CUSTOMER")
                .antMatchers(CUSTOMER_ENDPOINT).hasAnyRole("ADMIN", "CUSTOMER")
                .antMatchers(CONTRACTOR_ENDPOINT).hasAnyRole("ADMIN", "CONTRACTOR")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}







//@Configuration
//@EnableWebSecurity
//public class SecurityConfig
//        extends WebSecurityConfigurerAdapter implements ApplicationContextAware {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                // ...
//                .csrf().disable();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        PasswordEncoder encoder =
//                PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        auth
//                .inMemoryAuthentication()
//                .withUser("user")
//                .password(encoder.encode("password"))
//                .roles("USER")
//                .and()
//                .withUser("admin")
//                .password(encoder.encode("admin"))
//                .roles("USER", "ADMIN");
//    }
//
//
//}
