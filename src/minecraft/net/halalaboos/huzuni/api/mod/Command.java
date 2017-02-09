package net.halalaboos.huzuni.api.mod;

/**
 * Processes a string input and performs some action.
 * */
public interface Command {
    
	/**
     * @return An array of aliases available for the command.
     */
    String[] getAliases();

    /**
     * Gives the user help for using the command.
     */
    void giveHelp();

    /**
     * @return Description of the command.
     */
    String getDescription();

    /**
     * Handles processing the commands.
     */
    void run(String input, String[] args) throws Exception;

}
