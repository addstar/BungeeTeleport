package au.com.addstar.btb;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BungeeTeleportCommand implements CommandExecutor {
	BungeeTeleport plugin;
	
	public BungeeTeleportCommand(BungeeTeleport p) {
		plugin = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("Usage: /btb <sub-command>");
			sender.sendMessage("Available commands: debug");
		} else {
			if (args[0].equalsIgnoreCase("debug")) {
				plugin.setDebug(!plugin.isDebug());
				sender.sendMessage("BungeeTeleportBukkit debugging is now: " + ((plugin.isDebug()) ? "ENABLED" : "DISABLED"));
			} else {
				sender.sendMessage("ERROR: Available sub-commands: debug");
			}
		}
		return true;
	}
}
