package cz.zedramcak.epptecusers.service;

import cz.zedramcak.epptecusers.entity.User;
import cz.zedramcak.epptecusers.exceptions.IncorrectBirthNumberFormatException;
import cz.zedramcak.epptecusers.exceptions.MissingDataException;
import cz.zedramcak.epptecusers.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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

        log.info("User added -> {}", user);
    }

    @Override
    public void removeUser(Integer userId) {
        userRepository.removeUser(userId);
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    private void setBirthNumberFormat(User user) {
        user.setBirthNumber(user.getBirthNumber().replaceAll("/", ""));
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
