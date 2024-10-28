package cz.zedramcak.epptecusers.repository;

import cz.zedramcak.epptecusers.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j2
public class UserRepository {
    private final Map<Integer, User> userDatabase = new HashMap<>();
    private Integer userIdCounter = 0;

    public void addUser(User user){
        userDatabase.put(userIdCounter, user);

        raiseCounter();
    }


    public void removeUser(Integer userId){
        userDatabase.remove(userId);
    }

    public Map<Integer, User> getAllUsers(){
        return userDatabase;
    }

    public boolean existsUserById(Integer userId){
        return userDatabase.containsKey(userId);
    }

    public boolean existsUserByBirthNumber(String birthNumber){
        return userDatabase.values().stream()
                .anyMatch(user -> user.getBirthNumber().equals(birthNumber));
    }

    private void raiseCounter() {
        userIdCounter++;
    }
}
