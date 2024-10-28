package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import cz.zedramcak.epptecusers.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddUserWhenUserIsValid() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthNumber("830701/1234");

        userService.addUser(user);

        verify(userRepository, times(1)).addUser(user);
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