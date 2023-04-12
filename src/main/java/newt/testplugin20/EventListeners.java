package newt.testplugin20;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListeners implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!GameManager.InGame)
            return;

        Player p = event.getPlayer();
        Location location = p.getLocation();

        if (GameManager.GetPlayerData(p) == null)
            return;

        Location defaultLocation = GameManager.GetPlayerData(p).location;
        if (location.getBlockX() != defaultLocation.getBlockX() || location.getBlockZ() != defaultLocation.getBlockZ())
            p.teleport(new Location(defaultLocation.getWorld(), defaultLocation.getBlockX() + 0.5d, defaultLocation.getBlockY(), defaultLocation.getBlockZ() + 0.5d, location.getYaw(), location.getPitch()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bar.AddPlayer(event.getPlayer());
    }
}
