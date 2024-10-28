package cz.zedramcak.epptecusers.repository;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.UserDoesNotExistsException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j2
public class UserRepository {
    private final Map<Integer, User> UserDatabase = new HashMap<>();
    private Integer userIdCounter = 0;

    public void addUser(User user){
        UserDatabase.forEach((k, v) -> {
            if (v.getBirthNumber().equals(user.getBirthNumber())){
                throw new UserExistsException("User with this BirthNumber is already in the UserDatabase");
            }
        });

        UserDatabase.put(userIdCounter, user);

        log.info("Added user " + user + " with id " + userIdCounter + " to the UserDatabase");

        raiseCounter();
    }

    public void removeUser(Integer userId){
        if (!UserDatabase.containsKey(userId)){
            throw new UserDoesNotExistsException("User with id " + userId + " does not exists");
        }
        UserDatabase.remove(userId);
    }

    public Map<Integer, User> getAllUsers(){
        return UserDatabase;
    }

    private void raiseCounter() {
        userIdCounter++;
    }
}
