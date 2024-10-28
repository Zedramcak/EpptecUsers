package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shouldNotAddUserWhenFirstNameMissing() {
        User user = new User();
        user.setFirstName("");
        user.setLastName("Doe");
        user.setBirthNumber("830701/1234");

        assertThrows(MissingDataException.class, () -> userService.addUser(user));
    }

    @Test
    void shouldNotAddUserWhenBirthNumberInvalid() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthNumber("InvalidBirthNumber");

        assertThrows(IncorrectBirthNumberFormatException.class, () -> userService.addUser(user));
    }

    @Test
    void shouldNotAddUserWhenLastNameMissing() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("");
        user.setBirthNumber("830701/1234");

        assertThrows(MissingDataException.class, () -> userService.addUser(user));
    }

    @Test
    void shouldNotAddUserWhenBirthNumberMissing() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthNumber("");

        assertThrows(MissingDataException.class, () -> userService.addUser(user));
    }

    @Test
    void shouldNotAddUserWhenBirthNumberInvalidFormat() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthNumber("830701");

        assertThrows(IncorrectBirthNumberFormatException.class, () -> userService.addUser(user));
    }

    @Test
    void shouldNotAddUserWhenMonthInBirthNumberIsInvalid() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthNumber("831301/1234");

        assertThrows(IncorrectBirthNumberFormatException.class, () -> userService.addUser(user));
    }

    @Test
    void shouldNotAddUserWhenDayInBirthNumberIsInvalid() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthNumber("830732/1234");

        assertThrows(IncorrectBirthNumberFormatException.class, () -> userService.addUser(user));
    }
}