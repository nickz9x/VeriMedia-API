package com.vestingCustodyApp.vca.config;

import com.vestingCustodyApp.vca.entity.User;
import com.vestingCustodyApp.vca.service.TokenService;
import com.vestingCustodyApp.vca.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@AllArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoveryToken(request);
        if (token != null){
            String login = tokenService.validateToken(token);
            UserDetails byLogin = userService.findByLoginDetails(login);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(byLogin,null,byLogin.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request,response);
    }


    private String recoveryToken(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (authorization == null) return null;
        return authorization.replace("Bearer ","");
    }
}
