package com.prog5121.poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Represents one message created during a QuickChat session.
 * Also keeps track of every message sent / stored while the program is running.
 */
public class Message {

    public static final int MAX_LENGTH = 250;
    public static final String DEFAULT_STORE_FILE = "messages.json";

    private static final SecureRandom RANDOM = new SecureRandom();

    // Messages sent / stored while the program is running
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;
    private String status;

    /** Creates a message with a randomly generated ten-digit message ID. */
    public Message(String recipient, String message, int messageNumber) {
        this(generateMessageID(), recipient, message, messageNumber);
    }

    /** Creates a message with a supplied ID (used for predictable unit tests). */
    public Message(String messageID, String recipient, String message, int messageNumber) {
        this.messageID = messageID == null ? "" : messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient.trim();
        this.message = message == null ? "" : message.trim();
        this.messageHash = buildHash();
        this.status = "Pending";
    }

    // Random ten-digit number (leading zeros allowed, always exactly 10 characters)
    private static String generateMessageID() {
        return String.format("%010d", RANDOM.nextLong(10_000_000_000L));
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    /** Message ID must not be more than ten characters (and must be digits). */
    public boolean checkMessageID() {
        return messageID.matches("\\d{1,10}");
    }

    /** International code: starts with '+', then the country code and number (8-15 digits). */
    public static boolean isValidCellNumber(String cell) {
        return cell != null && cell.trim().matches("\\+[1-9]\\d{7,14}");
    }

    public boolean isRecipientValid() {
        return isValidCellNumber(recipient);
    }

    public String checkRecipientCell() {
        if (isRecipientValid()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    public boolean isMessageLengthValid() {
        return message.length() <= MAX_LENGTH;
    }

    public String checkMessageLength() {
        if (isMessageLengthValid()) {
            return "Message ready to send.";
        }
        int excess = message.length() - MAX_LENGTH;
        return "Message exceeds " + MAX_LENGTH + " characters by " + excess
                + "; please reduce the size.";
    }

    public String messageIdCreated() {
        return "Message ID generated: " + messageID;
    }

    // ------------------------------------------------------------------
    // Hash
    // ------------------------------------------------------------------

    /** First two digits of the ID : message number : first word + last word (all caps). */
    public String createMessageHash() {
        return buildHash();
    }

    private String buildHash() {
        String trimmed = message.trim();
        String[] words = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        String first = words.length == 0 ? "" : words[0];
        String last = words.length == 0 ? "" : words[words.length - 1];
        String firstTwo = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (firstTwo + ":" + messageNumber + ":" + first + last)
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9:]", "");
    }

    // ------------------------------------------------------------------
    // Send / disregard / store
    // ------------------------------------------------------------------

    /** Records the user's choice: 1/Send, 2/Disregard or 3/Store. */
    public String SentMessage(String choice) {
        if (choice == null) {
            return "Invalid option. Choose Send, Disregard, or Store.";
        }
        switch (choice.trim().toLowerCase(Locale.ROOT)) {
            case "1", "send" -> {
                String problem = validateForDelivery();
                if (problem != null) return problem;
                status = "Sent";
                totalMessagesSent++;
                sentMessages.add(this);
                return "Message successfully sent.";
            }
            case "2", "disregard" -> {
                status = "Disregarded";
                return "Press 0 to delete the message.";
            }
            case "3", "store" -> {
                String problem = validateForDelivery();
                if (problem != null) return problem;
                status = "Stored";
                storedMessages.add(this);
                return "Message successfully stored.";
            }
            default -> {
                return "Invalid option. Choose Send, Disregard, or Store.";
            }
        }
    }

    // Returns an error message if the message cannot be sent/stored, otherwise null
    private String validateForDelivery() {
        if (!isMessageLengthValid()) {
            return "Please enter a message of less than 250 characters.";
        }
        if (!isRecipientValid()) {
            return checkRecipientCell();
        }
        return null;
    }

    /** Called when the user presses 0 after disregarding a message. */
    public String deleteMessage() {
        status = "Deleted";
        return "Message deleted.";
    }

    // ------------------------------------------------------------------
    // Display
    // ------------------------------------------------------------------

    /** Full details of this message: ID, hash, recipient, message. */
    public String getMessageDetails() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + messageHash + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + message;
    }

    /** Returns all the messages sent while the program is running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentMessages.size(); i++) {
            if (i > 0) sb.append(System.lineSeparator()).append(System.lineSeparator());
            sb.append(sentMessages.get(i).getMessageDetails());
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Method name exactly as it is spelled in the assignment brief
    public static int returnTotalMessagess() {
        return totalMessagesSent;
    }

    /** Clears everything recorded so far (used by unit tests). */
    public static void resetTotalMessagesForTests() {
        totalMessagesSent = 0;
        sentMessages.clear();
        storedMessages.clear();
    }

    public static List<Message> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }

    public static List<Message> getStoredMessages() {
        return Collections.unmodifiableList(storedMessages);
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }

    // ------------------------------------------------------------------
    // JSON storage (no external libraries needed)
    // ------------------------------------------------------------------

    /** Writes all messages stored "to send later" to a JSON file. */
    public static void storeMessage(Path file) throws IOException {
        storeMessage(storedMessages, file);
    }

    /** Writes the given messages to a JSON file. */
    public static void storeMessage(List<Message> messages, Path file) throws IOException {
        Files.writeString(file, toJson(messages), StandardCharsets.UTF_8);
    }

    static String toJson(List<Message> messages) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            sb.append(i == 0 ? "\n" : ",\n");
            sb.append("  {\n");
            sb.append("    \"messageID\": ").append(quote(m.messageID)).append(",\n");
            sb.append("    \"messageHash\": ").append(quote(m.messageHash)).append(",\n");
            sb.append("    \"recipient\": ").append(quote(m.recipient)).append(",\n");
            sb.append("    \"message\": ").append(quote(m.message)).append(",\n");
            sb.append("    \"status\": ").append(quote(m.status)).append(",\n");
            sb.append("    \"messageNumber\": ").append(m.messageNumber).append("\n");
            sb.append("  }");
        }
        sb.append(messages.isEmpty() ? "]" : "\n]");
        return sb.append("\n").toString();
    }

    private static String quote(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }
}
