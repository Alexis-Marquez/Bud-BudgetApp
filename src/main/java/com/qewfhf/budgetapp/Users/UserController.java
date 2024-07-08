package com.qewfhf.budgetapp.Users;

import com.qewfhf.budgetapp.Budgets.Category;
import com.qewfhf.budgetapp.config.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService service;

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/{userId}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserByUserId(userId), HttpStatus.OK);
    }
    @GetMapping("/{userId}/categories")
    public ResponseEntity<List<Category>> getAvailableCategories(@PathVariable String userId){
        return new ResponseEntity<>(userService.getAvailableCategories(userId), HttpStatus.OK);
    }
    @PostMapping("/{userId}/add-category")
    public ResponseEntity<Optional<Category>> addCategory(@PathVariable String userId, @RequestBody Map<String, String> payload){
        Optional<Category> category;
        if (payload.containsKey("name")){
            if(payload.containsKey("total")){
                category = userService.addCategory(userId, new BigDecimal(payload.get("total")), payload.get("name"));
            }
            category =userService.addCategory(userId, BigDecimal.ZERO, payload.get("name"));
        }else{category = Optional.empty();}
        if (category.isPresent()){
            return new ResponseEntity<Optional<Category>>(category, HttpStatus.CREATED);
        }
        return new ResponseEntity<Optional<Category>>(HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/auth/new-user")
    public ResponseEntity<ResponseEntity<String>> createUser(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/auth/log-in")
    public ResponseEntity<String> loginUser(@RequestBody AuthenticationRequest request){
        if(request.getPassword()!=null && !request.getPassword().isEmpty()&&request.getUsername()!=null && !request.getUsername().isEmpty()) {
            return service.login(request);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/auth/sign-out")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String currJWT= jwtService.getJwtFromCookies(request);
        if(currJWT!=null && !currJWT.isEmpty()) {
            String user = jwtService.extractUsername(currJWT);
            return service.logOut(user);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PatchMapping("/{userId}/modify-budget/{newTotal}/{monthYear}") //Only use when creating the first month budget of a new account or when modifying the current budget limit
    public ResponseEntity<Optional<User>> modifyBudget(@PathVariable String userId, @PathVariable BigDecimal newTotal, @PathVariable YearMonth monthYear){
        Optional<User> budget = userService.createBudget(userId, newTotal, monthYear);
        return new ResponseEntity<>(budget, HttpStatus.ACCEPTED);
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtService.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUserId)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtService.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }
}

