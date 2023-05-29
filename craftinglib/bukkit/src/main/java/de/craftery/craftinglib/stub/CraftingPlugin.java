package de.craftery.craftinglib.stub;

import de.craftery.craftinglib.platform.PluginStub;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class CraftingPlugin extends JavaPlugin implements PluginStub {
    @Override
    public void onEnable() {
        this.onEntry();
    }

    @Override
    public void onEntry() {}

    // Constructors for testing
    public CraftingPlugin() {
        super();
    }
    protected CraftingPlugin(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
    }
}
