package com.sogong.tejava.validate.config;

import com.sogong.tejava.validate.config.util.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@EnableWebSecurity
public class WebSecurityConfig {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public WebSecurityConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors()
                .configurationSource(request -> {
                    CorsConfiguration cors = new CorsConfiguration();
                    cors.setAllowedOrigins(List.of("http://localhost:3000/**", "http://localhost:8080/**", "http://43.200.93.146:8000/**", "http://43.200.93.146/**", "http://43.200.93.146:3000/**"));
                    cors.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));
                    cors.setAllowedHeaders(List.of("*"));

                    return cors;
                })
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(applicationProperties.getWhiteListURLs().toArray(new String[0]))
                .permitAll()

                .and()
//                .formLogin(login -> login
//                        .loginPage("/loginPage")
//                        .permitAll()
//                )
//                .formLogin()
//                    .loginPage("/login")
//                    .failureUrl("/")
//                    .usernameParameter("uid")
//                    .passwordParameter("pw")
//                    .failureHandler(
//                            (request, response, exception) -> {
//                                System.out.println("exception : " + exception.getMessage());
//                                response.sendRedirect("/");
//                            }
//                    )
//                    .permitAll()

                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)
                        )
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me"))
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("remember-me") // check box 의 이름과 동일해야 함!
                        .alwaysRemember(false)
                        .tokenValiditySeconds(86400 * 14)) // 14일
                .build();
    }
}