package com.example.UsersCRUD.UserService;

import com.example.UsersCRUD.model.User;
import com.example.UsersCRUD.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    // service that adds user to repository
    public ResponseEntity<User> addUser(@RequestBody User user, String age){
        if (isValidUser(user, age)) { // check where user is valid by all parameters
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User newUser = userRepository.save(user); // save to the repository
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    // service that gets user by id and then update chosen field
    public ResponseEntity<User> updateUsersFieldById(@PathVariable long id, @PathVariable String field, @PathVariable String value){
        Optional<User> foundUser = userRepository.findById(id); // finding a user in the repository by id
        if (foundUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = foundUser.get();
        switch(field){
            case "firstName":
                if(value == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                else user.setFirstName(value);
                break;
            case "lastName":
                if(value == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                else user.setLastName(value);
                break;
            case "email":
                if (!validateEmail(value)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                else user.setEmail(value);
                break;
            case "birthDate":
                if (!validateBirthdate(LocalDate.parse(value))) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                else user.setBirthDate(LocalDate.parse(value));
                break;
            case "phoneNumber":
                user.setPhoneNumber(value);
                break;
            case "address":
                user.setAddress(value);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // service that updates full user's data on the new one
    public ResponseEntity<User> updateUserFullById(@PathVariable long id, @RequestBody User user) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            User userData = foundUser.get();
            userData.setEmail(user.getEmail());
            userData.setFirstName(user.getFirstName());
            userData.setLastName(user.getLastName());
            userData.setAddress(user.getAddress());
            userData.setPhoneNumber(user.getPhoneNumber());
            userData.setBirthDate(user.getBirthDate());
            if (isValidUser(userData, "18")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else userRepository.save(userData);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // service that deletes user passed by id
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable long id){
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            userRepository.delete(foundUser.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // service that gets a list of users passed by range of two dates
    public ResponseEntity<List<User>> getUsersByRange(@PathVariable String start, @PathVariable String end) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            if (startDate.isAfter(endDate)) {return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
            List<User> users = userRepository.findByBirthDateBetween(startDate, endDate);
            List<User> filteredUsers = new ArrayList<>();

            for (User user : users) {
                LocalDate userBirthDate = user.getBirthDate();
                // check if user's birthdate is equal to first date or the second date, or between both of them
                if (userBirthDate.isEqual(startDate) || userBirthDate.isEqual(endDate) ||
                        (userBirthDate.isAfter(startDate) && userBirthDate.isBefore(endDate))) {
                    filteredUsers.add(user);
                }
            }

            if (filteredUsers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(filteredUsers, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // method to check whether user's info is valid or not
    private boolean isValidUser(User user, String age) {
        return !validateEmail(user.getEmail()) ||
                user.getFirstName() == null ||
                user.getLastName() == null ||
                !user.getBirthDate().isBefore(LocalDate.now()) ||
                Period.between(user.getBirthDate(), LocalDate.now()).getYears() < Integer.parseInt(age);
    }

    // method checking does user have more than 18 years old
    private boolean validateBirthdate(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    // email validating email
    private boolean validateEmail(String email){
        final Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
