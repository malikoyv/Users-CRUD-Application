package com.example.UsersCRUD.controller;

import com.example.UsersCRUD.service.UserService;
import com.example.UsersCRUD.model.User;
import com.example.UsersCRUD.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserRepository userRepository;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userController = new UserController(new UserService(userRepository));
    }

    // Success tests
    @Test
    void addUser_success() {
        User testUser = new User(1,
                "test@gmail.com",
                "Test", "Test",
                LocalDate.of(2000, Month.AUGUST, 30));

        // Mock the save method of userRepository to return the test user
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Call the addUser method of userController
        ResponseEntity<User> response = userController.addUser(testUser);

        // Assert that the response status is OK and the body matches the test user
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void updateUsersFieldById_success() {
        User testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Test");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUsersFieldById(1, "firstName", "Yehor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Yehor", response.getBody().getFirstName());
    }

    @Test
    void updateUserFullById_success() {
        User testUser = new User(1,
                "test@gmail.com",
                "Test", "Test",
                LocalDate.of(2000, Month.AUGUST, 30));

        User updatedUser = new User(1,
                "updated@gmail.com",
                "Updated", "Updated",
                LocalDate.of(2005, Month.AUGUST, 30));

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.updateUserFullById(1, updatedUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", response.getBody().getFirstName());
        assertEquals("Updated", response.getBody().getLastName());
        assertEquals("updated@gmail.com", response.getBody().getEmail());
        assertEquals(LocalDate.of(2005, Month.AUGUST, 30), response.getBody().getBirthDate());
    }

    @Test
    void deleteUserById_success() {
        User testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Test");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity response = userController.deleteUserById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void getUsersByRange_success() {
        String start = "2002-01-01";
        String end = "2010-04-01";
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        User testUser1 = new User();
        testUser1.setId(1);
        testUser1.setBirthDate(LocalDate.of(2005, Month.JANUARY, 1));

        User testUser2 = new User();
        testUser2.setId(2);
        testUser2.setBirthDate(LocalDate.of(2012, Month.FEBRUARY, 15));

        List<User> userList = new ArrayList<>();
        userList.add(testUser1);
        userList.add(testUser2);

        when(userRepository.findByBirthDateBetween(startDate, endDate)).thenReturn(userList);
        ResponseEntity<List<User>> response = userController.getUsersByRange(start, end);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser1, response.getBody().get(0));
    }

    // Failed tests
    @Test
    void addUser_failedEmail(){
        User testUser = new User(1,
                "test@gmail.",
                "Test", "Test",
                LocalDate.of(2000, Month.AUGUST, 30));
        when(userRepository.save(testUser)).thenReturn(testUser);
        ResponseEntity<User> response = userController.addUser(testUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addUser_failedBirthdate(){
        User testUser = new User(1,
                "test@gmail.com",
                "Test", "Test",
                LocalDate.of(2010, Month.AUGUST, 30)); // it's not 18 years old
        when(userRepository.save(testUser)).thenReturn(testUser);
        ResponseEntity<User> response = userController.addUser(testUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addUser_failedName(){
        User testUser = new User(1,
                "test@gmail.com",
                null, "Test",   // set first name to null
                LocalDate.of(2000, Month.AUGUST, 30));
        testUser.setBirthDate(LocalDate.of(2005, Month.AUGUST, 30));
        when(userRepository.save(testUser)).thenReturn(testUser);
        ResponseEntity<User> response = userController.addUser(testUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUsersField_failedField(){ // incorrect field
        User testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Test");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUsersFieldById(1, "name", "Yehor");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUsersField_failedId(){ // ID doesn't exist
        User testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Test");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUsersFieldById(0, "firstName", "Yehor");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUsersField_failedEmail(){ // incorrect format of email
        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@com.com");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUsersFieldById(1, "email", "test@com.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUsersField_failedBirthdate(){ // incorrect birthday format
        User testUser = new User();
        testUser.setId(1);
        testUser.setBirthDate(LocalDate.parse("2000-08-30"));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUsersFieldById(1, "birthDate", "2010-08-30");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUserFull_failedEmail(){ // incorrect email format
        User testUser = new User(1,
                "test@gmail.com",
                null, "Test",   // set first name to null
                LocalDate.of(2000, Month.AUGUST, 30));

        User testUser2 = new User(2,
                "test@gmail.",
                "Test", "Test",   // set first name to null
                LocalDate.of(2000, Month.AUGUST, 30));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUserFullById(1, testUser2);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUserFull_failedId(){ // incorrect ID provided
        User testUser = new User(1,
                "test@gmail.com",
                null, "Test",   // set first name to null
                LocalDate.of(2000, Month.AUGUST, 30));

        User testUser2 = new User(2,
                "test@gmail.",
                "Test", "Test",   // set first name to null
                LocalDate.of(2000, Month.AUGUST, 30));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.updateUserFullById(33, testUser2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_failedId(){ // id not found
        User testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Test");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<HttpStatus> response = userController.deleteUserById(12333);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUsersByRange_failedNotFound() { // list is clear
        User testUser = new User();
        testUser.setId(1);
        testUser.setBirthDate(LocalDate.parse("2000-08-30"));
        ArrayList<User> userList = new ArrayList<>();
        userList.add(testUser);

        when(userRepository.findByBirthDateBetween(LocalDate.parse("2005-08-30"),
                LocalDate.parse("2010-08-30"))).thenReturn(userList);
        ResponseEntity<List<User>> response = userController.getUsersByRange("2005-08-30", "2010-08-30");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUsersByRange_failedIncorrectDates() { // start date is after end date
        User testUser = new User();
        testUser.setId(1);
        testUser.setBirthDate(LocalDate.parse("2000-08-30"));
        ArrayList<User> userList = new ArrayList<>();
        userList.add(testUser);

        when(userRepository.findByBirthDateBetween(LocalDate.parse("2005-08-30"),
                LocalDate.parse("2010-08-30"))).thenReturn(userList);
        ResponseEntity<List<User>> response = userController.getUsersByRange("2010-08-30", "2001-08-30"); // error here
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}