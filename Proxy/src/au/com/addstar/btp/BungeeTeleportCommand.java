package au.com.addstar.btp;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class BungeeTeleportCommand extends Command {
	BungeeTeleport plugin;
	
	public BungeeTeleportCommand(BungeeTeleport p) {
		super("bungeeteleportproxy", "bungeeteleport.admin", new String[] {"btp"});
		plugin = p;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("Usage: /btp <sub-command>"));
			sender.sendMessage(new TextComponent("Available commands: debug"));
		} else {
			if (args[0].equalsIgnoreCase("debug")) {
				plugin.setDebug(!plugin.isDebug());
				sender.sendMessage(new TextComponent("BungeeTeleportProxy debugging is now: " + ((plugin.isDebug()) ? "ENABLED" : "DISABLED")));
			} else {
				sender.sendMessage(new TextComponent("ERROR: Available sub-commands: debug"));
			}
		}
	}
	
}
