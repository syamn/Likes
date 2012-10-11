/**
 * Likes - Package: syam.likes.manager
 * Created: 2012/10/10 18:52:07
 */
package syam.likes.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.exception.LikesPluginException;
import syam.likes.player.PlayerProfile;
import syam.likes.sign.LikeSign;

/**
 * SignManager (SignManager.java)
 * @author syam(syamn)
 */
public class SignManager {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;
	public SignManager(final LikesPlugin plugin){
		this.plugin = plugin;
	}

	/*
	private static HashMap<Integer, LikeSign> signs = new HashMap<Integer, LikeSign>();
	public static HashMap<Integer, LikeSign> getSigns(){
		return signs;
	}
	public static void addSign(Integer signID, LikeSign sign){
		signs.put(signID, sign);
	}
	public static void removeSign(Integer signID){
		signs.remove(signID);
	}
	public static LikeSign getSign(Integer signID){
		return signs.get(signID);
	}
	*/

	private static HashMap<Location, Integer> signs = new HashMap<Location, Integer>();
	public static HashMap<Location, Integer> getSigns(){
		return signs;
	}
	public static void addSign(Location loc, int signID){
		signs.put(loc, signID);
	}
	public static void removeSign(Location loc){
		signs.remove(loc);
	}
	public static int getSignID(Location loc){
		return signs.get(loc);
	}
	public static boolean isLikesSign(Location loc){
		return signs.containsKey(loc);
	}

	/*********/
	/**
	 * 新規評価看板をDBに登録する
	 * @param sign
	 * @param creator
	 * @param sign_name
	 * @param description
	 * @return
	 */
	public static boolean createSign(final Sign sign, final Player creator, final String sign_name, final String description){
		if (sign == null || sign.getBlock() == null || creator == null || sign_name == null){
			return false;
		}

		final Location loc = sign.getBlock().getLocation();

		PlayerProfile prof = new PlayerProfile(creator.getName(), false);
		if (!prof.isLoaded() || prof.getPlayerID() == 0){
			log.severe("This player records does not exist! creator="+creator.getName());
			return false;
		}
		final int playerID = prof.getPlayerID();

		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = LikesPlugin.getInstance().getConfigs().getMySQLtablePrefix();

		database.write("INSERT INTO " + tablePrefix + "signs " +
				"(`player_id`, `sign_name`, `world`, `x`, `y`, `z`) VALUES " +
				"(" + playerID + ", '" + sign_name + "', '" + loc.getWorld().getName() + "', " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
		final int signID = database.getInt("SELECT `sign_id` FROM "+tablePrefix + "signs WHERE `player_id` = " + playerID + " AND `sign_name` = '" + sign_name + "'");
		if (signID == 0){
			throw new LikesPluginException("Could not insert to " + tablePrefix + "signs table properly!");
		}

		// Add
		addSign(loc, signID);
		return true;
	}

	/**
	 * データベースから看板データをマッピングする
	 */
	public static int loadSigns(){
		signs.clear();
		World world = null;

		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = LikesPlugin.getInstance().getConfigs().getMySQLtablePrefix();

		HashMap<Integer, ArrayList<String>> result = database.read("SELECT `sign_id`, `world`, `x`, `y`, `z` FROM " + tablePrefix + "signs");
		for (ArrayList<String> record : result.values()){
			world = Bukkit.getWorld(record.get(1));
			if (world == null){
				log.warning(logPrefix+ "Skipping SignID " + record.get(0) + ":not exist world " + record.get(1));
				continue;
			}
			signs.put(
					new Location(
							world,
							Double.parseDouble(record.get(2)),
							Double.parseDouble(record.get(3)),
							Double.parseDouble(record.get(4))
							),
					Integer.parseInt(record.get(0))
					);
		}

		return signs.size();
	}
}
