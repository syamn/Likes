/**
 * Likes - Package: syam.likes.manager
 * Created: 2012/10/11 6:31:38
 */
package syam.likes.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;

/**
 * SetupManager (SetupManager.java)
 * @author syam(syamn)
 */
public class SetupManager {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;
	public SetupManager(final LikesPlugin plugin){
		this.plugin = plugin;
	}

	// 選択中の看板
	private static Map<String, Sign> selectedSign = new HashMap<String, Sign>();


	public static void setSelectedSign(Player player, Sign sign){
		selectedSign.put(player.getName(), sign);
	}
	public static Sign getSelectedSign(Player player){
		return (player == null) ? null : selectedSign.get(player.getName());
	}

	@Deprecated
	public static Sign getSign(final Block block){
		if (block == null) return null;

		// ItemID: 63-Sign post, 68-Wall sign
		if (block.getTypeId() != 63 && block.getTypeId() != 68){
			return null;
		}

		// instanceof Sign
		if (!(block.getState() instanceof Sign)){
			return null;
		}

		return (Sign) block.getState();
	}

}
