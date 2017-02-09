package net.halalaboos.huzuni.api.mod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.util.text.TextFormatting;

/**
 * Manages all {@link Command}s and allows easy access.
 * */
public final class CommandManager {

	// Pattern used to split the arguments into separate strings.
	private final Pattern pattern = Pattern.compile("([^\"']\\S*|\".+?\"|'.+?')\\s*");
	
	private final Huzuni huzuni;
	
	private final List<Command> commands = new ArrayList<Command>();
	
	private String commandPrefix = ".";
	
	public CommandManager(Huzuni huzuni) {
		this.huzuni = huzuni;
	}
	
	public void addCommand(Command command) {
		commands.add(command);
	}
	
	public List<Command> getCommands() {
		return commands;
	}

	public String getCommandPrefix() {
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}

	/**
	 * Attempts to find a {@link Command} a process it using the input.
	 * @param input The input used to find the {@link Command} and the input for the {@link Command}. 
	 * */
	public boolean processCommand(String input) {
		String commandName = input.contains(" ") ? input.split(" ")[0] : input;
		for (Command command : getCommands()) {
			for (String alias : command.getAliases()) {
				if (alias.toLowerCase().equals(commandName.toLowerCase())) {
					if (input.contains(" ")) {
						if (input.split(" ")[1].equalsIgnoreCase("aliases")) {
							listAliases(command);
							return true;
						}
					}
					tryCommand(command, input);
					return true;
				}
			}
		}
		huzuni.addChatMessage("'" + input + "' is not recognized as a command.");
		return false;
	}

	/**
     * Uses a pattern to match the arguments within the input provided. Splitting each argument into it's own string.
     * */
	private String[] getArguments(String input) {
		Matcher matcher = pattern.matcher(input);
		List<String> tempList = new ArrayList<String>();
		while (matcher.find())
			tempList.add(matcher.group(1).replaceAll("\"", "").replaceAll("'", ""));
		return tempList.toArray(new String[tempList.size()]);
	}
	
	/**
	 * Searches for {@link Method}s with the {@link CommandPointer} annotation and creates a command that invokes the function through reflection.
	 * */
	public void generateCommands(Object source) { 
		for (Method method : source.getClass().getMethods()) {
			AnnotationCommand.createCommand(this, source, method);
		}
	}

	/**
	 * Lists every alias of the given {@link Command} to the user.
	 * @param command The {@link Command} to list aliases from.
	 * */
	private void listAliases(Command command) {
		String aliasList = "Available aliases: ";
		String[] aliases = command.getAliases();
		for (int i = 0; i < aliases.length; i++) {
			String alias = aliases[i];
			aliasList+= TextFormatting.GOLD + alias + TextFormatting.GRAY + (i != aliases.length ? ", " : "");
		}
		huzuni.addChatMessage(aliasList);
	}

	/**
	 * Attempts to run a {@link Command} with the given input.
	 * @param command The {@link Command} to be run.
	 * @param input The input for the {@link Command}.
	 * */
	public void tryCommand(Command command, String input) {
		try {
			String[] args = input.contains(" ") ? getArguments(input.substring(input.indexOf(" ") + 1)) : null;
			command.run(input, args);
		} catch (Exception e) {
			huzuni.addChatMessage("Improper usage of command '" + TextFormatting.RED + command.getAliases()[0] + TextFormatting.GRAY + "'!");
			command.giveHelp();
			huzuni.addNotification(NotificationType.ERROR, "Command Manager", 5000, "Improper usage of command '" + TextFormatting.RED + command.getAliases()[0] + TextFormatting.GRAY + "'!");
		}
	}
}
