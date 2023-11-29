package ru.sobse.cloud_storage;

import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.sobse.cloud_storage.DTO.FileDTO;
import ru.sobse.cloud_storage.DTO.FileUpdateDTO;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;
import ru.sobse.cloud_storage.controller.CloudStorageController;
import ru.sobse.cloud_storage.controller.CloudStorageControllerImpl;
import ru.sobse.cloud_storage.service.FileService;
import ru.sobse.cloud_storage.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudStorageControllerTest {
    public UserService userService;
    public FileService fileService;

    @BeforeEach
    public void beforeEach() {
        userService = Mockito.mock(UserService.class);
        fileService = Mockito.mock(FileService.class);
    }

    @Test
    public void loginTest() {
        //arrange
        UserDTO userDTO = new UserDTO("shutle@gmail.com", "123");
        TokenDTO tokenDTOExpected = new TokenDTO("123456");
        String token = "123456";

        Mockito.when(userService.login(userDTO)).thenReturn(token);

        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        //act
        TokenDTO tokenDTOActual = controller.login(userDTO);
        //assert
        Assertions.assertEquals(tokenDTOExpected, tokenDTOActual);
    }

    @Test
    public void logoutTest() {
        //arrange
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        int wantedNumberOfInvocations = 1;
        String token = "123456";
        //act
        controller.logout(token);
        //assert
        Mockito.verify(userService, Mockito.times(wantedNumberOfInvocations)).logout(token);
    }

    @Test
    public void pingTest() {
        //arrange
        String expected = "OK";
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        //act
        String actual = controller.ping();
        //assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void listTest() {
        //arrange
        List<FileDTO> filesExpected = Arrays.asList(new FileDTO("Test_1.jpg", 1000)
                , new FileDTO("Test_2.jpg", 1001));
        Pageable pageable = Mockito.mock(Pageable.class);
        Mockito.when(fileService.list(PageRequest.of(0, 2))).thenReturn(filesExpected);
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        //act
        List<FileDTO> fileActual = controller.list(2);
        //assert
        Assertions.assertEquals(filesExpected, fileActual);
    }

    @Test
    public void createTest() {
        //arrange
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        int wantedNumberOfInvocations = 1;
        String fileName = "Test.jpg";
        MultipartFile file = new MockMultipartFile(fileName, new byte[1]);
        //act
        controller.create("Test.jpg", file);
        //assert
        Mockito.verify(fileService, Mockito.times(wantedNumberOfInvocations)).create(fileName, file);
    }

    @Test
    public void updateTest() {
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        int wantedNumberOfInvocations = 1;
        String fileName = "Test.jpg";
        FileUpdateDTO fileUpdateDTO = new FileUpdateDTO("Test_1.jpg");
        MultipartFile file = new MockMultipartFile(fileName, new byte[1]);
        //act
        controller.update(fileName, fileUpdateDTO);
        //assert
        Mockito.verify(fileService, Mockito.times(wantedNumberOfInvocations)).update(fileName, fileUpdateDTO);
    }

    @Test
    public void readTest() {
        //arrange
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        int wantedNumberOfInvocations = 1;
        String fileName = "Test.jpg";
        Resource fileExpected = new ByteArrayResource(new byte[1]);
        Mockito.when(fileService.read(fileName)).thenReturn(fileExpected);
        //act
        Resource fileActual = controller.read(fileName);
        //assert
        Assertions.assertEquals(fileExpected, fileActual);
    }

    @Test
    public void deleteTest() {
        CloudStorageController controller = new CloudStorageControllerImpl(userService, fileService);
        int wantedNumberOfInvocations = 1;
        String fileName = "Test.jpg";
        //act
        controller.delete(fileName);
        //assert
        Mockito.verify(fileService, Mockito.times(wantedNumberOfInvocations)).delete(fileName);
    }
}
