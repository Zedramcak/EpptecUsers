package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;

import java.util.Map;

public interface UserService {
    void addUser(User user);
    void removeUser(Integer userId);
    Map<Integer, User> getAllUsers();
}
