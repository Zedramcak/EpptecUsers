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
    private final Map<Integer, User> userDatabase = new HashMap<>();
    private Integer userIdCounter = 0;

    public void addUser(User user){
        userDatabase.forEach((k, v) -> {
            if (user.getBirthNumber().equals(v.getBirthNumber())){
                throw new UserExistsException("User with this BirthNumber is already in the userDatabase");
            }
        });

        userDatabase.put(userIdCounter, user);

        log.info("Added user " + user + " with id " + userIdCounter + " to the userDatabase");

        raiseCounter();
    }


    public void removeUser(Integer userId){
        if (!userDatabase.containsKey(userId)){
            throw new UserDoesNotExistsException("User with id " + userId + " does not exists");
        }
        userDatabase.remove(userId);
    }

    public Map<Integer, User> getAllUsers(){
        return userDatabase;
    }

    public Map<Integer, User> getUsersWithParameters(User usersToSearch){
        Map<Integer, User> foundUsers = new HashMap<>();
        if (!usersToSearch.getBirthNumber().isBlank()){
            if (usersToSearch.getFirstName().isBlank() && usersToSearch.getLastName().isBlank()){
                userDatabase.forEach((k, v) -> {
                    if (v.getBirthNumber().equals(usersToSearch.getBirthNumber())){
                        foundUsers.put(k, v);
                    }
                });
            } else if (!usersToSearch.getFirstName().isBlank() && usersToSearch.getLastName().isBlank()){
                userDatabase.forEach((k, v) -> {
                    if (v.getFirstName().equals(usersToSearch.getFirstName()) && v.getBirthNumber().equals(usersToSearch.getBirthNumber())){
                        foundUsers.put(k, v);
                    }
                });
            } else if (usersToSearch.getFirstName().isBlank()){
                userDatabase.forEach((k, v) -> {
                    if (v.getLastName().equals(usersToSearch.getLastName()) && v.getBirthNumber().equals(usersToSearch.getBirthNumber())){
                        foundUsers.put(k, v);
                    }
                });
            } else{
                userDatabase.forEach((k, v) -> {
                    if (
                        v.getFirstName().equals(usersToSearch.getFirstName())
                        && v.getLastName().equals(usersToSearch.getLastName())
                        && v.getBirthNumber().equals(usersToSearch.getBirthNumber())){
                            foundUsers.put(k, v);
                    }
                });
            }
            return foundUsers;
        }

        if (usersToSearch.getFirstName().isBlank() && usersToSearch.getLastName().isBlank()){
            return userDatabase;
        }

        if (!usersToSearch.getFirstName().isBlank() && !usersToSearch.getLastName().isBlank()){
            userDatabase.forEach((k, v) -> {
                if (v.getFirstName().equals(usersToSearch.getFirstName()) && v.getLastName().equals(usersToSearch.getLastName())){
                    foundUsers.put(k, v);
                }
            });
        } else if (!usersToSearch.getFirstName().isBlank()){
            userDatabase.forEach((k, v) -> {
                if (v.getFirstName().equals(usersToSearch.getFirstName())){
                    foundUsers.put(k, v);
                }
            });
        } else {
            userDatabase.forEach((k, v) -> {
                if (v.getLastName().equals(usersToSearch.getLastName())){
                    foundUsers.put(k, v);
                }
            });
        }

        return foundUsers;
    }


    private void raiseCounter() {
        userIdCounter++;
    }
}
