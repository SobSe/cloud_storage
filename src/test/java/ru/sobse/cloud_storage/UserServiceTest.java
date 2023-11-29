package ru.sobse.cloud_storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;
import ru.sobse.cloud_storage.entity.UserDAO;
import ru.sobse.cloud_storage.exeption.TokenNotFound;
import ru.sobse.cloud_storage.repositories.UserRepo;
import ru.sobse.cloud_storage.service.TokenGenerator;
import ru.sobse.cloud_storage.service.UserServiceImpl;

import java.util.Optional;

public class UserServiceTest {
    public UserRepo repository;
    public AuthenticationManager authenticationManager;
    public TokenGenerator tokenGenerator;

    @BeforeEach
    public void beforeEach() {
        repository = Mockito.mock(UserRepo.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        tokenGenerator = Mockito.mock(TokenGenerator.class);
    }

    @Test
    public void loginTest() {
        //arrange
        String tokenExpected = "123456";
        UserServiceImpl userService = new UserServiceImpl(repository, authenticationManager, tokenGenerator);
        UserDTO userDTO = new UserDTO("shutle@gmail.com", "123");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDTO.getLogin(), userDTO.getPassword());

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(new UserDAO("shutle@gmail.com", "123", null, 1));

        Mockito.when(tokenGenerator.generate()).thenReturn(tokenExpected);
        Mockito.when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        //act
        String tokenActual = userService.login(userDTO);
        //assert
        Assertions.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void logoutTest() {
        //arrange
        int wantedNumberOfInvocations = 1;
        String tokenBearer = "Bearer 123456";
        String token = "123456";
        UserDAO userDAO = new UserDAO();
        Optional<UserDAO> userDAOOptional = Optional.of(userDAO);

        Mockito.when(repository.getUserByToken(token)).thenReturn(userDAOOptional);

        UserServiceImpl userService = new UserServiceImpl(repository, authenticationManager, tokenGenerator);
        //act
        userService.logout(tokenBearer);
        //assert
        Mockito.verify(repository, Mockito.times(wantedNumberOfInvocations)).save(userDAO);
    }

    @Test
    public void logoutTestTokenNotFound() {
        String tokenBearer = "Bearer 123456";
        String token = "1234567";
        UserDAO userDAO = new UserDAO();
        Optional<UserDAO> userDAOOptional = Optional.of(userDAO);

        Mockito.when(repository.getUserByToken(token)).thenReturn(userDAOOptional);

        UserServiceImpl userService = new UserServiceImpl(repository, authenticationManager, tokenGenerator);
        //act
        Executable action = () -> userService.logout(tokenBearer);;
        //assert
        Assertions.assertThrowsExactly(TokenNotFound.class, action);
    }

    @Test
    public void findByToken() {
        //arrange
        Optional<UserDAO> userDAOExpected = Optional.of(new UserDAO());
        String token = "123456";
        TokenDTO tokenDTO = new TokenDTO(token);

        Mockito.when(repository.getUserByToken(token)).thenReturn(userDAOExpected);

        UserServiceImpl userService = new UserServiceImpl(repository, authenticationManager, tokenGenerator);
        //act
        Optional<UserDAO> userDAOActual = userService.findByToken(tokenDTO);
        //assert
        Assertions.assertEquals(userDAOExpected, userDAOActual);
    }
}
