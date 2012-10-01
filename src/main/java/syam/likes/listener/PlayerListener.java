/**
 * Likes - Package: syam.likes.listener
 * Created: 2012/10/01 16:43:24
 */
package syam.likes.listener;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import syam.likes.LikesPlugin;
import syam.likes.util.Actions;
import syam.likes.util.Util;

/**
 * PlayerListener (PlayerListener.java)
 * @author syam(syamn)
 */
public class PlayerListener implements Listener {
	public final static Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;

	public PlayerListener(final LikesPlugin plugin){
		this.plugin = plugin;
	}

	// プレイヤーがクリックした
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block block = null;

		// ブロックをクリックしていなければ返す
		if (event.hasBlock()){
			block = event.getClickedBlock();
		}else{
			return;
		}

		// 看板チェック
		if ((block.getState() instanceof Sign) && (event.getAction() == Action.RIGHT_CLICK_BLOCK)){
			Sign sign = (Sign) block.getState();
			if (sign.getLine(0).equals("§a[Likes]")){
				// Like回数チェック
				if (!Util.isInteger(sign.getLine(3))){
					Actions.message(player, "&cこの看板は壊れています！");
					return;
				}
				int likes = Integer.parseInt(sign.getLine(3)) + 1;

				sign.setLine(3, String.valueOf(likes));
				sign.update();

				Actions.message(player, "&aあなたはこの建築物を評価しました！");
			}
		}
	}
}
