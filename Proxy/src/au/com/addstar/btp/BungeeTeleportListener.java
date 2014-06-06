package au.com.addstar.btp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeTeleportListener implements Listener {
	private BungeeTeleport plugin;
	
	public BungeeTeleportListener(BungeeTeleport p) {
		plugin = p;
	}

	@EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        // Clear tpa entry for player
    }

	@EventHandler
    public void onServerChange(final ServerConnectedEvent event) {
        // Clear tpa entry for player
    }
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event){
		if (event.getTag().equals("BTProxy") && event.getSender() instanceof Server) {
			ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
			DataInputStream input = new DataInputStream(stream);

			// Important message debugging (can be toggled live)
			if (plugin.isDebug()) {
				String data = plugin.dumpPacket(event.getData());
				plugin.DebugMsg("DEBUG received {BTProxy}: " + data);
			}

			// Handle the request
			String action = "<empty>";
			try {
				action = input.readUTF();
				if (action.equals("SendPlayerToPlayer")) {
					// Parameters: SrcPlayer, DestPlayer
					plugin.LogMsg("Processing SendPlayerToPlayer request...");
					String s = input.readUTF();
					String d = input.readUTF();
					ProxiedPlayer sp = plugin.getProxy().getPlayer(s);
					ProxiedPlayer dp = plugin.getProxy().getPlayer(d);
					if (sp != null) {
						if (dp != null) {
							// Recipient is online, commence teleport!
							plugin.TeleportPlayerToPlayer(sp, dp);
						} else {
							// Recipient player is not online
							plugin.WarnMsg("Player " + d + " is not online.");
						}
					} else {
						plugin.ErrorMsg(action + " request from unknown player " + d + "!!");
					}
				}
				else if (action.equals("SendPlayerToLocation")) {
					// Parameters: SrcPlayer, Server, World, X, Y, Z, Y, P
					String s = input.readUTF();
					ProxiedPlayer sp = plugin.getProxy().getPlayer(s);

					String server = input.readUTF();
					String world = input.readUTF();
					Double x = Double.valueOf(input.readUTF());
					Double y = Double.valueOf(input.readUTF());
					Double z = Double.valueOf(input.readUTF());
					Float yaw = Float.valueOf(input.readUTF());
					Float pitch = Float.valueOf(input.readUTF());
					
					Location loc = new Location(server, world, x, y, z, yaw, pitch);
					plugin.TeleportPlayerToLocation(sp, loc);
				}
				else {
					plugin.WarnMsg("Unknown action received: " + action);
				}
			}
			catch(IOException e) {
				plugin.ErrorMsg("Unexpected failure during message read!");
				plugin.ErrorMsg("Requested action was: " + action);
				e.printStackTrace();
			}
		}
	}
}