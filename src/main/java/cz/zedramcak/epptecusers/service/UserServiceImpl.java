package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.entity.dto.UserDTO;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.exceptions.UserDoesNotExistsException;
import cz.zedramcak.epptecusers.exceptions.UserExistsException;
import cz.zedramcak.epptecusers.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(User user) {
        validateUserFields(user);

        setBirthNumberFormat(user);

        ensureUserDoesNotExist(user.getBirthNumber());

        userRepository.addUser(user);
    }

    private void validateUserFields(User user) {
        ensureFieldsAreNotEmpty(user);

        validateBirthNumber(user.getBirthNumber());
    }

    private static void ensureFieldsAreNotEmpty(User user) {
        if (user.getFirstName().isBlank() || user.getLastName().isBlank() || user.getBirthNumber().isBlank()){
            throw new MissingDataException("First name and last name are required");
        }
    }

    private void validateBirthNumber(String birthNumber) {
        if (!isBirthNumberValid(birthNumber)){
            throw new IncorrectBirthNumberFormatException("The Birth Number is invalid.");
        }
    }

    private void ensureUserDoesNotExist(String birthNumber) {
        if (userRepository.existsUserByBirthNumber(birthNumber))
            throw new UserExistsException("User with this Birth Number already exists.");
    }

    @Override
    public void removeUser(String userId) {
        Integer id = parsedUserId(userId);

        ensureUserExists(id);

        userRepository.removeUser(id);
    }

    private void ensureUserExists(Integer id) {
        if (!userRepository.existsUserById(id))
            throw new UserDoesNotExistsException("User with id " + id + " does not exists");
    }

    private static int parsedUserId(String userId) {
        try {
            return Integer.parseInt(userId);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("The userId is invalid.");
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        Map<Integer, User> users = userRepository.getAllUsers();
        return convertToUserDTOs(users);
    }

    @Override
    public List<UserDTO> findUsers(String firstName, String lastName, String birthNumber) {
        Map<Integer, User> foundUsers = userRepository.getAllUsers()
                .entrySet()
                .stream()
                .filter(user -> firstName == null || user.getValue().getFirstName().equals(firstName))
                .filter(user -> lastName == null || user.getValue().getLastName().equals(lastName))
                .filter(user -> birthNumber == null || user.getValue().getBirthNumber().equals(birthNumber))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return convertToUserDTOs(foundUsers);

    }

    private List<UserDTO> convertToUserDTOs(Map<Integer, User> users) {
        List<UserDTO> filteredUsers = new ArrayList<>();

        users.forEach((userId, user) -> {
            UserDTO userDTO = createUserDTO(userId, user);
            filteredUsers.add(userDTO);
        });

        return filteredUsers;
    }

    private UserDTO createUserDTO(Integer userId, User user) {
        int age = calculateAgeFromBirthNumber(user.getBirthNumber());
        return new UserDTO(userId, user.getBirthNumber(), user.getFirstName(), user.getLastName(), age);
    }

    private Integer calculateAgeFromBirthNumber(String birthNumber) {
        
        int year = Integer.parseInt(birthNumber.substring(0, 2));
        int month = Integer.parseInt(birthNumber.substring(2, 4));
        int day = Integer.parseInt(birthNumber.substring(4, 6));

        if (month > 50) {
            month = month - 50;
        }

        int thisYear = LocalDate.now().getYear() % 100;
        int century = (year > thisYear) ? 1900 : 2000;
        int fullYear = century + year;

        LocalDate dateOfBirth = LocalDate.of(fullYear, month, day);
        LocalDate today = LocalDate.now();

        int age =  today.getYear() - dateOfBirth.getYear();

        if (today.isBefore(dateOfBirth.plusYears(age))) {
            age--;
        }

        return age;
    }

    private void setBirthNumberFormat(User user) {
        String birthNumber = user.getBirthNumber();

        if (birthNumber.matches("\\d{10}")) {
            user.setBirthNumber(birthNumber.substring(0, 6) + "/" + birthNumber.substring(6));
        }
    }

    private Boolean isBirthNumberValid(String birthNumber) {
        String regex = "(\\d{2})(\\d{2})(\\d{2})/?(\\d{4})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(birthNumber);

        if (!matcher.matches()) {
            return false;
        }

        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));

        if (month > 50) {
            month = month - 50;
        }

        if (month < 1 || month > 12) {
            return false;
        }

        int maxDayInMonth = getMaxDayInMonth(year, month);

        return day >= 1 && day <= maxDayInMonth;

    }

    private int getMaxDayInMonth(int year, int month) {
        return switch (month) {
            case 2 -> ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) ? 29 : 28;
            case 4, 6, 9, 11 -> 30;
            default -> 31;
        };
    }
}
