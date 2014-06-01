package au.com.addstar.bungeeteleport;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class BungeeTeleportCommand extends Command {
	BungeeTeleport plugin;
	
	public BungeeTeleportCommand(BungeeTeleport p) {
		super("bungeeteleport", "bungeeteleport.admin", new String[] {"bt"});
		plugin = p;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("Usage: /bungeeteleport <sub-command>"));
			sender.sendMessage(new TextComponent("Available commands: debug"));
		} else {
			if (args[0].equalsIgnoreCase("debug")) {
				plugin.setDebug(!plugin.isDebug());
				sender.sendMessage(new TextComponent("BungeeTeleport debugging is now: " + ((plugin.isDebug()) ? "ENABLED" : "DISABLED")));
			} else {
				sender.sendMessage(new TextComponent("ERROR: Available sub-commands: debug"));
			}
		}
	}
	
}
