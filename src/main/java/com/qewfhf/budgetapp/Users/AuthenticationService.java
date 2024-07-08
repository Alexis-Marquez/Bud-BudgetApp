package com.qewfhf.budgetapp.Users;

import com.qewfhf.budgetapp.config.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final User user;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<String> register(AuthenticationRequest request) {
        if(userRepository.findUserByEmail(request.getUsername()).isPresent()) {
            return ResponseEntity.ok("User already exists");
        }
        var user = new User(request.getName(),request.getUsername(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return login(request);
    }

    public ResponseEntity<String> login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtService.generateJwtCookie(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        ResponseCookie jwtRefreshCookie = jwtService.generateRefreshJwtCookie(refreshToken.getToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(user.getUserId());
    }
    ResponseEntity<?> logOut(String user) {
        if (user!=null) {
            refreshTokenService.deleteByUserId(user);
            ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
            ResponseCookie jwtRefreshCookie = jwtService.getCleanJwtRefreshCookie();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new MessageResponse("You've been signed out!"));
        }
        else {
            return ResponseEntity.badRequest().body("You're not logged in!");
        }
    }
}
