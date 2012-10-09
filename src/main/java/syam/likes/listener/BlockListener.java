/**
 * Likes - Package: syam.likes.listener
 * Created: 2012/10/01 6:23:26
 */
package syam.likes.listener;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import syam.likes.LikesPlugin;
import syam.likes.permission.Perms;
import syam.likes.util.Actions;

/**
 * BlockListener (BlockListener.java)
 * @author syam(syamn)
 */
public class BlockListener implements Listener {
	public final static Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;

	public BlockListener(final LikesPlugin plugin){
		this.plugin = plugin;
	}

	// 看板を設置した
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		BlockState state = event.getBlock().getState();

		if (state instanceof Sign){
			Sign sign = (Sign)state;

			/* [Likes] 特殊看板 */
			if (event.getLine(0).toLowerCase().indexOf("[likes]") != -1 ||
					event.getLine(0).toLowerCase().indexOf("[like]") != -1){
				// 権限チェック
				if (!Perms.PLACESIGN.has(player)){
					event.setLine(0, "§c[Likes]");
					event.setLine(1, "Perm Denied :(");
					Actions.message(player, "&cYou don't have permission to use this!");
					return;
				}

				// 内容チェック
				boolean err = false; // エラーフラグ

				if (event.getLine(1).trim().length() < 3){
					Actions.message(player, "&a2行目に建築物名を3文字以上で入力してください！");
					err = true;
				}
				else if (!event.getLine(2).trim().equals("") || !event.getLine(3).trim().equals("") ){
					Actions.message(player, "&c3、4行目は空白にしてください！");
					err = true;
				}

				// 1行目の文字色
				if (err){
					event.setLine(0, "§c[Likes]");
				}else{
					event.setLine(0, "§a[Likes]");
					event.setLine(2, "Likes Received:");
					event.setLine(3, "0");

					Actions.message(player, "&aLikes看板を設置しました！");
				}
			}
		}
	}
}
