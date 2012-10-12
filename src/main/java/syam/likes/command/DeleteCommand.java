/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/13 2:49:36
 */
package syam.likes.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import syam.likes.command.queue.Queueable;
import syam.likes.exception.CommandException;
import syam.likes.exception.LikesPluginException;
import syam.likes.manager.SignManager;
import syam.likes.permission.Perms;
import syam.likes.player.PlayerProfile;
import syam.likes.sign.LikeSign;
import syam.likes.util.Actions;

/**
 * DeleteCommand (DeleteCommand.java)
 * @author syam(syamn)
 */
public class DeleteCommand extends BaseCommand implements Queueable{
	public DeleteCommand(){
		bePlayer = true;
		name = "delete";
		argLength = 0;
		usage = "<- delete your sign";
	}

	@Override
	public void execute() throws CommandException {
		// Check sign
		final Sign sign = Actions.getSign(SignManager.getSelectedSign(player));
		if (sign == null || sign.getBlock() == null){
			throw new CommandException("&c先に対象の看板を右クリックで選択してください！");
		}
		final LikeSign ls = SignManager.getLikeSign(sign.getLocation());
		if (ls == null){
			throw new CommandException("&cこの看板は登録されていません！");
		}

		// Check creator
		if (!ls.getCreator().equals(player.getName())){
			throw new CommandException("&cそれはあなたの看板ではありません！");
		}

		final Location loc = ls.getLocation();
		// confirmキュー追加
		@SuppressWarnings("serial")
		ArrayList<String> locStr = new ArrayList<String>() {{
			add(loc.getWorld().getName());
			add(String.valueOf(loc.getBlockX()));
			add(String.valueOf(loc.getBlockY()));
			add(String.valueOf(loc.getBlockZ()));
			}};

		plugin.getQueue().addQueue(sender, this, locStr, 10);
		Actions.message(sender, "&d看板'&6"+ls.getUniqueName()+"&d'を削除しようとしています！");
		if (ls.getLiked() > 0){
			Actions.message(sender, "&c削除すると、この看板に付与された" + ls.getLiked() + "Likeポイントを失います！");
		}
		Actions.message(sender, "&dこの操作は取り消しすることができません。本当に続行しますか？");
		Actions.message(sender, "&d続行するには &a/likes confirm &dコマンドを入力してください！");
		Actions.message(sender, "&a/likes confirm &dコマンドは10秒間のみ有効です。");
	}

	@Override
	public void executeQueue(List<String> qArgs){
		World world = Bukkit.getWorld(qArgs.get(0));
		if (world == null){
			Actions.message(player, "&cワールドが見つかりません！");
			return;
		}

		Location loc = new Location(world,
				Double.parseDouble(qArgs.get(1)),
				Double.parseDouble(qArgs.get(2)),
				Double.parseDouble(qArgs.get(3)));

		final LikeSign ls = SignManager.getLikeSign(loc);
		if (ls == null){
			Actions.message(player, "&c登録済みの看板が見つかりません！");
			return;
		}

		// Check creator again
		if (!ls.getCreator().equals(player.getName())){
			Actions.message(player, "&cそれはあなたの看板ではありません！");
			return;
		}

		// delete!
		if (ls.deleteSign()){
			// success
			PlayerProfile prof = new PlayerProfile(ls.getCreator(), false);
			if (!prof.isLoaded() || prof.getPlayerID() == 0){
				throw new LikesPluginException("This player records does not exist! creator="+ls.getCreator());
			}

			int remLiked = ls.getLiked();
			if (remLiked > 0){
				prof.setLikeReceiveCount(prof.getLikeReceiveCount() - remLiked);
				prof.save();

				Actions.message(sender, "&a看板を削除し、" + remLiked + "つのLikeポイントを失いました！");
			}else{
				Actions.message(sender, "&a看板を削除しました！");
			}
		}else{
			Actions.message(sender, "&c看板の削除に失敗しました！");
		}
	}

	@Override
	public boolean permission() {
		return Perms.DELETE.has(sender);
	}
}
