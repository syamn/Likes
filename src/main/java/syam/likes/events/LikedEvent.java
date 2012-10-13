/**
 * Likes - Package: syam.likes.events
 * Created: 2012/10/13 12:10:17
 */
package syam.likes.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.likes.sign.LikeSign;

/**
 * LikedEvent (LikedEvent.java)
 * @author syam(syamn)
 */
public class LikedEvent extends Event{
	private static final HandlerList handlers = new HandlerList();

	private Player likePlayer;
	private String likedPlayer;
	private LikeSign likeSign;
	private int likedPlayerTotal;
	private String comment;

	/**
	 * コンストラクタ
	 * @param likePlayer
	 * @param likedPlayer
	 * @param likeSign
	 * @param likedPlayerTotalLiked
	 * @param comment
	 */
	public LikedEvent(Player likePlayer, String likedPlayer, LikeSign likeSign, int likedPlayerTotalLiked, String comment){
		this.likePlayer = likePlayer;
		this.likedPlayer = likedPlayer;
		this.likeSign = likeSign;
		this.likedPlayerTotal = likedPlayerTotalLiked;
		this.comment = comment;
	}

	/* イベントgetter */
	public Player getPlayer(){
		return this.likePlayer;
	}

	public String getLikedPlayerName(){
		return this.likedPlayer;
	}

	public LikeSign getLikeSign(){
		return this.likeSign;
	}

	public Location getLikedSignLocation(){
		if (this.likeSign == null) return null;
		return this.likeSign.getLocation();
	}

	public int getLikedPlayerPoint(){
		return this.likedPlayerTotal;
	}

	public String getComment(){
		return this.comment;
	}

	/* ******************** */
	@Override
	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
