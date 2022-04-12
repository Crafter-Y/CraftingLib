package de.paradubsch.paradubschmanager.commands;

import de.paradubsch.paradubschmanager.models.PlayerData;
import de.paradubsch.paradubschmanager.util.Expect;
import de.paradubsch.paradubschmanager.util.Hibernate;
import de.paradubsch.paradubschmanager.util.MessageAdapter;
import de.paradubsch.paradubschmanager.util.lang.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrefixCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Expect.minArgs(1, args)) {
            MessageAdapter.sendPlayerError(sender, Message.Error.CMD_PLAYER_NOT_PROVIDED);
            return true;
        }

        if (!Expect.playerString(args[0])) {
            MessageAdapter.sendPlayerError(sender, Message.Error.CMD_RECEIVER_NOT_PLAYER, args[0]);
            return true;
        }

        if (!Expect.minArgs(2, args)) {
            MessageAdapter.sendPlayerError(sender, Message.Error.CMD_PREFIX_NOT_PROVIDED);
            return true;
        }

        new Thread(() -> {
            PlayerData pd = Hibernate.getPlayerData(args[0]);
            if (pd == null) {
                MessageAdapter.sendPlayerError(sender, Message.Error.CMD_PLAYER_NEVER_ONLINE, args[0]);
                return;
            }

            StringBuilder prefix = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                prefix.append(args[i]).append(" ");
            }

            MessageAdapter.setPlayerPrefix(pd, prefix.toString().trim());

            MessageAdapter.sendPlayerInfo(sender, Message.Info.CMD_PREFIX_SET, pd.getName(), ChatColor.translateAlternateColorCodes('&', prefix.toString().trim()));
        }).start();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return null;
        }
        return new ArrayList<>();
    }
}
