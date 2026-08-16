package com.vestingCustodyApp.vca.config;

import com.vestingCustodyApp.vca.service.TokenService;
import com.vestingCustodyApp.vca.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@AllArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = recoveryToken(request);
            if (token != null){
                String login = tokenService.validateToken(token);
                UserDetails byLogin = userService.findByLoginDetails(login);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(byLogin,null,byLogin.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            // token inválido ou usuário inexistente: segue sem autenticação (o entry point responde 401)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request,response);
    }


    private String recoveryToken(HttpServletRequest request){
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) return null;
        return authorization.substring(7);
    }
}
