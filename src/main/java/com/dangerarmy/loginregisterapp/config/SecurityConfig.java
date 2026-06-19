package com.dangerarmy.loginregisterapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig { 


    @Autowired
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder setPasswordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        System.out.println(">>> LOADING SECURITY CONFIG <<<");

        return httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)

                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/req/signup", "/req/login","/req/signup/save",
                                            "/req/login/**","/req/login/verify","/favicon.ico","/test","/verify/email",
                                             "/req/logout","/html/logout.html","/test/**",
                                             "/req/forgotPass","/html/forgotPass.html","/redis-test",
                                             "/html/resend-verification.html","/resend-email",
											 "/css/**","/js/**","/images/**","/static/**","/html/**","/favicon.ico")
                                .permitAll();
                        auth.requestMatchers("/admin/**").hasRole("ADMIN");
                        auth.anyRequest().authenticated();
                    })

                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((request,
                                                       response,
                                                       authException) ->
                                    response.sendRedirect("/req/login")
                            )
                    )

                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                    .addFilterBefore(jwtFilter , UsernamePasswordAuthenticationFilter.class)

                    .build();
    }
}
