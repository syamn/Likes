/**
 * Likes - Package: syam.likes.sign
 * Created: 2012/10/10 19:24:10
 */
package syam.likes.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.exception.InvalidSignException;
import syam.likes.exception.LikesPluginException;
import syam.likes.player.PlayerProfile;

/**
 * LikeSign (LikeSign.java)
 * @author syam(syamn)
 */
public class LikeSign {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;
	private int signID;
	private Sign sign;

	/**
	 * コンストラクタ
	 * @param plugin
	 * @param sign
	 */
	public LikeSign(final LikesPlugin plugin, final Sign sign){
		this.plugin = plugin;
		this.sign = sign;
		Location loc = sign.getLocation();

		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = plugin.getConfigs().getMySQLtablePrefix();
		this.signID = database.getInt("SELECT `sign_id` FROM " + tablePrefix + "signs WHERE `world` = '" + loc.getWorld().getName() + "' AND `x` = " + loc.getBlockX() + " AND `y` = " + loc.getBlockY() + " AND `z` = " + loc.getBlockZ());
		if (signID == 0){
			throw new InvalidSignException("This sign is not registered!");
		}

	}
}
