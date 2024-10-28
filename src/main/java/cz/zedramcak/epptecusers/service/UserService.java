package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.entity.dto.UserDTO;

import java.util.List;

public interface UserService {
    void addUser(User user);
    void removeUser(String userId);
    List<UserDTO> getAllUsers();
    List<UserDTO> findUsers(String firstName, String lastName, String birthNumber);
}
