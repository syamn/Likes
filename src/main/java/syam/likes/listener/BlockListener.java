/**
 * Likes - Package: syam.likes.listener
 * Created: 2012/10/01 6:23:26
 */
package syam.likes.listener;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;

import syam.likes.LikesPlugin;
import syam.likes.manager.SignManager;
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

				// 1行目の文字色
				if (err){
					event.setLine(0, "§c[Likes]");
				}else{
					event.setLine(0, "§a[Likes]");
					event.setLine(1, "§e== READY ==");
					event.setLine(2, "§7Placed by");
					if (player.getName().length() > 15) { event.setLine(3, player.getName().substring(0, 13) + ".."); }
					else { event.setLine(3, player.getName()); }

					Actions.message(player, "&aLikes看板を設置しました！");
				}
			}
		}
	}

	// ブロックを破壊した
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (protectBlock(block)){
			event.setCancelled(true);
		}
	}

	//@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(final BlockPhysicsEvent event){
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
			if (SignManager.isLikesSign(block.getLocation())){
				org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
				Block attackedBlock = block.getRelative(sign.getAttachedFace());
				if (attackedBlock.getType() == Material.AIR){
					event.setCancelled(true);

				}
			}
		}
	}

	private boolean protectBlock(final Block block){
		// 看板ブロック
		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
			if (SignManager.isLikesSign(block.getLocation())){
				return true;
			}else{
				return false;
			}
		}

		// 一般ブロック
		// 上ブロックチェック
		Block check = block.getRelative(BlockFace.UP);
		if (check.getType() == Material.SIGN_POST && SignManager.isLikesSign(check.getLocation())){
			return true;
		}
		// 周囲ブロックチェック
		final BlockFace[] directions = new BlockFace[]{
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
		};
		for (BlockFace face : directions){
			check = block.getRelative(face);
			if (check.getType() == Material.WALL_SIGN){
				org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) check.getState().getData();
				if (signMat != null && signMat.getFacing() == face && SignManager.isLikesSign(check.getLocation())){
					return true;
				}
			}
		}

		return false;
	}
}
