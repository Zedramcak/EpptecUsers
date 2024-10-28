package cz.zedramcak.epptecusers.repository;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Integer, User> UserDatabase = new HashMap<>();
    private Integer userIdCounter = 0;

    public void addUser(User user){
        UserDatabase.forEach((k, v) -> {
            if (v.getBirthNumber().equals(user.getBirthNumber())){
                throw new UserExistsException("User with this BirthNumber is already in the UserDatabase");
            }
        });
        UserDatabase.put(userIdCounter++, user);
    }

    public Map<Integer, User> getAllUsers(){
        return UserDatabase;
    }
}
