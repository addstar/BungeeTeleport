package au.com.addstar.bungeeteleport;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
				List<String> dparams = new ArrayList<String>();
				
				try {
					ByteArrayInputStream ds = new ByteArrayInputStream(event.getData());
					DataInputStream di = new DataInputStream(ds);
					// Read upto 20 parameters from the stream and load them into the string list
					
					for (int x = 0; x < 20; x++) {
						dparams.add(di.readUTF());
					}
				}
				catch(EOFException e) {
					String p = StringUtils.join(dparams, ", ");
					System.out.println("BungeeTeleport message [" + daction + "] " + p);
				}
				catch(IOException e) {
					System.out.println("Error during debug output!");
					e.printStackTrace();
				}
			}

			// Handle the request
			try {
				String action = input.readUTF();
				if (action.equals("SendPlayerToPlayer")) {
					// Parameters: SrcPlayer, DestPlayer
					String s = input.readUTF();
					String d = input.readUTF();
					ProxiedPlayer sp = plugin.getProxy().getPlayer(s);
					ProxiedPlayer dp = plugin.getProxy().getPlayer(d);
					if (sp != null) {
						if (dp != null) {
							
						} else {
							// Recipient player is not online
						}
					} else {
						System.out.println("ERROR: " + action + " request from unknown player " + d + "!!");
					}
				}
				else if (action.equals("SendPlayerToLocation")) {
					// Parameters: SrcPlayer, Server, World, X, Y, Z, Y, P 
				}
				else {
					System.out.println("ERROR: Unknown action received: " + action);
				}
			}
			catch(IOException e) {
				System.out.println("ERROR: Unexpected failure during message read!");
				e.printStackTrace();
			}
		}
	}
}