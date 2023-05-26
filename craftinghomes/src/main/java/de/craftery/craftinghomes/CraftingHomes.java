package de.craftery.craftinghomes;

import de.craftery.craftinglib.CraftingPlugin;
import de.craftery.craftinglib.annotation.annotations.Entry;

@Entry(name = "CraftingHomes")
public class CraftingHomes extends CraftingPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        System.out.println("Hello World!");
    }
}
