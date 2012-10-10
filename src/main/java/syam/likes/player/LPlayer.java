/**
 * Likes - Package: syam.likes.player
 * Created: 2012/10/10 16:43:25
 */
package syam.likes.player;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;

/**
 * LPlayer (LPlayer.java)
 * @author syam(syamn)
 */
public class LPlayer {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	// プレイヤーデータ
	private Player player;
	private PlayerProfile profile;

	public LPlayer(final Player player){
		this.player = player;
		this.profile = new PlayerProfile(player.getName(), true);
	}

	/* getter / setter */
	public Player getPlayer(){
		return this.player;
	}
	public void setPlayer(final Player player){
		this.player = player;
	}

	public PlayerProfile getProfile(){
		return this.profile;
	}
}
