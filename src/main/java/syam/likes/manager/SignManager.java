/**
 * Likes - Package: syam.likes.manager
 * Created: 2012/10/10 18:52:07
 */
package syam.likes.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.sign.LikeSign;
import syam.likes.util.Util;

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

	// 選択中の看板
	private static Map<String, Location> selectedSign = new HashMap<String, Location>();
	public static void setSelectedSign(Player player, Location loc){
		selectedSign.put(player.getName(), loc);
	}
	public static Location getSelectedSign(Player player){
		return (player == null) ? null : selectedSign.get(player.getName());
	}

	// 評価看板
	private static HashMap<Location, LikeSign> signs = new HashMap<Location, LikeSign>();
	public static boolean isLikesSign(Location loc){
		return signs.containsKey(loc);
	}
	public static LikeSign getLikeSign(Location loc){
		return signs.get(loc);
	}
	public static void removLikeSign(Location loc){
		signs.remove(loc);
	}

	public static int saveAll(){
		int i = 0;
		for (LikeSign ls : signs.values()){
			ls.save();
			i++;
		}
		return i;
	}

	/* ******* */
	/**
	 * 建築者の評価看板リストを返す
	 * @param creator
	 * @return List<LikeSign>
	 */
	public static List<LikeSign> getLikeSignsByCreator(String creator){
		List<LikeSign> ret = new ArrayList<LikeSign>();

		for (LikeSign ls : signs.values()){
			if (ls.getCreator().equalsIgnoreCase(creator)){
				ret.add(ls);
			}
		}

		return ret;
	}
	/**
	 * 看板IDから評価看板を返す
	 * @param signID
	 * @return LikeSign or null
	 */
	public static LikeSign getLikeSignBySignID(int signID){
		for (LikeSign ls : signs.values()){
			if (ls.getSignID() == signID){
				return ls;
			}
		}
		return null; // not found
	}
	/**
	 * 建築者名と看板名から評価看板を返す
	 * @param creator
	 * @param signName
	 * @return LikeSign or null
	 */
	public static LikeSign getLikeSignByCreatorAndName(String creator, String signName){
		for (LikeSign ls : signs.values()){
			if (ls.getCreator().equalsIgnoreCase(creator) &&
					ls.getName().equalsIgnoreCase(signName)){
				return ls;
			}
		}
		return null;
	}
	/**
	 * 看板のユニーク名から評価看板を返す
	 * @param unique
	 * @return LikeSign or null
	 */
	public static LikeSign getLikeSignByUniqueName(String unique){
		String[] s = unique.split(".");
		if (s.length != 2){
			return null;
		}
		return getLikeSignByCreatorAndName(s[0], s[1]);
	}


	/* ******* */
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

		LikeSign ls = new LikeSign(0, sign_name, creator.getName(), 0, description, 0, 0, Util.getCurrentUnixSec(), loc);
		ls.save(true); // INSERT

		// Add
		signs.put(loc, ls);
		return true;
	}

	/**
	 * データベースから看板データをマッピングする
	 */
	public static int loadSigns(){
		signs.clear();
		World world;
		Location loc;

		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = LikesPlugin.getInstance().getConfigs().getMySQLtablePrefix();

		HashMap<Integer, ArrayList<String>> result = database.read(
				"SELECT `sign_id`, `sign_name`, `player_name`, `status`, `text`, `liked`, `lastliked`, `created`, `world`, `x`, `y`, `z` " +
				"FROM " + tablePrefix + "signs NATURAL JOIN " + tablePrefix + "users");
		for (ArrayList<String> record : result.values()){
			int signID = Integer.parseInt(record.get(0));

			world = Bukkit.getWorld(record.get(8));
			if (world == null){
				log.warning(logPrefix+ "Skipping SignID " + record.get(0) + ":not exist world " + record.get(1));
				continue;
			}

			loc = new Location(
					world,
					Double.parseDouble(record.get(9)),
					Double.parseDouble(record.get(10)),
					Double.parseDouble(record.get(11))
					);

			LikeSign ls = new LikeSign(
					signID,
					record.get(1),
					record.get(2),
					Integer.parseInt(record.get(3)),
					record.get(4),
					Integer.parseInt(record.get(5)),
					Long.parseLong(record.get(6)),
					Long.parseLong(record.get(7)),
					loc
					);

			ls.updateSign();
			// Add HashMap
			signs.put(loc, ls);
		}
		return signs.size();
	}
}
