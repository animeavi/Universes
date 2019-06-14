package net.whispwriting.universes.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.Utils.PlayersWhoCanConfirm;
import net.whispwriting.universes.files.WorldListFile;
import net.whispwriting.universes.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class ConfirmCommand implements CommandExecutor {

    private Universes plugin;
    private WorldListFile worldList;
    private WorldSettingsFile worldSettings;

    public ConfirmCommand(Universes pl, WorldListFile wl, WorldSettingsFile ws){
        plugin = pl;
        worldList = wl;
        worldSettings = ws;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.delete")){
            sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }
        for (Object o : plugin.players){
            PlayersWhoCanConfirm p = (PlayersWhoCanConfirm) o;
            if (p.getSender() == sender){
                sender.sendMessage(ChatColor.GREEN + "Deleting world.");
                String worldName = p.getWorld();
                World world = Bukkit.getWorld(worldName);
                world.getEntities().clear();
                Bukkit.getServer().unloadWorld(world, true);
                File file = new File(Bukkit.getWorldContainer() + "/"+worldName);
                deleteFolderContents(file);
                file.delete();
                List<String> worlds = worldList.get().getStringList("worlds");
                worlds.remove(worldName);
                worldList.get().set("worlds", worlds);
                worldList.save();
                worldList.reload();
                worldSettings.get().set("worlds."+worldName, null);
                worldSettings.save();
                worldSettings.reload();
                sender.sendMessage(ChatColor.GREEN + "World deleted.");
                plugin.players.remove(p);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "You have nothing to confirm.");
        return true;
    }

    private void deleteFolderContents(File directory){
        String[] files = directory.list();
        for (String f : files){
            File current = new File(directory.getPath(), f);
            if (current.isDirectory()){
                deleteFolderContents(current);
                current.delete();
            }else{
                current.delete();
            }
        }
    }
}
