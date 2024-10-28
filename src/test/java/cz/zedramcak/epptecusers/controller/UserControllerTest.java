package cz.zedramcak.epptecusers.controller;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.entity.dto.UserDTO;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.exceptions.UserDoesNotExistsException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import cz.zedramcak.epptecusers.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setFirstName("Jim");
        user.setLastName("Halpert");
        user.setBirthNumber("820101/1234");

        userDTO = new UserDTO(1, "820101/1234", "Jim", "Halpert", 39);
    }

    @Test
    public void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Jim")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void findUsers_ShouldReturnMatchingUsers() throws Exception {
        when(userService.findUsers("Jim", "Halpert", null)).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/api/v1/users")
                        .param("firstName", "Jim")
                        .param("lastName", "Halpert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Jim")))
                .andExpect(jsonPath("$[0].lastName", is("Halpert")));

        verify(userService, times(1)).findUsers("Jim", "Halpert", null);
    }

    @Test
    public void findUsers_ShouldReturnEmptyList_WhenNoUsersMatch() throws Exception {
        when(userService.findUsers("Michael", "Scott", null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users")
                        .param("firstName", "Michael")
                        .param("lastName", "Scott"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));

        verify(userService, times(1)).findUsers("Michael", "Scott", null);
    }

    @Test
    public void addUser_ShouldAddUser_WhenValid() throws Exception {
        doNothing().when(userService).addUser(any(User.class));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jim\", \"lastName\":\"Halpert\", \"birthNumber\":\"820101/1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(UserController.USER_ADDED));

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    public void addUser_ShouldReturnBadRequest_WhenUserAlreadyExists() throws Exception {
        doThrow(new UserExistsException("User with this Birth Number already exists."))
                .when(userService).addUser(any(User.class));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Pam\", \"lastName\":\"Beesly\", \"birthNumber\":\"830202/2345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with this Birth Number already exists."));

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    public void addUser_ShouldReturnBadRequest_WhenDataIsMissing() throws Exception {
        doThrow(new MissingDataException("First name and last name are required"))
                .when(userService).addUser(any(User.class));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\", \"lastName\":\"\", \"birthNumber\":\"820101/1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("First name and last name are required"));

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    public void removeUser_ShouldRemoveUser_WhenUserExists() throws Exception {
        doNothing().when(userService).removeUser("1");

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(UserController.USER_REMOVED));

        verify(userService, times(1)).removeUser("1");
    }

    @Test
    public void removeUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        doThrow(new UserDoesNotExistsException("User with id 1 does not exist"))
                .when(userService).removeUser("1");

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with id 1 does not exist"));

        verify(userService, times(1)).removeUser("1");
    }

    @Test
    public void removeUser_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        doThrow(new NumberFormatException("The userId is invalid."))
                .when(userService).removeUser("invalid");

        mockMvc.perform(delete("/api/v1/users/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The userId is invalid."));

        verify(userService, times(1)).removeUser("invalid");
    }
}
