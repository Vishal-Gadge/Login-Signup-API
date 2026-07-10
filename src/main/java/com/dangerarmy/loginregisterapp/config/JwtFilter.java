package com.dangerarmy.loginregisterapp.config;

import com.dangerarmy.loginregisterapp.service.JwtService;
import com.dangerarmy.loginregisterapp.service.MyAppUserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyAppUserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/images") ||
                path.startsWith("/static") ||
                path.startsWith("/req/") ||
                path.startsWith("/test") ||
                path.startsWith("/.well-known/appspecific/com.chrome.devtools.json") ||
                path.equals("/favicon.ico") ||
                path.equals("/req/signup") ||
                path.startsWith("/verify/email") ||
                path.startsWith("/html/verifyEmail.html") ||
                path.equals("/req/signup/save") ||
                path.equals("/resend-verification") ||
                path.equals("/resend-email") ||
                path.equals("/html/resend-verification.html") ||

                path.equals("/req/login") ||
                path.equals("/req/login/verify") ||
                path.equals("/req/login.html") ||
                path.equals("/req/logout") ||

                path.equals("/html/forgotPass.html") ||
                path.equals("/req/forgotPass") ||
                path.equals("/verify/forgotPass") ||
                path.equals("/redis-test");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if(token == null && request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("jwt".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token != null){
            try {
                Claims claims = jwtService.extractClaims(token);
                String username = jwtService.getUsername(claims);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (jwtService.verifyToken(userDetails.getUsername(), claims)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }catch (Exception e){
                log.error("JWT validation failed: {}",e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}