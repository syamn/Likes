/**
 * Likes - Package: syam.likes.manager
 * Created: 2012/10/10 18:52:07
 */
package syam.likes.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import syam.likes.LikesPlugin;
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


}
