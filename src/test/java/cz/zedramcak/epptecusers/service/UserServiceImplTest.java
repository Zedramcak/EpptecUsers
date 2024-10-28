package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.entity.dto.UserDTO;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.exceptions.UserDoesNotExistsException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import cz.zedramcak.epptecusers.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addUser_ShouldAddUser_WhenValid() {
        User user = new User();
        user.setFirstName("Jim");
        user.setLastName("Halpert");
        user.setBirthNumber("820101/1234");

        when(userRepository.existsUserByBirthNumber(user.getBirthNumber())).thenReturn(false);

        userService.addUser(user);

        verify(userRepository, times(1)).addUser(user);
    }

    @Test
    public void addUser_ShouldThrowException_WhenBirthNumberExists() {
        User user = new User();
        user.setFirstName("Pam");
        user.setLastName("Beesly");
        user.setBirthNumber("830202/2345");

        when(userRepository.existsUserByBirthNumber(user.getBirthNumber())).thenReturn(true);

        assertThrows(UserExistsException.class, () -> userService.addUser(user));
    }

    @Test
    public void addUser_ShouldThrowException_WhenBirthNumberIsInvalid() {
        User user = new User();
        user.setFirstName("Michael");
        user.setLastName("Scott");
        user.setBirthNumber("invalidBirthNumber");

        assertThrows(IncorrectBirthNumberFormatException.class, () -> userService.addUser(user));
    }

    @Test
    public void addUser_ShouldThrowException_WhenFieldsAreEmpty() {
        User user = new User();
        user.setFirstName("");
        user.setLastName("");
        user.setBirthNumber("820101/1234");

        assertThrows(MissingDataException.class, () -> userService.addUser(user));
    }

    @Test
    public void removeUser_ShouldRemoveUser_WhenUserExists() {
        String userId = "1";
        when(userRepository.existsUserById(1)).thenReturn(true);

        userService.removeUser(userId);

        verify(userRepository, times(1)).removeUser(1);
    }

    @Test
    public void removeUser_ShouldThrowException_WhenUserDoesNotExist() {
        String userId = "2";
        when(userRepository.existsUserById(2)).thenReturn(false);

        assertThrows(UserDoesNotExistsException.class, () -> userService.removeUser(userId));
    }

    @Test
    public void removeUser_ShouldThrowException_WhenUserIdIsInvalid() {
        String userId = "invalid";
        assertThrows(IllegalArgumentException.class, () -> userService.removeUser(userId));
    }

    @Test
    public void getAllUsers_ShouldReturnUserList_WhenUsersExist() {
        Map<Integer, User> users = new HashMap<>();
        User user = new User();
        user.setFirstName("Dwight");
        user.setLastName("Schrute");
        user.setBirthNumber("840303/3456");

        users.put(1, user);
        when(userRepository.getAllUsers()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Dwight", result.get(0).getFirstName());
    }

    @Test
    public void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(userRepository.getAllUsers()).thenReturn(new HashMap<>());

        List<UserDTO> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    public void findUsers_ShouldReturnMatchingUsers() {
        Map<Integer, User> users = new HashMap<>();
        User user1 = new User();
        user1.setFirstName("Jim");
        user1.setLastName("Halpert");
        user1.setBirthNumber("820101/1234");

        User user2 = new User();
        user2.setFirstName("Pam");
        user2.setLastName("Beesly");
        user2.setBirthNumber("830202/2345");

        users.put(1, user1);
        users.put(2, user2);

        when(userRepository.getAllUsers()).thenReturn(users);

        List<UserDTO> result = userService.findUsers("Jim", "Halpert", null);

        assertEquals(1, result.size());
        assertEquals("Jim", result.get(0).getFirstName());
    }

    @Test
    public void findUsers_ShouldReturnEmptyList_WhenNoUsersMatch() {
        Map<Integer, User> users = new HashMap<>();
        when(userRepository.getAllUsers()).thenReturn(users);

        List<UserDTO> result = userService.findUsers("Stanley", "Hudson", null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findUsers_ShouldReturnAllUsers_WhenNoFilterIsProvided() {
        Map<Integer, User> users = new HashMap<>();
        User user1 = new User();
        user1.setFirstName("Jim");
        user1.setLastName("Halpert");
        user1.setBirthNumber("820101/1234");

        User user2 = new User();
        user2.setFirstName("Pam");
        user2.setLastName("Beesly");
        user2.setBirthNumber("830202/2345");

        users.put(1, user1);
        users.put(2, user2);

        when(userRepository.getAllUsers()).thenReturn(users);

        List<UserDTO> result = userService.findUsers(null, null, null);

        assertEquals(2, result.size());
    }

}