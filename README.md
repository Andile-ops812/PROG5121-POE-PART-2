# PROG5121 PoE — Part 2: QuickChat Sending Messages

This project extends the Part 1 registration/login application with the Part 2 QuickChat messaging menu.

## Implemented requirements
- Users must register and log in successfully before accessing the messaging menu.
- Displays `Welcome to QuickChat.`
- Numeric menu: Send Messages, Show recently sent messages (`Coming Soon.`), and Quit.
- Runs until the user chooses Quit.
- Asks for the number of messages at startup and limits completed message actions to that number.
- Creates a ten-digit message ID, validates the recipient and message length, creates the uppercase message hash, and displays message details in the required order.
- Supports Send, Disregard, and Store actions. Stored messages are written to `messages.json`.
- JUnit 5 unit tests cover IDs, recipient format, message length, hash, action results, message details, and JSON storage.
- GitHub Actions workflow runs Maven tests on pushes and pull requests.

## Requirements / assumptions
- Java 17+ and Maven are required. No third-party libraries are needed at runtime (JSON is written by the project itself); JUnit 5 is used for tests only.
- A recipient number must start with `+` followed by an international code and number (8-15 digits in total), e.g. `+27718693002`. Numbers such as `08575975889` are rejected.
- The message hash format is `first two ID digits:message number:first word + last word`, in uppercase. The message number starts at zero to match the supplied expected hash `00:0:HITONIGHT`.
- The recently-sent menu item intentionally prints `Coming Soon.` as specified.
- "Disregard" asks the user to press 0 to delete the message and does not increase the sent count. "Store" writes the message to `messages.json` and does not increase the sent count.

## Open in NetBeans
1. Extract the ZIP file.
2. In NetBeans, choose **File -> Open Project** and select the `PoE_Part2` folder (the folder containing `pom.xml`).
3. Right-click the **PoE_Part2** project (not PoE_Part1) and choose **Clean and Build**, then **Run**. The main class is set in `pom.xml` (`com.prog5121.poe.Main`).
4. Run tests with **Test Project** or `mvn test`.

## GitHub / feature branch
Create a feature branch before changing Part 2, for example:
`git checkout -b sending-messages`

Commit changes in small, genuine stages and push them to your repository. Do not claim commits or CI runs until you have actually made/pushed them. GitHub Actions runs tests automatically after the workflow is pushed.

## Before submission
- Run all tests in your own NetBeans/Maven environment.
- Confirm the rubric's exact expected wording and recipient-number rule with your lecturer if unclear.
- Record your own demonstration video, explaining the code and showing successful and unsuccessful test cases.
