/**
 * Likes - Package: syam.likes.listener
 * Created: 2012/10/11 22:41:50
 */
package syam.likes.listener;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import syam.likes.LikesPlugin;
import syam.likes.manager.SignManager;

/**
 * SignProtectListener (SignProtectListener.java)
 * @author syam(syamn)
 */
public class SignProtectListener implements Listener{
	public final static Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;

	public SignProtectListener(final LikesPlugin plugin){
		this.plugin = plugin;
	}

	/* Block Listener */
	// ブロックを破壊した
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (protectBlock(block)){
			event.setCancelled(true);
		}
	}

	// ピストンを展開した
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event){
		for (Block block : event.getBlocks()){
			if (protectBlock(block)){
				event.setCancelled(true);
			}
		}
	}

	// ピストンを格納した
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event){
		if (event.isSticky() && protectBlock(event.getBlock())){
			event.setCancelled(true);
		}
	}

	// ブロックが消失した
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBurn(final BlockBurnEvent event){
		if (protectBlock(event.getBlock())){
			event.setCancelled(true);
		}
	}

	// ブロックに着火した
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event){
		if (protectBlock(event.getBlock())){
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


	/* Entity Listener */
	// ブロックが爆発した
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplode(final EntityExplodeEvent event){
		for (Block block : event.blockList()){
			if (protectBlock(block)){
				event.setCancelled(true);
				return;
			}
		}
	}

	// エンティティがブロックを変えた
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityChangeBlock(final EntityChangeBlockEvent event){
		if (protectBlock(event.getBlock())){
			event.setCancelled(true);
		}
	}

	/* ********** */
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
