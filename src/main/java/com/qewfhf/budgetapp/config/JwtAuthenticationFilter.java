package com.qewfhf.budgetapp.config;

import com.mongodb.lang.NonNull;
import com.qewfhf.budgetapp.Users.User;
import com.qewfhf.budgetapp.Users.UserRepository;
import com.qewfhf.budgetapp.Users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String token;
        final String userEmail;
        try{
            String requestUri = request.getRequestURI();
            String[] uriParts = requestUri.split("/");
            String userId = uriParts[2];
            token = parseJwt(request);
            if(token != null){
            userEmail = jwtService.extractUsername(token);
            Optional<User> owner = userRepository.findUserByUserId(userId);
            if(!userId.equals("auth")&&(owner.isEmpty() || !userEmail.equals(owner.get().getEmail()))){
                throw new AccessDeniedException("Access denied: User does not own the resource");
            }
            if(SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser") {
                UserDetails user = this.userService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            }
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtService.getJwtFromCookies(request);
        return jwt;
    }
}
