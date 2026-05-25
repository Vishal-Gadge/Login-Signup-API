package com.dangerarmy.loginregisterapp.config;

import com.dangerarmy.loginregisterapp.service.JwtService;
import com.dangerarmy.loginregisterapp.service.MyAppUserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyAppUserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/images") ||
                path.startsWith("/static") ||
                path.startsWith("/req/") ||
                path.startsWith("/.well-known/appspecific/com.chrome.devtools.json") ||
                path.equals("/favicon.ico") ||
                path.equals("/req/signup") ||
                path.equals("/req/signup/save") ||
                path.equals("/req/login") ||
                path.equals("/req/login/verify") ||
                path.equals("/req/login.html") ||
                path.equals("/req/logout") ||
                path.equals("/html/forgotPass.html") ||
                path.equals("/req/forgotPass");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("\n\n==== Filter: "+path+"====");

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        System.out.println("Raw Cookie header: "+request.getHeader("Cookie"));
        System.out.println("request.getCookies() is null: "+(request.getCookies() == null));

        if(token == null && request.getCookies() != null){
            System.out.println("Checking cookies...");
            for(Cookie cookie : request.getCookies()){
                System.out.println("Found cookie: "+cookie.getName());
                if("jwt".equals(cookie.getName())){
                    token = cookie.getValue();
                    System.out.println("JWT from cookie: "+token.substring(0,20)+"...");
                    break;
                }
            }
        }

        if(token != null){
            System.out.println("token found");
            try {
                Claims claims = jwtService.extractClaims(token);
                String username = jwtService.getUsername(claims);
                System.out.println("username: "+username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    System.out.println("USER LOADED FROM DB");
                    if (jwtService.verifyToken(userDetails.getUsername(), claims)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Auth set in context");
                        System.out.println("Token is valid , authorization granted");
                    }
                }
            }catch (Exception e){
                logger.debug("JWT validation failed: {}" + e.getMessage());
                System.out.println("Jwt validation failed"+e.getMessage());
            }
        }else{
            System.out.println("no token found");
        }
        filterChain.doFilter(request, response);
    }


}
