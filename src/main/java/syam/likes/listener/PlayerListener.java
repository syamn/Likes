/**
 * Likes - Package: syam.likes.listener
 * Created: 2012/10/01 16:43:24
 */
package syam.likes.listener;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import syam.likes.LikesPlugin;
import syam.likes.manager.PlayerManager;
import syam.likes.manager.SetupManager;
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
				SetupManager.setSelectedSign(player, sign);
				Actions.message(player, msgPrefix+ "この看板を選択しました！");
			}
		}
	}

	// プレイヤーがログインしようとした
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event){
		// プレイヤー追加
		PlayerManager.addPlayer(event.getPlayer());
	}

	// プレイヤーがログアウトした
	//@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerQuit(final PlayerQuitEvent event){
		Player player = event.getPlayer();

		/* TODO: GC here */
	}
}
