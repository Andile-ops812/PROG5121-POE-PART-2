package com.prog5121.poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit tests for the Message class, using the test data from the PoE Part 2 brief. */
class MessageTest {

    // Test data - message 1
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    // Test data - message 2
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void reset() {
        Message.resetTotalMessagesForTests();
    }

    // ---------------- Message length ----------------

    @Test
    @DisplayName("Message of 250 characters or less - success")
    void messageLengthSuccess() {
        assertEquals("Message ready to send.",
                new Message(RECIPIENT_1, TEXT_1, 0).checkMessageLength());
        assertEquals("Message ready to send.",
                new Message(RECIPIENT_1, "x".repeat(250), 0).checkMessageLength());
    }

    @Test
    @DisplayName("Message longer than 250 characters - failure")
    void messageLengthFailure() {
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                new Message(RECIPIENT_1, "x".repeat(260), 0).checkMessageLength());
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.",
                new Message(RECIPIENT_1, "x".repeat(251), 0).checkMessageLength());
    }

    // ---------------- Recipient number ----------------

    @Test
    @DisplayName("Recipient number correctly formatted - success")
    void recipientSuccess() {
        assertEquals("Cell phone number successfully captured.",
                new Message(RECIPIENT_1, TEXT_1, 0).checkRecipientCell());
    }

    @Test
    @DisplayName("Recipient number incorrectly formatted - failure")
    void recipientFailure() {
        String expected = "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
        assertEquals(expected, new Message(RECIPIENT_2, TEXT_2, 1).checkRecipientCell());
        assertEquals(expected, new Message("", TEXT_2, 1).checkRecipientCell());
    }

    // ---------------- Message hash ----------------

    @Test
    @DisplayName("Message hash is correct for test case 1")
    void messageHashTestCase1() {
        Message message = new Message("0012345678", RECIPIENT_1, TEXT_1, 0);
        assertEquals("00:0:HITONIGHT", message.getMessageHash());
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    @DisplayName("Message hashes for all test messages (loop)")
    void messageHashesInLoop() {
        String[] ids = {"0012345678", "7712345678"};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {TEXT_1, TEXT_2};
        String[] expected = {"00:0:HITONIGHT", "77:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], recipients[i], texts[i], i);
            assertEquals(expected[i], message.getMessageHash());
        }
    }

    // ---------------- Message ID ----------------

    @Test
    @DisplayName("Message ID is created and is ten digits")
    void messageIdCreated() {
        Message message = new Message(RECIPIENT_1, TEXT_1, 0);
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.checkMessageID());
        assertEquals("Message ID generated: " + message.getMessageID(), message.messageIdCreated());
    }

    @Test
    @DisplayName("Message ID of more than ten characters is rejected")
    void messageIdTooLong() {
        assertTrue(new Message("1234567890", RECIPIENT_1, TEXT_1, 0).checkMessageID());
        assertFalse(new Message("12345678901", RECIPIENT_1, TEXT_1, 0).checkMessageID());
    }

    // ---------------- Send / Disregard / Store ----------------

    @Test
    @DisplayName("User selected 'Send Message'")
    void sendMessage() {
        Message message = new Message(RECIPIENT_1, TEXT_1, 0);
        assertEquals("Message successfully sent.", message.SentMessage("1"));
        assertEquals("Sent", message.getStatus());
    }

    @Test
    @DisplayName("User selected 'Disregard Message'")
    void disregardMessage() {
        Message message = new Message(RECIPIENT_2, TEXT_2, 1);
        assertEquals("Press 0 to delete the message.", message.SentMessage("2"));
        assertEquals("Disregarded", message.getStatus());
        assertEquals("Message deleted.", message.deleteMessage());
    }

    @Test
    @DisplayName("User selected 'Store Message'")
    void storeMessageChoice() {
        Message message = new Message(RECIPIENT_1, TEXT_1, 0);
        assertEquals("Message successfully stored.", message.SentMessage("3"));
        assertEquals("Stored", message.getStatus());
        assertEquals(1, Message.getStoredMessages().size());
    }

    @Test
    @DisplayName("Invalid choice is rejected")
    void invalidChoice() {
        Message message = new Message(RECIPIENT_1, TEXT_1, 0);
        assertTrue(message.SentMessage("9").startsWith("Invalid option"));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("Message over 250 characters cannot be sent")
    void longMessageCannotBeSent() {
        Message message = new Message(RECIPIENT_1, "x".repeat(251), 0);
        assertEquals("Please enter a message of less than 250 characters.", message.SentMessage("1"));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("Message to an invalid number cannot be sent")
    void invalidRecipientCannotBeSent() {
        Message message = new Message(RECIPIENT_2, TEXT_2, 0);
        assertTrue(message.SentMessage("1").startsWith("Cell phone number is incorrectly formatted"));
        assertEquals(0, Message.returnTotalMessages());
    }

    // ---------------- Totals and display ----------------

    @Test
    @DisplayName("Total messages sent - send message 1, discard message 2")
    void totalMessagesSent() {
        Message one = new Message(RECIPIENT_1, TEXT_1, 0);
        Message two = new Message(RECIPIENT_2, TEXT_2, 1);
        one.SentMessage("1");
        two.SentMessage("2");
        assertEquals(1, Message.returnTotalMessages());
        assertEquals(1, Message.returnTotalMessagess());
    }

    @Test
    @DisplayName("printMessages returns all sent messages")
    void printMessagesReturnsAllSent() {
        assertEquals("No messages have been sent yet.", Message.printMessages());

        new Message("0012345678", RECIPIENT_1, TEXT_1, 0).SentMessage("1");
        new Message("7712345678", "+27831234567", TEXT_2, 1).SentMessage("1");

        String output = Message.printMessages();
        assertTrue(output.contains("0012345678"));
        assertTrue(output.contains("7712345678"));
        assertTrue(output.indexOf("0012345678") < output.indexOf("7712345678"));
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("Message details are shown in order: ID, hash, recipient, message")
    void detailsInRequiredOrder() {
        String output = new Message("0012345678", RECIPIENT_1, TEXT_1, 0).getMessageDetails();
        assertTrue(output.indexOf("Message ID:") < output.indexOf("Message Hash:"));
        assertTrue(output.indexOf("Message Hash:") < output.indexOf("Recipient:"));
        assertTrue(output.indexOf("Recipient:") < output.indexOf("Message:"));
    }

    // ---------------- JSON ----------------

    @Test
    @DisplayName("Messages can be stored in a JSON file")
    void messagesCanBeStoredAsJson() throws Exception {
        Path temp = Files.createTempFile("quickchat", ".json");
        try {
            Message.storeMessage(List.of(new Message("0012345678", RECIPIENT_1, "Say \"hi\" to Mike", 0)), temp);
            String json = Files.readString(temp);
            assertTrue(json.contains("\"messageID\": \"0012345678\""));
            assertTrue(json.contains("\"message\": \"Say \\\"hi\\\" to Mike\""));
            assertTrue(json.contains("\"recipient\": \"+27718693002\""));
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
