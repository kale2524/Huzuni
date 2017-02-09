package net.halalaboos.huzuni.gui.screen.account;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.mojang.authlib.exceptions.AuthenticationException;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.util.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;

public class AccountSelectionList extends GuiListExtended {

	private final List<AccountListEntry> accounts = new ArrayList<AccountListEntry>();
	
	private int selected = -1;
	
	public AccountSelectionList(Minecraft mc, int width, int height, int top, int bottom, int itemSize) {
		super(mc, width, height, top, bottom, itemSize);
		
	}

	@Override
	public AccountListEntry getListEntry(int index) {
		return accounts.get(index);
	}
	
	public AccountListEntry getSelected() {
		return hasSelected() ? this.accounts.get(selected) : null;
	}

	@Override
	protected int getSize() {
		return accounts.size();
	}

	public void setAccounts(List<String> accounts) {
		this.selected = -1;
		this.accounts.clear();
		for (String account : accounts) {
			this.accounts.add(new AccountListEntry(this, account));
		}
	}

	@Override
	protected boolean isSelected(int index) {
		return index == selected;
	}

	public void setSelected(int index) {
		this.selected = index;
	}

	public boolean hasSelected() {
		return selected >= 0 && selected < accounts.size();
	}

	public void login(final HuzuniAccounts screen) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					AccountListEntry account = getSelected();
					Session session = MinecraftUtils.loginToMinecraft(account.getUsername(), account.getPassword());
					// TODO: Fix this.
					//Minecraft.getMinecraft().setSession(session);
					Huzuni.INSTANCE.settings.setLastSession(session);
					screen.setStatus(TextFormatting.YELLOW + "Success. " + session.getUsername());
				} catch (AuthenticationException e) {
					screen.setStatus(TextFormatting.RED + "Unsuccessful. " + e.getMessage());
				}
			}
		});
	}
}
