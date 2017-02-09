package net.halalaboos.huzuni.api.plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Level;

import net.halalaboos.huzuni.Huzuni;

/**
 * Manager class provided to create, load, save, and provide easy access to {@link Plugin}s within the application.
 * */
public final class PluginManager {
	
	private File pluginFolder;
	
	private final List<Plugin> plugins = new ArrayList<Plugin>();
	
	private final List<PluginData> pluginDatas = new ArrayList<PluginData>();

	public PluginManager() {
		
	}
	
	/**
	 * Attempts to create and load the {@link Plugin}s from the plugin directory.
	 * */
	public void init() {
		// Obvious folder creation
		if (!pluginFolder.exists()) {
			pluginFolder.mkdirs();
			return;
		}
		// Iterate through every file found within the folder
		File[] files = pluginFolder.listFiles();
		for (File file : files) {
			// Check if the file is actually a file and not a folder. Note that I am not checking the file extension, somewhat of an oversight of mine
			if (file != null && !file.isDirectory()) {
				try {
					// This class is what is used to load the plugin information, including it's classpath.
					PluginData pluginData = null;
					// Create a JarFile of the file and enumerate through it's entries
					@SuppressWarnings("resource")
					JarFile jarFile = new JarFile(file);
					Enumeration<JarEntry> entries = jarFile.entries();
					for (JarEntry entry = null; entries.hasMoreElements();) {
						entry = entries.nextElement();
						// Again, check if the file is valid
						if (entry != null && !entry.isDirectory()) {
							// Check the file's name
							if (entry.getName().equalsIgnoreCase("plugin.json")) {
								// Attempt to read the data
								pluginData = new PluginData(new InputStreamReader(jarFile.getInputStream(entry)), pluginFolder);
								pluginDatas.add(pluginData);
								break;
							}
						}
					}
					// If the data was not null, we will have a PluginData object can be used within the constructor of the plugin we are now going to load into the runtime environment.
					if (pluginData != null) {
						if (pluginData.hasLoaded()) {
							if (pluginData.isCurrentVersion()) {
								Plugin plugin = loadFromJar(file, pluginData.getClassPath(), pluginData);
								if (plugin != null) {
									Huzuni.LOGGER.log(Level.INFO, String.format("%s version '%s' successfully loaded!", pluginData.getName(), pluginData.getVersion()));
									plugins.add(plugin);
								}
							} else {
								Huzuni.LOGGER.log(Level.ERROR, String.format("Unable to load %s! It is made for Minecraft version %s! Currently running %s", pluginData.getName(), pluginData.getMinecraftVersion(), Huzuni.MCVERSION));
							}
						} else {
							Huzuni.LOGGER.log(Level.ERROR, String.format("'%s' could not be loaded as a plugin.", file.getAbsolutePath()));
						}
					}
				} catch (Exception e) {
					Huzuni.LOGGER.log(Level.ERROR, "Reading plugin data: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		// After the plugins have been instantiated, they are initialized and loaded
		for (Plugin plugin : plugins) {
			try {
				plugin.init();
				plugin.load();
			} catch (Exception e) {
				Huzuni.LOGGER.log(Level.ERROR, "Unable to load plugin file: " + e.getMessage());
			}
		}
		
		for (Plugin plugin : plugins) {
			try {
				plugin.loadPlugin();
			} catch (Exception e) {
				Huzuni.LOGGER.log(Level.ERROR, "Unable to load plugin: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Saves all {@link Plugin}s.
	 * */
	public void save() {
		for (Plugin plugin : plugins) {
			try {
				plugin.save();
			} catch (Exception e) {
				Huzuni.LOGGER.log(Level.ERROR, "Unable to save plugin: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Attempts to load the {@link Plugin} from the {@link File} provided.
	 * @param file The file to be loaded from.
	 * @param classPath The class-path to the {@link Plugin} we are attempting to load.
	 * @param pluginData The {@link PluginData} provided for the {@link Plugin}.
	 * */
	private Plugin loadFromJar(File file, String classPath, PluginData pluginData) {
		try {
			// Create a ClassLoader object to load the file
			ClassLoader classLoader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() }, PluginManager.class.getClassLoader());
			// Grab the Constructor with the two parameters specified
			Constructor<?> constructor = classLoader.loadClass(classPath).getConstructor(Huzuni.class, PluginData.class);
			// Create an instance of the plugin
			return (Plugin) constructor.newInstance(Huzuni.INSTANCE, pluginData);
		} catch (Exception e) {
			Huzuni.LOGGER.log(Level.FATAL, "Unable to load plugin class: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * @return The {@link Plugin} loaded which is assignable from the {@code Class} provided. Returns null if the {@code Class} has not been loaded.
	 * @param clazz The {@link Plugin} class to be retrieved.
	 * */
	public Plugin getPlugin(Class<? extends Plugin> clazz) {
		for (Plugin plugin : plugins)
			if (plugin.getClass().isAssignableFrom(clazz))
				return plugin;
		return null;
	}

	/**
	 * @return The {@link Plugin} associated with the given {@link PluginData}.
	 * */
	public Plugin getPlugin(PluginData pluginData) {
		for (Plugin plugin : plugins) {
			if (plugin.getPluginData() == pluginData)
				return plugin;
		}
		return null;
	}
	
	public File getPluginFolder() {
		return pluginFolder;
	}

	public void setPluginFolder(File pluginFolder) {
		this.pluginFolder = pluginFolder;
	}
	
	public List<Plugin> getPlugins() {
		return plugins;
	}

	public List<PluginData> getPluginDatas() {
		return pluginDatas;
	}
}
