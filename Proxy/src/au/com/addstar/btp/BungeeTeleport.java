package au.com.addstar.btp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeTeleport extends Plugin {
	public static BungeeTeleport plugin;
	public static ProxyServer proxy;
	private boolean Debug = false;

	@Override
	public void onEnable() {
		proxy = ProxyServer.getInstance();
		plugin = this;
		getProxy().getPluginManager().registerListener(this, new BungeeTeleportListener(this));
		getProxy().registerChannel("BTProxy");
		getProxy().getPluginManager().registerCommand(this, new BungeeTeleportCommand(this));
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

	public boolean TeleportPlayerToPlayer(ProxiedPlayer src, ProxiedPlayer dst) {
		if (src == null) {
			ErrorMsg("TeleportPlayerToPlayer: src is null!");
			return false;
		}
		
		if (dst == null) {
			ErrorMsg("TeleportPlayerToPlayer: dst is null!");
			return false;
		}

		if (src.getName() == dst.getName()) {
			ErrorMsg("TeleportPlayerToPlayer: cannot teleport to self!");
			return false;
		}

		// Send player to the right server (if necessary)
        if (!src.getServer().equals(dst.getServer())) {
            src.connect(dst.getServer().getInfo());
            DebugMsg("DEBUG connect " + src.getName() + " to server " + dst.getServer().getInfo().getName());
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            out.writeUTF("TeleportToLocation");
            out.writeUTF(src.getName());
            out.writeUTF(dst.getName());
            getProxy().getScheduler().runAsync(this, new SendPluginMessage("BTBukkit", dst.getServer().getInfo(), bytes));
            if (isDebug()) {
            	String data = dumpPacket(bytes.toByteArray());
            	DebugMsg("DEBUG sent {BTBukkit}: " + data);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}

	public boolean TeleportPlayerToLocation(ProxiedPlayer src, Location loc) {
		if (src == null) {
			ErrorMsg("TeleportPlayerToLocation: src is null!");
			return false;
		}
		
		// Send player to the right server (if necessary)
        if (!src.getServer().equals(loc.getServer())) {
            DebugMsg("DEBUG connect " + src.getName() + " to server " + loc.getServer().getName());
            src.connect(loc.getServer());
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            out.writeUTF("TeleportToLocation");
            out.writeUTF(src.getName());
            out.writeUTF(loc.getWorld());
            out.writeUTF(Double.toString(loc.getX()));
            out.writeUTF(Double.toString(loc.getY()));
            out.writeUTF(Double.toString(loc.getZ()));
            out.writeUTF(Float.toString(loc.getYaw()));
            out.writeUTF(Float.toString(loc.getPitch()));

            getProxy().getScheduler().runAsync(this, new SendPluginMessage("BTBukkit", loc.getServer(), bytes));
            if (isDebug()) {
            	String data = dumpPacket(bytes.toByteArray());
            	DebugMsg("DEBUG sent {BTBukkit}: " + data);
            }
        }
        catch (IOException e) {
        	ErrorMsg("TeleportPlayerToLocation failed!");
        	e.printStackTrace();
            return false;
        }
		return true;
	}
}
