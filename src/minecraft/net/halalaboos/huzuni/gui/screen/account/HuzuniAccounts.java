package net.halalaboos.huzuni.gui.screen.account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.util.FileUtils;
import net.halalaboos.huzuni.gui.screen.HuzuniScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.text.TextFormatting;

public class HuzuniAccounts extends HuzuniScreen implements GuiYesNoCallback {

	protected final File accountsFile = new File(Huzuni.INSTANCE.getSaveFolder(), "Accounts.txt");

	protected final GuiScreen parent;
	
	protected AccountSelectionList selectionList;
	
	protected List<String> totalAccounts;
		
	protected GuiButton use, remove, lastLogin;
	
	protected String status = "";

	public HuzuniAccounts(GuiScreen parent) {
		super();
		this.parent = parent;
		totalAccounts = this.readAccounts();
		status = Minecraft.getMinecraft().getSession().getUsername();
	}
	
	@Override
	public void initGui() {
		buttonList.clear();
		this.selectionList = new AccountSelectionList(this.mc, this.width, this.height, 32, this.height - 64, 36);
		this.selectionList.setAccounts(totalAccounts);
    	
        buttonList.add(new GuiButton(2, this.width / 2 - 154, this.height - 28, 152, 20, "Load File.."));
        buttonList.add(new GuiButton(3, this.width / 2 + 2, this.height - 28, 152, 20, "Done"));
        buttonList.add(use = new GuiButton(1, this.width / 2 + 2, this.height - 52, 152, 20, "Login"));
        buttonList.add(lastLogin = new GuiButton(0, this.width / 2 - 154, this.height - 52, 152, 20, "Last Login"));
        lastLogin.enabled = huzuni.settings.getLastSession() != null;

        use.enabled = selectionList.hasSelected();
        remove = new GuiButton(4, width / 2 + 4, height - 52, 152, 20, "Remove");
        //buttonList.add(remove = new GuiButton(13, width / 2 + 4, height - 52, 152, 20, "Remove"));
        remove.enabled = selectionList.hasSelected();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (!button.enabled)
			return;
		switch (button.id) {
		case 0:
			if (huzuni.settings.getLastSession() != null) {
				// TODO: Fix this.
				//mc.setSession(huzuni.settings.getLastSession());
				setStatus(TextFormatting.YELLOW + mc.getSession().getUsername());
			}
			break;
		case 1:
			if (selectionList.hasSelected()) {
				selectionList.login(this);
			}
			break;
		case 2:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFileChooser fileChooser = new JFileChooser();
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new FileReader(
									selectedFile));
							for (String line; (line = reader.readLine()) != null;) {
								if (line.contains(":")) {
									if (!totalAccounts.contains(line))
										totalAccounts.add(line);
								}
							}
							selectionList.setAccounts(totalAccounts);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (reader != null)
								try {
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					}
				}
			});
			break;
		case 3:
			mc.displayGuiScreen(parent);
			break;
		case 4:
			mc.displayGuiScreen(new GuiYesNo(this, "Delete account '" + selectionList.getSelected().getUsername() + "'", "Are you sure?", "Delete", "Back", 0));
			break;
		default:
			break;
		}
	}
	
	@Override
    public void confirmClicked(boolean confirm, int id) {
        if (confirm && id == 0) {
        	totalAccounts.remove(selectionList.getSelected().getAccount());
        	selectionList.setAccounts(totalAccounts);
           	FileUtils.writeFile(accountsFile, totalAccounts);
        }
        mc.displayGuiScreen(this);
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		selectionList.drawScreen(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(fontRenderer, status, width / 2, 10, 0xFFFFFFFF);
		use.enabled = selectionList.hasSelected();
		remove.enabled = selectionList.hasSelected();
        lastLogin.enabled = huzuni.settings.getLastSession() != null;
	}
	
	@Override
	public void handleMouseInput() throws IOException {
        super.handleMouseInput();
		selectionList.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		selectionList.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		selectionList.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		FileUtils.writeFile(accountsFile, totalAccounts);
	}

	public List<String> readAccounts() {
        if (accountsFile.exists()) {
            return FileUtils.readFile(accountsFile);
        }
        return new ArrayList<String>();
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
