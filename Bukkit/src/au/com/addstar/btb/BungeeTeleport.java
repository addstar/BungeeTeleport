package au.com.addstar.btb;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeTeleport extends JavaPlugin implements PluginMessageListener, Listener {
	public static BungeeTeleport plugin;
	private boolean Debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;

		getCommand("bungeeteleportbukkit").setExecutor(new BungeeTeleportCommand(this));
		getCommand("btb").setAliases(Arrays.asList("btp"));

		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BTBukkit", this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BTProxy");
	}

	public boolean isDebug() {
		return Debug;
	}

	public void setDebug(boolean debug) {
		Debug = debug;
	}

	public void LogMsg(String msg) {
		getLogger().info(msg);
	}

	public void WarnMsg(String msg) {
		getLogger().warning(msg);
	}

	public void ErrorMsg(String msg) {
		getLogger().severe(msg);
	}

	public void DebugMsg(String msg) {
		if (isDebug()) {
			getLogger().info(msg);
		}
	}

	public String dumpPacket(byte[] bytes) {
		String data = "";
		try {
			ByteArrayInputStream ds = new ByteArrayInputStream(bytes);
			DataInputStream di = new DataInputStream(ds);
			// Read upto 20 parameters from the stream and load them into the string list
			for (int x = 0; x < 20; x++) {
				if (data.isEmpty()) {
					data = "\"" + di.readUTF() + "\"";
				} else {
					data = data + ", \"" + di.readUTF() + "\"";
				}
			}
		}
		catch(EOFException e) {
			return data;
		}
		catch(IOException e) {
			ErrorMsg("Error during debug output!");
			e.printStackTrace();
		}
		return data;
	}

	public Location getLocationFromInput(DataInputStream input) {
		String world;
		Double x, y, z;
		Float yaw, pitch;
		try {
			world = input.readUTF();
			x = Double.valueOf(input.readUTF());
			y = Double.valueOf(input.readUTF());
			z = Double.valueOf(input.readUTF());
			yaw = Float.valueOf(input.readUTF());
			pitch = Float.valueOf(input.readUTF());
		}
		catch (IOException e) {
			ErrorMsg("Unable to convert location string to Location object!");
			e.printStackTrace();
			return null;
		}

		World w = getServer().getWorld(world);
		if (w == null) {
			ErrorMsg("World \"" + world + "\" does not exist!");
			return null;
		}

		Location loc = null;
		try {
			loc = new Location(w, x, y, z, yaw, pitch);
		}
		catch (Exception e) {
			ErrorMsg("Failed to construct Location object!");
			e.printStackTrace();
		}
		
		return loc;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		DataInputStream input = new DataInputStream(stream);
		try	{
			if (channel.equals("BTBukkit")) {
				String action = input.readUTF();
				
				if (action.equals("TeleportToPlayer")) {
					DebugMsg("Message received: [TeleportToPlayer] " + dumpPacket(bytes));
					
					// Get the destination player location
					String dpname = input.readUTF();
					Player dp = getServer().getPlayerExact(dpname);
					if (dp == null) { 
						plugin.WarnMsg("Player \"" + dpname + "\" is not online!");
						return;
					}
					if (player.isOnline()) {
						Location loc = dp.getLocation();
						if (!player.teleport(loc)) {
							plugin.WarnMsg("Unable to teleport \"" + player.getName() + "\" to location!");
						}
					} else {
						DebugMsg("Player not online - Storing teleport");
					}
				}
				else if (action.equals("TeleportToLocation")) {
					DebugMsg("Message received: [TeleportToLocation] " + dumpPacket(bytes));
					if (player.isOnline()) {
						Location loc = getLocationFromInput(input);
						if (!player.teleport(loc)) {
							plugin.WarnMsg("Unable to teleport \"" + player.getName() + "\" to location!");
						}
					} else {
						DebugMsg("Player not online - Storing teleport");
					}
				}
				else {
					DebugMsg("Unknown message: [" + action + "] " + dumpPacket(bytes));
				}
			}
		}
		catch (Exception e) {
			
		}
	}
}
