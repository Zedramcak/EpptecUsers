package cz.zedramcak.epptecusers.controller;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.entity.dto.UserDTO;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.exceptions.UserDoesNotExistsException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import cz.zedramcak.epptecusers.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public static final String USER_ADDED = "User added";
    public static final String USER_REMOVED = "User removed";

    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> findUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String birthNumber
    ){
        return ResponseEntity.ok(userService.findUsers(firstName, lastName, birthNumber));
    }

    @PostMapping()
    public ResponseEntity<String> addUser(@RequestBody User user){
        try {
            userService.addUser(user);
            return ResponseEntity.ok(USER_ADDED);
        }catch (UserExistsException | MissingDataException |IncorrectBirthNumberFormatException exception){
            return handleException(exception);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeUser(@PathVariable String id){
        try {
            userService.removeUser(id);
            return ResponseEntity.ok(USER_REMOVED);
        }catch (UserDoesNotExistsException | NumberFormatException exception){
            return handleException(exception);
        }
    }

    private ResponseEntity<String> handleException(RuntimeException exception) {
        if (exception instanceof UserDoesNotExistsException) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

}
