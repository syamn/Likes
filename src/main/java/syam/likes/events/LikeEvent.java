/**
 * Likes - Package: syam.likes.events
 * Created: 2012/10/13 13:17:55
 */
package syam.likes.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.likes.sign.LikeSign;

/**
 * LikeEvent (LikeEvent.java)
 * @author syam(syamn)
 */
public class LikeEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private LikeSign likeSign;
	private double cost;
	private String comment;

	private boolean isCancelled;

	/**
	 * コンストラクタ
	 * @param player
	 * @param cost
	 */
	public LikeEvent(Player player, LikeSign likeSign, double cost, String comment){
		this.player = player;
		this.likeSign = likeSign;
		this.cost = cost;
		this.comment = comment;

		this.isCancelled = false;
	}

	public Player getPlayer(){
		return this.player;
	}

	public LikeSign getLikeSign(){
		return this.likeSign;
	}

	public Location getLikeSignLocation(){
		if (this.likeSign == null) return null;
		return this.likeSign.getLocation();
	}

	public double getCost(){
		return this.cost;
	}
	public void setCost(double cost){
		this.cost = cost;
	}

	public String getComment(){
		return this.comment;
	}
	public void setComment(String comment){
		this.comment = comment;
	}

	public boolean isCancelled(){
		return this.isCancelled;
	}
	public void setCancelled(boolean cancelled){
		this.isCancelled = cancelled;
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
