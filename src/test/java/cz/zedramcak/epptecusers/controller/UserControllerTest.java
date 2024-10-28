package cz.zedramcak.epptecusers.controller;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import cz.zedramcak.epptecusers.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void addUserSuccessfully() throws Exception {
        User testUser = new User();
        testUser.setFirstName("Dwight");
        testUser.setLastName("Schrute");
        testUser.setBirthNumber("126231/7717");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName" : "Dwight",
                                "lastName" : "Schrute",
                                "birthNumber" : "126231/7717"}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService, Mockito.times(1)).addUser(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(testUser);
    }

    @Test
    void addUserUserExistsException() throws Exception {
        User testUser = new User();
        testUser.setFirstName("Dwight");
        testUser.setLastName("Schrute");
        testUser.setBirthNumber("126231/7717");
        Mockito.doThrow(UserExistsException.class).when(userService).addUser(Mockito.any(User.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName" : "Dwight",
                                "lastName" : "Schrute",
                                "birthNumber" : "126231/7717"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserMissingDataException() throws Exception {
        User testUser = new User();
        Mockito.doThrow(MissingDataException.class).when(userService).addUser(Mockito.any(User.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName" : "",
                                "lastName" : "",
                                "birthNumber" : "126231/7717"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserIncorrectBirthNumberFormatException() throws Exception {
        User testUser = new User();
        Mockito.doThrow(IncorrectBirthNumberFormatException.class).when(userService).addUser(Mockito.any(User.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName" : "Dwight",
                                "lastName" : "Schrute",
                                "birthNumber" : "126235/7717"}
                                """))
                .andExpect(status().isBadRequest());
    }
}