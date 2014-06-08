package au.com.addstar.btb;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import java.util.Date;
import au.com.addstar.btb.BungeeTeleport.TPRec;

public class BungeeTeleportListener implements Listener {
	BungeeTeleport plugin;
	
	public BungeeTeleportListener(BungeeTeleport p) {
		plugin = p;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		TPRec rec = plugin.TPQueue.get(player.getName());
		if (rec == null) {
			return; // Player has no teleport record, let them be!
		}
		
		if (rec.location == null) {
			plugin.WarnMsg("Teleport location for " + player.getName() + " is null!");
			return;
		}
		
		// Check if TP is too stale (older than 20 seconds)
		long now = new Date().getTime();
		if (now < (rec.timestamp + (20 * 1000))) {
			plugin.DebugMsg("Setting new spawn location for \"" + player.getName() + "\"..");
			event.setSpawnLocation(rec.location);
		} else {
			plugin.WarnMsg("Teleport record for " + player.getName() + " has expired - Ignoring!");
		}
	}
}
