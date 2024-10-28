package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.entity.dto.UserDTO;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(User user) {
        if (user.getFirstName().isBlank() || user.getLastName().isBlank() || user.getBirthNumber().isBlank()){
            throw new MissingDataException("First name and last name are required");
        }

        if (!isBirthNumberValid(user.getBirthNumber())){
            throw new IncorrectBirthNumberFormatException("The Birth Number is invalid.");
        }

        setBirthNumberFormat(user);

        userRepository.addUser(user);
    }

    @Override
    public void removeUser(Integer userId) {
        userRepository.removeUser(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        Map<Integer, User> users = userRepository.getAllUsers();
        return getUserDTOS(users);
    }

    @Override
    public List<UserDTO> getUsersByParameters(User user) {
        Map<Integer, User> users = userRepository.getUsersWithParameters(user);
        return getUserDTOS(users);
    }

    private List<UserDTO> getUserDTOS(Map<Integer, User> users) {
        List<UserDTO> filtredUsers = new ArrayList<>();
        users.forEach((id, user1) -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setFirstName(user1.getFirstName());
            userDTO.setLastName(user1.getLastName());
            userDTO.setBirthNumber(user1.getBirthNumber());
            userDTO.setAge(getAgeFromBirthNumber(user1.getBirthNumber()));

            filtredUsers.add(userDTO);
        });

        return filtredUsers;
    }

    private Integer getAgeFromBirthNumber(String birthNumber) {
        
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
