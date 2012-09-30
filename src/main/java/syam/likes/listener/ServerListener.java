/**
 * Likes - Package: syam.likes.listener
 * Created: 2012/10/01 5:43:21
 */
package syam.likes.listener;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import syam.likes.LikesPlugin;

/**
 * ServerListener (ServerListener.java)
 * @author syam(syamn)
 */
public class ServerListener implements Listener{
	// Logger
    private static final Logger log = LikesPlugin.log;
    private static final String logPrefix = LikesPlugin.logPrefix;
    private static final String msgPrefix = LikesPlugin.msgPrefix;

    private final LikesPlugin plugin;

    public ServerListener(final LikesPlugin plugin){
        this.plugin = plugin;
    }

    // plugin unloading..
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginDisable(final PluginDisableEvent event){
        if (!plugin.getConfigs().getUseVault()){
            return;
        }

        String pname = event.getPlugin().getName();

        if (pname.equals("Vault")){
            log.warning(logPrefix + "Detected unloading Vault plugin. Disabled Vault integration.");
            plugin.getConfigs().setUseVault(false);
        }
    }
}
