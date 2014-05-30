package au.com.addstar.bungeeteleport;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
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
		if (event.getTag().equals("BungeeTeleport") && event.getSender() instanceof Server) {
			ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
			DataInputStream input = new DataInputStream(stream);

			// Important message debugging (can be toggled live)
			if (plugin.isDebug()) {
				String daction = "<empty>";
				String dparams = "";
				
				try {
					ByteArrayInputStream ds = new ByteArrayInputStream(event.getData());
					DataInputStream di = new DataInputStream(ds);
					// Read upto 20 parameters from the stream and load them into the string list
					
					daction = di.readUTF();
					for (int x = 0; x < 20; x++) {
						if (dparams.isEmpty()) {
							dparams = di.readUTF();
						} else { 
							dparams = dparams + ", " + di.readUTF();
						}
					}
					plugin.DebugMsg("Received message [" + daction + "] " + dparams);
				}
				catch(EOFException e) {
					plugin.DebugMsg("Received message [" + daction + "] " + dparams);
				}
				catch(IOException e) {
					plugin.ErrorMsg("Error during debug output!");
					e.printStackTrace();
				}
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
							plugin.LogMsg("Player " + dp.getName() + " is online, teleport would occur here.");
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
					plugin.LogMsg("Processing SendPlayerToLocation request...");
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