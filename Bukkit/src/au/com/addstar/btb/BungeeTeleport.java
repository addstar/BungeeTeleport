package au.com.addstar.btb;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
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

		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeTeleport", this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeTeleport");
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

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		DataInputStream input = new DataInputStream(stream);
		try	{
			if (channel.equals("BungeeTeleport")) {
				String action = input.readUTF();
				if (action.equals("SendPlayerToPlayer")) {
					DebugMsg("Message received: [SendPlayerToPlayer] " + dumpPacket(bytes));
				}
				else if (action.equals("SendPlayerToLocation")) {
					DebugMsg("Message received: [SendPlayerToLocation] " + dumpPacket(bytes));
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