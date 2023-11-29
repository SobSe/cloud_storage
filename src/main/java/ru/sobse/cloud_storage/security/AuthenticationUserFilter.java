package ru.sobse.cloud_storage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.entity.UserDAO;
import ru.sobse.cloud_storage.exeption.TokenNotFound;
import ru.sobse.cloud_storage.service.UserService;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthenticationUserFilter extends OncePerRequestFilter { //GenericFilterBean {
    private final String AUTHORIZATION = "auth-token";
    private final UserService service;
    private final HandlerExceptionResolver resolver;
/*    @Autowired
    @Qualifier("SecurityContextImpl")
    private SecurityContext context;*/

    public AuthenticationUserFilter(UserService service
            , @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.service = service;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null) {
            Optional<UserDAO> userOptional = service
                    .findByToken(new TokenDTO(token));
            if (userOptional.isPresent()) {
                UserDAO user = userOptional.get();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                resolver.resolveException(request, response, null, new TokenNotFound("Token not found"));
                return;
            }
        } else if (!request.getRequestURI().equals("/login")) {
            resolver.resolveException(request, response, null, new TokenNotFound("Token not found"));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authToken = request.getHeader(AUTHORIZATION);
        if (authToken != null && authToken.startsWith("Bearer ")) {
            return authToken.split(" ")[1];
        }
        return null;
    }
}
