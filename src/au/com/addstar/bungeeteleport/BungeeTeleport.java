package au.com.addstar.bungeeteleport;

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
		getLogger().info("[BungeeTeleport] " + msg);
	}

	public void WarnMsg(String msg) {
		getLogger().warning("[BungeeTeleport] " + msg);
	}

	public void ErrorMsg(String msg) {
		getLogger().severe("[BungeeTeleport] " + msg);
	}

	public void DebugMsg(String msg) {
		if (isDebug()) {
			getLogger().info("[BungeeTeleport] " + msg);
		}
	}
}
