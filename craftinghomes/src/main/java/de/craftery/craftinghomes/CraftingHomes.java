package de.craftery.craftinghomes;

import de.craftery.craftinglib.stub.CraftingPlugin;
import de.craftery.craftinglib.annotation.annotations.Entry;

@Entry(name = "CraftingHomes")
public class CraftingHomes extends CraftingPlugin {
    @Override
    public void onEntry() {
        System.out.println("Hello from CraftingHomes!");
    }
}
