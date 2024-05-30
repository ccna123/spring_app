package com.example.spring_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring_app.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.example.spring_app.model.AuthenticationResponse;
import com.example.spring_app.model.User;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User login(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("not found"));
    }

    public AuthenticationResponse register(User req) {
        User user = new User();

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(User req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()));

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

}
