/**
 * Likes - Package: syam.likes.manager
 * Created: 2012/10/10 17:35:34
 */
package syam.likes.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.player.LPlayer;
import syam.likes.player.PlayerProfile;

/**
 * PlayerManager (PlayerManager.java)
 * @author syam(syamn)
 */
public class PlayerManager {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private static Map<String, LPlayer> players = new HashMap<String, LPlayer>();

	/**
	 * プレイヤーを追加します
	 * @param player 追加するプレイヤー
	 * @return プレイヤーオブジェクト {@link LPlayer}
	 */
	public static LPlayer addPlayer(Player player){
		LPlayer fgPlayer = players.get(player.getName());

		if (fgPlayer != null){
			// プレイヤーオブジェクトは接続ごとに違うものなので再設定する
			fgPlayer.setPlayer(player);
		}else{
			// 新規プレイヤー
			fgPlayer = new LPlayer(player);
			players.put(player.getName(), fgPlayer);
		}

		return fgPlayer;
	}

	/**
	 * 指定したプレイヤーをマップから削除します
	 * @param playerName 削除するプレイヤー名
	 */
	public static void remove(String playerName){
		players.remove(playerName);
	}

	/**
	 * プレイヤーマップを全削除します
	 */
	public static void clearAll(){
		players.clear();
	}

	/**
	 * 全プレイヤーデータを保存する
	 */
	public static int saveAll(){
		int i = 0;
		for (LPlayer fgPlayer : players.values()){
			fgPlayer.getProfile().save();
			i++;
		}
		return i;
	}

	/**
	 * プレイヤーを取得する
	 * @param playerName 取得対象のプレイヤー名
	 * @return プレイヤー {@link LPlayer}
	 */
	public static LPlayer getPlayer(String playerName){
		return players.get(playerName);
	}

	/**
	 * プレイヤーを取得する
	 * @param player 取得対象のプレイヤー
	 * @return プレイヤー {@link FGPlayer}
	 */
	public static LPlayer getPlayer(Player player){
		return getPlayer(player.getName());
	}

	/**
	 * プレイヤーのプロフィールを取得する
	 * @param player 取得対象のプレイヤー名
	 * @return プレイヤープロフィール {@link PlayerProfile}
	 */
	public static PlayerProfile getProfile(String playerName){
		LPlayer fgPlayer = players.get(playerName);

		return (fgPlayer != null) ? fgPlayer.getProfile() : null;
	}
	/**
	 * プレイヤーのプロフィールを取得する
	 * @param player 取得対象のプレイヤー
	 * @return プレイヤープロフィール {@link PlayerProfile}
	 */
	public static PlayerProfile getProfile(OfflinePlayer player){
		return getProfile(player.getName());
	}

	/* getter / setter */
	public static Map<String, LPlayer> getPlayers(){
		return players;
	}
}
