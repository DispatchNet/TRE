package IO_operations;

public class IO {
    /**
     * @brief Constructor for IO class
     */
    public IO() {}
    
    /**
     * @brief Displays an error message
     * @param message The error message to display
     */
    public void displayError(String message) {
        System.out.println("Error: " + message);
    }
    
    /**
     * @brief Prints a generic message
     * @param message The string to print
     */
    public void print(String message) {
      System.out.print(message);
    }

    /**
     * @brief Prints a generic message and includes a newline character at the end
     * @param message The string to print
     */
    public void println(String message) {
      System.out.println(message);
    }

    /**
     * @brief Prompts the user for input
     * @param message The message to display
     * @return The user's input
     */
    public String prompt(String message) {
        System.out.println(message);
        return System.console().readLine();
    }

    /**
     * @brief Prompts the user for a password
     * @param message The message to display
     * @return The user's password
     * 
     * @throws java.io.IOException if an I/O error occurs while reading input
     */
    public String promptPassword(String message) {
        java.io.Console console = System.console();
        // use console to read password without echoing if available,
        // otherwise fall back to regular input
        if (console != null) {
            return new String(console.readPassword(message));
        }

        System.out.println(message);
        // fallback to regular input if console is not available (e.g. in IDEs)
        try {
            return new java.io.BufferedReader(
                // wrap System.in in a BufferedReader to read a line of input
                new java.io.InputStreamReader(System.in)
            ).readLine();
        } catch (java.io.IOException e) {
            return "";
        }
    }
}
