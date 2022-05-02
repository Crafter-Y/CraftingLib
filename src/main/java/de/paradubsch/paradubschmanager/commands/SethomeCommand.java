package de.paradubsch.paradubschmanager.commands;

import de.paradubsch.paradubschmanager.ParadubschManager;
import de.paradubsch.paradubschmanager.models.Home;
import de.paradubsch.paradubschmanager.util.Expect;
import de.paradubsch.paradubschmanager.util.Hibernate;
import de.paradubsch.paradubschmanager.util.MessageAdapter;
import de.paradubsch.paradubschmanager.util.lang.Language;
import de.paradubsch.paradubschmanager.util.lang.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SethomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Expect.playerSender(sender)) {
            MessageAdapter.sendMessage(sender, Message.Error.CMD_ONLY_FOR_PLAYERS);
            return true;
        }
        Player player = (Player) sender;


        String homeName;
        if (args.length > 0) {
            homeName = args[0];
        } else {
            homeName = "default";
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
            CompletableFuture.supplyAsync(() -> Hibernate.getPlayerData(player)).thenApplyAsync(playerData -> {
                List<Home> homes = Hibernate.getHomes(player);
                Language lang = Language.getLanguageByShortName(playerData.getLanguage());

                if (homes.stream().anyMatch(home -> home.getName().equals(homeName))) {
                    Home home = homes.stream().filter(home1 -> home1.getName().equals(homeName)).findFirst().get();
                    homes.remove(home);
                    home.setX(player.getLocation().getBlockX());
                    home.setY(player.getLocation().getBlockY());
                    home.setZ(player.getLocation().getBlockZ());
                    home.setWorld(player.getLocation().getWorld().getName());

                    homes.add(home);
                    playerData.setHomes(homes);

                    MessageAdapter.sendMessage(player, Message.Info.CMD_HOME_SET, homeName);

                    return playerData;
                }

                if (homes.size() >= playerData.getMaxHomes()) {
                    MessageAdapter.sendMessage(sender, Message.Error.CMD_SETHOME_NOT_ENOUGH_HOMES);
                    String translation = ParadubschManager.getInstance().getLanguageManager().getString(Message.Constant.BUY_BUTTON, lang);
                    String buyTranslation = ParadubschManager.getInstance().getLanguageManager().getString(Message.Constant.BUY, lang);
                    Component buyButton = Component.text(ChatColor.translateAlternateColorCodes('&', translation));
                    buyButton = buyButton.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/buyhome"));
                    buyButton = buyButton.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(buyTranslation)));
                    MessageAdapter.sendWidgetMessage(sender, Message.Info.CMD_SETHOME_BUYHOME, (TextComponent) buyButton);
                    return playerData;
                }

                Home home = new Home();
                home.setName(homeName);
                home.setX(player.getLocation().getBlockX());
                home.setY(player.getLocation().getBlockY());
                home.setZ(player.getLocation().getBlockZ());
                home.setWorld(player.getLocation().getWorld().getName());
                home.setPlayerRef(playerData);

                homes.add(home);


                playerData.setHomes(homes);

                MessageAdapter.sendMessage(player, Message.Info.CMD_HOME_SET, homeName);

                return playerData;
            }).thenAccept(Hibernate::save);
            return true;
        }

        CompletableFuture.supplyAsync(() -> Hibernate.getPlayerData(player)).thenApplyAsync(playerData -> {
            List<Home> homes = Hibernate.getHomes(player);
            Language lang = Language.getLanguageByShortName(playerData.getLanguage());

            if (homes.stream().anyMatch(home -> home.getName().equals(homeName))) {
                MessageAdapter.sendMessage(sender, Message.Error.CMD_SETHOME_ALREADY_EXISTING, homeName);
                String translation = ParadubschManager.getInstance().getLanguageManager().getString(Message.Constant.OVERRIDE_BUTTON, lang);
                String overrideTranslation = ParadubschManager.getInstance().getLanguageManager().getString(Message.Constant.OVERRIDE, lang);
                Component overrideButton = Component.text(ChatColor.translateAlternateColorCodes('&', translation));
                overrideButton = overrideButton.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/sethome " + homeName + " confirm"));
                overrideButton = overrideButton.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(overrideTranslation)));
                MessageAdapter.sendWidgetMessage(sender, Message.Info.CMD_SETHOME_OVERRIDE_EXISTING_HOME, (TextComponent) overrideButton);
                return playerData;
            }

            if (homes.size() >= playerData.getMaxHomes()) {
                MessageAdapter.sendMessage(sender, Message.Error.CMD_SETHOME_NOT_ENOUGH_HOMES);
                String translation = ParadubschManager.getInstance().getLanguageManager().getString(Message.Constant.BUY_BUTTON, lang);
                String buyTranslation = ParadubschManager.getInstance().getLanguageManager().getString(Message.Constant.BUY, lang);
                Component buyButton = Component.text(ChatColor.translateAlternateColorCodes('&', translation));
                buyButton = buyButton.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/buyhome"));
                buyButton = buyButton.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(buyTranslation)));
                MessageAdapter.sendWidgetMessage(sender, Message.Info.CMD_SETHOME_BUYHOME, (TextComponent) buyButton);
                return playerData;
            }

            Home home = new Home();
            home.setName(homeName);
            home.setX(player.getLocation().getBlockX());
            home.setY(player.getLocation().getBlockY());
            home.setZ(player.getLocation().getBlockZ());
            home.setWorld(player.getLocation().getWorld().getName());
            home.setPlayerRef(playerData);

            homes.add(home);


            playerData.setHomes(homes);

            MessageAdapter.sendMessage(player, Message.Info.CMD_HOME_SET, homeName);

            return playerData;
        }).thenAccept(Hibernate::save);


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
