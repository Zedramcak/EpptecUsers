package cz.zedramcak.epptecusers.repository;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.getAllUsers().clear();
    }


    @Test
    void whenAddNewUser_thenShouldBeAdded() {
        User userMock1 = new User();
        userMock1.setFirstName("John");
        userMock1.setLastName("Doe");
        userMock1.setBirthNumber("830701/1234");

        userRepository.addUser(userMock1);
        Map<Integer, User> users = userRepository.getAllUsers();
        assertTrue(users.containsValue(userMock1));
    }

    @Test
    void shouldNotAddUserWhenBirthNumberExists() {
        User userMock1 = new User();
        userMock1.setFirstName("John");
        userMock1.setLastName("Doe");
        userMock1.setBirthNumber("830701/1234");

        User userMock2 = new User();
        userMock2.setFirstName("Dwight");
        userMock2.setLastName("Schrute");
        userMock2.setBirthNumber("830701/1234");

        userRepository.addUser(userMock1);
        assertThrows(UserExistsException.class, () -> userRepository.addUser(userMock2));
    }
}