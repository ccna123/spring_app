package com.example.spring_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

        @Autowired
        private JwtAuthFilter JwtAuthFilter;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private AuthenticationProvider authenticationProvider;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
                        throws Exception {
                httpSecurity
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/auth/register", "/auth/login", "/api/student/health", "/actuator/*")
                                                .permitAll()
                                                .anyRequest()
                                                // .authenticated()
                                                .permitAll()
                                                )
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .userDetailsService(userDetailsService)
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(JwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return httpSecurity.build();

        }

}
