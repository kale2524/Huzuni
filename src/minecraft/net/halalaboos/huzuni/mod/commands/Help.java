package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.mod.Command;
import net.halalaboos.huzuni.api.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.List;


public final class Help extends BasicCommand {
	
	private final int COMMAND_PER_PAGE = 4;
	
	public Help() {
		super("help", "Gives you all of the help commands.");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		if (args != null) {
			if (StringUtils.isInteger(args[0])) {
				listHelp(Integer.parseInt(args[0]));
			} else {
				Command command = findCommand(args[0]);
				if (command != null) {
					huzuni.addChatMessage(String.format("%s--- %sShowing help for command '%s%s%s' %s---", TextFormatting.GOLD, TextFormatting.GRAY, TextFormatting.GOLD, args[0], TextFormatting.GRAY, TextFormatting.GOLD));
					String aliases = "";
					for(String s : command.getAliases()) { aliases += s + ", "; } if (aliases.length() > 0) aliases = aliases.substring(0, aliases.length() - 2);
					huzuni.addChatMessage(String.format("%sAliases: %s%s", TextFormatting.GOLD, TextFormatting.GRAY, aliases));
					huzuni.addChatMessage(String.format("%sDescription: %s%s", TextFormatting.GOLD, TextFormatting.GRAY, command.getDescription()));
					huzuni.addChatMessage(String.format("%sHelp:", TextFormatting.GOLD));
					command.giveHelp();
				} else
					huzuni.addChatMessage("Command not found!");
			}
		} else {
			listHelp(1);
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private Command findCommand(String string) {
		for (Command command : huzuni.commandManager.getCommands()) {
			for (String alias : command.getAliases()) {
				if (string.equalsIgnoreCase(alias)) {
					return command;
				}
			}
		}
		return null;
	}

	/**
	 * @return Lists all commands for the page.
	 * */
	public void listHelp(int wantedPage) {
		int pages = getPages();
		List<Command> commandsOnPage = getCommandsOnPage(wantedPage);
		if (commandsOnPage.isEmpty() || wantedPage <= 0) {
			huzuni.addChatMessage(String.format("%s'%s%d%s' is an invalid page!", TextFormatting.GRAY, TextFormatting.GOLD, wantedPage, TextFormatting.GRAY));
			return;
		}
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(getPageMessage(wantedPage, pages));
		for (Command command : commandsOnPage)
			huzuni.addChatMessage(String.format("%s%s%s - %s", TextFormatting.GOLD, command.getAliases()[0], TextFormatting.GRAY, command.getDescription()));
	}

	/**
	 * @return All commands for the page.
	 * */
	private List<Command> getCommandsOnPage(int page) {
		List<Command> tempList = new ArrayList<Command>();
		int pageCount = 1, commandCount = 0;

		for (int i = 0; i < huzuni.commandManager.getCommands().size(); i++) {
			Command command = huzuni.commandManager.getCommands().get(i);
			if (command != this) {
				if (commandCount >= COMMAND_PER_PAGE) {
					pageCount++;
					commandCount = 0;
				}
				if (pageCount == page)
					tempList.add(command);
				commandCount++;
			}
		}
		return tempList;
	}

	/**
	 * @return Amount of pages of commands we have.
	 * */
	private int getPages() {
		boolean isDividedEvenly = false;
		return MathHelper.ceil((float) (huzuni.commandManager.getCommands().size() - 1) / (float) COMMAND_PER_PAGE - (isDividedEvenly ? 1 : 0));
	}

	private TextComponentString getPageMessage(int wantedPage, int pages) {
		String text = String.format("%s--- %sShowing help page %d of %d (%shelp <page>)%s ---", TextFormatting.GOLD, TextFormatting.GRAY, wantedPage, pages, huzuni.commandManager.getCommandPrefix(), TextFormatting.GOLD);
		TextComponentString output = new TextComponentString(text);
		int nextPage = wantedPage == getPages() ? 1 : wantedPage + 1;
		output.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to go to the next page!")));
		output.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%shelp %d", huzuni.commandManager.getCommandPrefix(), nextPage)));
		return output;
	}
}
