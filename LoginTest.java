package com.prog5121.poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginTest.java
 *
 * Unit tests for the Login class, using the exact test data supplied
 * in the PROG5121 PoE Part 1 brief.
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    // ---------------------------------------------------------------
    // checkUserName()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Username correctly formatted - contains underscore, <= 5 characters")
    void testCheckUserName_CorrectlyFormatted() {
        login.setUsername("kyl_1");
        assertTrue(login.checkUserName());
    }

    @Test
    @DisplayName("Username incorrectly formatted - no underscore / too long")
    void testCheckUserName_IncorrectlyFormatted() {
        login.setUsername("kyle!!!!!!");
        assertFalse(login.checkUserName());
    }

    // ---------------------------------------------------------------
    // checkPasswordComplexity()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Password meets complexity requirements")
    void testCheckPasswordComplexity_Meets() {
        login.setPassword("Ch&&sec@ke99!");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    @DisplayName("Password does not meet complexity requirements")
    void testCheckPasswordComplexity_DoesNotMeet() {
        login.setPassword("password");
        assertFalse(login.checkPasswordComplexity());
    }

    // ---------------------------------------------------------------
    // checkCellPhoneNumber()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Cell phone number correctly formatted")
    void testCheckCellPhoneNumber_Correct() {
        login.setCellPhoneNumber("+27838968976");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    @DisplayName("Cell phone number incorrectly formatted")
    void testCheckCellPhoneNumber_Incorrect() {
        login.setCellPhoneNumber("08966553");
        assertFalse(login.checkCellPhoneNumber());
    }

    // ---------------------------------------------------------------
    // registerUser()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("registerUser - username incorrectly formatted returns correct message")
    void testRegisterUser_UsernameIncorrect() {
        login.setUsername("kyle!!!!!!");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("+27838968976");

        String result = login.registerUser();

        assertEquals("Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.", result);
    }

    @Test
    @DisplayName("registerUser - password does not meet complexity returns correct message")
    void testRegisterUser_PasswordIncorrect() {
        login.setUsername("kyl_1");
        login.setPassword("password");
        login.setCellPhoneNumber("+27838968976");

        String result = login.registerUser();

        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a "
                + "special character.", result);
    }

    @Test
    @DisplayName("registerUser - cell phone incorrectly formatted returns correct message")
    void testRegisterUser_CellPhoneIncorrect() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("08966553");

        String result = login.registerUser();

        assertEquals("Cell phone number incorrectly formatted or does not contain international code.",
                result);
    }

    @Test
    @DisplayName("registerUser - all conditions met returns success message")
    void testRegisterUser_Success() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("+27838968976");
        login.setFirstName("Kyle");
        login.setLastName("Naidoo");

        String result = login.registerUser();

        assertEquals("User successfully registered.", result);
    }

    // ---------------------------------------------------------------
    // loginUser()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Login successful - username and password match registered details")
    void testLoginUser_Successful() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("+27838968976");
        login.setFirstName("Kyle");
        login.setLastName("Naidoo");
        login.registerUser();

        boolean result = login.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertTrue(result);
    }

    @Test
    @DisplayName("Login failed - username or password does not match registered details")
    void testLoginUser_Failed() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("+27838968976");
        login.setFirstName("Kyle");
        login.setLastName("Naidoo");
        login.registerUser();

        boolean result = login.loginUser("kyl_1", "wrongPassword1!");

        assertFalse(result);
    }

    // ---------------------------------------------------------------
    // returnLoginStatus()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("returnLoginStatus - success returns welcome message")
    void testReturnLoginStatus_Success() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("+27838968976");
        login.setFirstName("Kyle");
        login.setLastName("Naidoo");
        login.registerUser();

        boolean loggedIn = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        String result = login.returnLoginStatus(loggedIn);

        assertEquals("Welcome Kyle, Naidoo it is great to see you again.", result);
    }

    @Test
    @DisplayName("returnLoginStatus - failure returns error message")
    void testReturnLoginStatus_Failure() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        login.setCellPhoneNumber("+27838968976");
        login.setFirstName("Andile");
        login.setLastName("Ndlovu");
        login.registerUser();

        boolean loggedIn = login.loginUser("kyl_1", "wrongPassword1!");
        String result = login.returnLoginStatus(loggedIn);

        assertEquals("Username or password incorrect, please try again.", result);
    }
}
