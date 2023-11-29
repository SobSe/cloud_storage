package ru.sobse.cloud_storage;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import ru.sobse.cloud_storage.DTO.FileDTO;
import ru.sobse.cloud_storage.DTO.FileUpdateDTO;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;
import ru.sobse.cloud_storage.entity.FileDAO;
import ru.sobse.cloud_storage.entity.UserDAO;
import ru.sobse.cloud_storage.repositories.FileRepo;
import ru.sobse.cloud_storage.repositories.UserRepo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudStorageApplicationTests {

	private static final GenericContainer<?> app = new GenericContainer<>("cloud_storage_test:latest")
			.withExposedPorts(5500);
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private FileRepo fileRepo;

	private UserDAO user;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	public void beforeEach(){
		user = UserDAO.builder()
				.login("shutle@gmail.com")
				.password("123")
				.token("123456")
				.build();
		fileRepo.deleteAll();
		userRepo.deleteAll();

		app.start();
	}

	@Test
	@Order(1)
	public void loginTest() {
		//arrange
		UserDTO user = UserDTO.builder()
				.login("login@gmail.com")
				.password("123")
				.build();
		String url = "http://localhost:" + app.getMappedPort(5500) + "/login";
		//act
		ResponseEntity<TokenDTO> forEntity = testRestTemplate.postForEntity(url,
				user,
				TokenDTO.class);
		TokenDTO token = forEntity.getBody();
		//assert
		Assertions.assertNotNull(token.getAuthToken());
        Assertions.assertFalse(token.getAuthToken().isEmpty());
	}

	@Test
	@Order(2)
	public void pingTest() {
		//arrange
		String url = "http://localhost:" + app.getMappedPort(5500) + "/ping";
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);
		String expected = "OK";
		//act
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.GET
				, new HttpEntity<>(headers)
				, String.class);
		String actual = responseEntity.getBody();
		//assert
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@Order(3)
	public void listTest() {
		//arrange
		String fileName = "data_list.sql";
		String url = "http://localhost:" + app.getMappedPort(5500) + "/list?limit=3";
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);

		//Save file in storage
		saveFile("http://localhost:" + app.getMappedPort(5500) + "/file?filename=" + fileName
				, headers);

		//act
		ResponseEntity<List<FileDTO>> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.GET
				, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
                });
		List<FileDTO> files = responseEntity.getBody();
		//assert
		Assertions.assertNotNull(files);
        Assertions.assertFalse(files.isEmpty());
	}

	@Test
	@Order(4)
	public void filePostTest() {
		//arrange
		String url = "http://localhost:" + app.getMappedPort(5500) + "/file?filename=data.sql";
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		Resource file = new FileSystemResource("src/main/resources/data.sql");
		body.add("file", file);
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);

		HttpStatus expected = HttpStatus.resolve(200);
		//act
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.POST
				, httpEntity
				, String.class);
		HttpStatus actual = HttpStatus.resolve(responseEntity.getStatusCode().value());
		//assert
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@Order(5)
	public void fileGetTest() throws IOException {
		String fileName = "data_get.sql";
		String url = "http://localhost:" + app.getMappedPort(5500) + "/file?filename=" + fileName;
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);

		//Save file in storage
		Resource file = saveFile("http://localhost:"
				+ app.getMappedPort(5500)
				+ "/file?filename=" + fileName
				, headers);

		HttpStatus expected = HttpStatus.resolve(200);
		//act
		ResponseEntity<Resource> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.GET
				, new HttpEntity<>(headers), Resource.class);
		HttpStatus actual = HttpStatus.resolve(responseEntity.getStatusCode().value());
		//assert
		Assertions.assertEquals(expected, actual);
		Assertions.assertArrayEquals(file.getContentAsByteArray(), responseEntity.getBody().getContentAsByteArray());
	}

	@Test
	@Order(6)
	public void fileUpdateTest() {
		//arrange
		String fileName = "data_update.sql";
		String url = "http://localhost:" + app.getMappedPort(5500) + "/file?filename=" + fileName;
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);

		FileUpdateDTO fileUpdate = FileUpdateDTO.builder()
				.fileName("data_update_rename.sql")
				.build();
		HttpEntity<FileUpdateDTO> httpEntity = new HttpEntity<>(fileUpdate, headers);

		//Save file in storage
		saveFile("http://localhost:" + app.getMappedPort(5500) + "/file?filename=" + fileName
				, headers);

		HttpStatus expected = HttpStatus.resolve(200);
		//act
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.PUT
				, httpEntity
				, String.class);
		HttpStatus actual = HttpStatus.resolve(responseEntity.getStatusCode().value());
		//assert
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@Order(7)
	public void fileDeleteTest() {
		String fileName = "data_delete.sql";
		String url = "http://localhost:" + app.getMappedPort(5500) + "/file?filename=" + fileName;
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);

		HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);

		//Save file in storage
		saveFile("http://localhost:" + app.getMappedPort(5500) + "/file?filename=" + fileName
				, headers);

		HttpStatus expected = HttpStatus.resolve(200);
		//act
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.DELETE
				, httpEntity
				, String.class);
		HttpStatus actual = HttpStatus.resolve(responseEntity.getStatusCode().value());
		//assert
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@Order(8)
	public void logOutTest() {
		//arrange
		String url = "http://localhost:" + app.getMappedPort(5500) + "/logout";
		String authToken = "Bearer 01d3da2b-d02e-42fe-8a08-fdb6d1f597d6";
		HttpHeaders headers = new HttpHeaders();
		headers.add("auth-token", authToken);

		HttpStatus expected = HttpStatus.resolve(200);
		//act
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(url
				, HttpMethod.POST
				, new HttpEntity<>(headers)
				, String.class);
		HttpStatus actual = HttpStatus.resolve(responseEntity.getStatusCode().value());
		//assert
		Assertions.assertEquals(expected, actual);
	}

	@NotNull
	private Resource saveFile(String url, HttpHeaders headers) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		Resource file = new FileSystemResource("src/main/resources/data.sql");
		body.add("file", file);
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> responseSaveFile = testRestTemplate.exchange(url
				, HttpMethod.POST
				, httpEntity
				, String.class);
		return file;
	}

	@Test
	public void saveUser() {
		//arrange
		//act
		UserDAO savedUser = userRepo.save(user);
		//assert
		Assertions.assertNotNull(savedUser);
		Assertions.assertEquals(savedUser.getLogin(), user.getLogin());
	}

	@Test
	public void getUserByTokenTest() {
		//arrange
		String tokenExpected = "123456";
		UserDAO savedUser = userRepo.save(user);
		//act
		Optional<UserDAO> userDAOOptional = userRepo.getUserByToken(tokenExpected);
		//assert
		Assertions.assertTrue(userDAOOptional.isPresent());
		Assertions.assertEquals(tokenExpected, userDAOOptional.get().getToken());
	}

	@Test
	public void saveFileTest() {
		//arrange
		UserDAO userDAO = userRepo.save(user);
		FileDAO fileDAO = FileDAO.builder()
				.fileName("Test.jpg")
				.data(new byte[1])
				.size(1)
				.user(userDAO)
				.build();
		//act
		FileDAO saveFileDao = fileRepo.save(fileDAO);
		//assert
		Assertions.assertNotNull(saveFileDao);
		Assertions.assertEquals(saveFileDao,  fileDAO);
	}

	@Test
	public void findFileByUserTest() {
		UserDAO userDAO = userRepo.save(user);
		FileDAO fileDAO = FileDAO.builder()
				.fileName("Test.jpg")
				.data(new byte[1])
				.size(1)
				.user(userDAO)
				.build();
		FileDAO saveFileDao = fileRepo.save(fileDAO);
		//act
		List<FileDAO> files = fileRepo.findByUser(userDAO, PageRequest.of(0, 3));
		//assert
		Assertions.assertFalse(files.isEmpty());
	}

}
