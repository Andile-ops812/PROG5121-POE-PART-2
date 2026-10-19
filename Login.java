package com.prog5121.poe;

import java.util.regex.Pattern;

/**
 * This class handles the registration and login of a user.
 * @author Andile Ndlovu ST10452270
 */
public class Login {

    // User details
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Used to check if the user has registered successfully
    private boolean isRegistered = false;

    // Username must contain an underscore and be 5 characters or less
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^(?=.*_).{1,5}$");

    // Password must have 8 or more characters, a capital letter,
    // a number and a special character
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    // South African number must start with +27 and contain digits after it
    private static final Pattern CELLPHONE_PATTERN =
            Pattern.compile("^\\+27\\d{1,10}$");

    // Constructor
    public Login() {
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // Checks if the username follows the required format
    public boolean checkUserName() {
        if (username == null) {
            return false;
        }

        return USERNAME_PATTERN.matcher(username).matches();
    }

    // Checks if the password meets the required conditions
    public boolean checkPasswordComplexity() {
        if (password == null) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // Checks if the cell phone number is in the correct format
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }

        return CELLPHONE_PATTERN.matcher(cellPhoneNumber).matches();
    }

    // Registers the user if all the details are valid
    public String registerUser() {

        boolean userNameOk = checkUserName();
        boolean passwordOk = checkPasswordComplexity();
        boolean cellOk = checkCellPhoneNumber();

        if (!userNameOk) {
            return "Username is not correctly formatted; please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }

        if (!passwordOk) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, and a "
                    + "special character.";
        }

        if (!cellOk) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        isRegistered = true;

        return "User successfully registered.";
    }

    // Checks whether the login details match the registered details
    public boolean loginUser(String loginUsername, String loginPassword) {

        if (!isRegistered) {
            return false;
        }

        return this.username != null
                && this.password != null
                && this.username.equals(loginUsername)
                && this.password.equals(loginPassword);
    }

    // Displays the correct message after a login attempt
    public String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}