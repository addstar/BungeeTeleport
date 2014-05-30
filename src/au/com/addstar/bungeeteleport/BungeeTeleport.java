package au.com.addstar.bungeeteleport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeTeleport extends Plugin {
	private boolean Debug = false;
	
	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, new BungeeTeleportListener(this));
		getProxy().registerChannel("BungeeTeleport");
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
		if ((src == null) || (dst == null)) {
			ErrorMsg("TeleportPlayerToPlayer: src or dst player is null!");
			return false;
		}

		// Send player to the right server
        if (!src.getServer().equals(dst.getServer())) {
            src.connect(dst.getServer().getInfo());
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            out.writeUTF("TeleportToLocation");
            out.writeUTF(src.getName());
            out.writeUTF(dst.getName());
            getProxy().getScheduler().runAsync(this, new SendPluginMessage("CHTeleport", dst.getServer().getInfo(), bytes));
            if (isDebug()) {
            	String data = dumpPacket(bytes.toByteArray());
            	DebugMsg("DEBUG sent {CHTeleport}: " + data);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}
}
