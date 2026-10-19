package com.prog5121.poe;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Console entry point for QuickChat Part 2. */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            run(scanner);
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input closed. Exiting QuickChat. Total messages sent: "
                    + Message.returnTotalMessages());
        }
    }

    private static void run(Scanner scanner) {
        Login login = new Login();

        // ---------- Registration (repeats until the details are valid) ----------
        System.out.println("=== QuickChat Registration ===");
        String registration;
        do {
            System.out.print("First name: ");
            login.setFirstName(scanner.nextLine().trim());
            System.out.print("Last name: ");
            login.setLastName(scanner.nextLine().trim());
            System.out.print("Username (must contain '_' and be 5 characters or fewer): ");
            login.setUsername(scanner.nextLine().trim());
            System.out.print("Password (8+ characters, capital, number and special character): ");
            login.setPassword(scanner.nextLine());
            System.out.print("South African cell number (e.g. +27838968976): ");
            login.setCellPhoneNumber(scanner.nextLine().trim());

            registration = login.registerUser();
            System.out.println(registration);
            if (!"User successfully registered.".equals(registration)) {
                System.out.println("Please try again.\n");
            }
        } while (!"User successfully registered.".equals(registration));

        // ---------- Login (repeats until correct) ----------
        System.out.println("\n=== QuickChat Login ===");
        boolean loggedIn;
        do {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            loggedIn = login.loginUser(username, password);
            System.out.println(login.returnLoginStatus(loggedIn));
        } while (!loggedIn);

        // ---------- Messaging (only reachable after a successful login) ----------
        System.out.println("\nWelcome to QuickChat.");
        System.out.print("How many messages would you like to enter? ");
        int messageLimit = readPositiveInt(scanner);

        int messagesEntered = 0;
        boolean quit = false;
        while (!quit) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> {
                    if (messagesEntered >= messageLimit) {
                        System.out.println("You have already entered the " + messageLimit
                                + " message(s) you selected at startup.");
                    } else {
                        processOneMessage(scanner, messagesEntered);
                        messagesEntered++;
                        if (messagesEntered == messageLimit) {
                            System.out.println("\nAll " + messageLimit + " message(s) entered. "
                                    + "Total messages sent: " + Message.returnTotalMessages());
                        }
                    }
                }
                case "2" -> System.out.println("Coming Soon.");
                case "3" -> quit = true;
                default -> System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
        System.out.println("Goodbye!");
    }

    /** Captures a single message from the user and handles Send / Disregard / Store. */
    private static void processOneMessage(Scanner scanner, int messageNumber) {
        // Recipient (repeat until valid)
        String recipient;
        while (true) {
            System.out.print("Recipient cell number (e.g. +27718693002): ");
            recipient = scanner.nextLine().trim();
            if (Message.isValidCellNumber(recipient)) {
                System.out.println("Cell phone number successfully captured.");
                break;
            }
            System.out.println("Cell phone number is incorrectly formatted or does not contain "
                    + "an international code. Please correct the number and try again.");
        }

        // Message text (repeat until 250 characters or fewer)
        Message message;
        while (true) {
            System.out.print("Enter message (maximum 250 characters): ");
            String body = scanner.nextLine();
            message = new Message(recipient, body, messageNumber);
            if (message.isMessageLengthValid()) {
                System.out.println(message.checkMessageLength());
                break;
            }
            System.out.println(message.checkMessageLength());
        }
        System.out.println(message.messageIdCreated());

        // Send / Disregard / Store (repeat until a valid choice is made)
        String result;
        while (true) {
            System.out.println("\n1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose an action: ");
            String action = scanner.nextLine().trim();
            result = message.SentMessage(action);
            if (!result.startsWith("Invalid")) break;
            System.out.println(result);
        }
        System.out.println(result);

        switch (message.getStatus()) {
            case "Sent" -> {
                System.out.println("\n" + message.getMessageDetails());
            }
            case "Disregarded" -> {
                System.out.print("Enter 0 to confirm: ");
                if (scanner.nextLine().trim().equals("0")) {
                    System.out.println(message.deleteMessage());
                } else {
                    System.out.println("Message disregarded.");
                }
            }
            case "Stored" -> {
                try {
                    Message.storeMessage(Path.of(Message.DEFAULT_STORE_FILE));
                    System.out.println("Messages stored in " + Message.DEFAULT_STORE_FILE);
                } catch (Exception e) {
                    System.out.println("Unable to store messages: " + e.getMessage());
                }
            }
            default -> { }
        }
    }

    private static int readPositiveInt(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= 1) return value;
            } catch (NumberFormatException ignored) { }
            System.out.print("Please enter a whole number of 1 or more: ");
        }
    }
}
