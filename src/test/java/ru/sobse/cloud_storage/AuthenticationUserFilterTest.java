package ru.sobse.cloud_storage;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.entity.UserDAO;
import ru.sobse.cloud_storage.exeption.TokenNotFound;
import ru.sobse.cloud_storage.security.AuthenticationUserFilter;
import ru.sobse.cloud_storage.service.UserService;

import java.io.IOException;
import java.util.Optional;

public class AuthenticationUserFilterTest {
    public UserService service;
    public HandlerExceptionResolver resolver;
    public HttpServletRequest request;
    public HttpServletResponse response;
    public FilterChain filterChain;

    @BeforeEach
    public void beforeEach() {
        service = Mockito.mock(UserService.class);
        resolver = Mockito.mock(HandlerExceptionResolver.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    public void doFilterInternalTest() throws IOException, ServletException {
        //arrange
        String token = "Bearer 123456";
        Mockito.when(request.getHeader("auth-token")).thenReturn(token);
        int wantedNumberOfInvocations = 1;

        UserDAO userDAO = new UserDAO();
        Optional<UserDAO> userDAOOptional = Optional.of(userDAO);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDAO, null, userDAO.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(service.findByToken(new TokenDTO("123456"))).thenReturn(userDAOOptional);

        AuthenticationUserFilter authenticationUserFilter = new AuthenticationUserFilter(service, resolver);
        //act
        authenticationUserFilter.doFilter(request, response, filterChain);
        //assert
        Mockito.verify(securityContext, Mockito.times(wantedNumberOfInvocations))
                .setAuthentication(authentication);
    }

    @Test
    public void doFilterInternalNotTokenInHeader() throws ServletException, IOException{
        //arrange
        int wantedNumberOfInvocations = 1;

        Mockito.when(request.getRequestURI()).thenReturn("/list");

        AuthenticationUserFilter authenticationUserFilter = new AuthenticationUserFilter(service, resolver);
        //act
        authenticationUserFilter.doFilter(request, response, filterChain);
        //assert
        Mockito.verify(resolver, Mockito.times(wantedNumberOfInvocations))
                .resolveException(request, response, null, new TokenNotFound("Token not found"));
    }

    @Test
    public void doFilterInternalNotTokenNoFound() throws ServletException, IOException{
        //arrange
        int wantedNumberOfInvocations = 1;
        String token = "Bearer 123456";
        Mockito.when(request.getHeader("auth-token")).thenReturn(token);

        Mockito.when(service.findByToken(new TokenDTO("123456"))).thenReturn(Optional.empty());

        AuthenticationUserFilter authenticationUserFilter = new AuthenticationUserFilter(service, resolver);
        //act
        authenticationUserFilter.doFilter(request, response, filterChain);
        //assert
        Mockito.verify(resolver, Mockito.times(wantedNumberOfInvocations))
                .resolveException(request, response, null, new TokenNotFound("Token not found"));
    }
}
