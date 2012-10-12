/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/11 23:43:39
 */
package syam.likes.command;

import org.bukkit.block.Sign;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.exception.CommandException;
import syam.likes.manager.PlayerManager;
import syam.likes.manager.SignManager;
import syam.likes.permission.Perms;
import syam.likes.sign.LikeSign;
import syam.likes.util.Actions;
import syam.likes.util.Util;

/**
 * LikeCommand (LikeCommand.java)
 * @author syam(syamn)
 */
public class LikeCommand extends BaseCommand{
	public LikeCommand(){
		bePlayer = true;
		name = "like";
		argLength = 0;
		usage = "[comment] <- Like It!";
	}

	@Override
	public void execute() throws CommandException {
		final Sign sign = Actions.getSign(SignManager.getSelectedSign(player));
		if (sign == null || sign.getBlock() == null){
			throw new CommandException("&c先に対象の看板を右クリックで選択してください！");
		}

		final LikeSign ls = SignManager.getLikeSign(sign.getLocation());
		if (ls == null){
			throw new CommandException("&cこの看板はまだ登録されていません！");
		}

		String comment = null;
		if (args.size() > 0){
			comment = Util.join(args, " ").trim();
			if (comment.length() > 100){
				throw new CommandException("&cコメントが長すぎます！");
			}
		}

		if (!PlayerManager.getPlayer(player.getName()).canDoLikeTime()){
			throw new CommandException("&c6時間に1回だけお気に入りに登録することができます！");
		}

		if (ls.isAlreadyLiked(player)){
			throw new CommandException("&cあなたは既にこの看板をお気に入りにしています！");
		}

		// do like!
		if (ls.addLike(player, comment)){
			Actions.message(player, "&aこの建築物をお気に入りに追加しました！");
		}else{
			Actions.message(player, "&c内部エラーが発生しました。管理人へご連絡ください。");
		}
	}

	@Override
	public boolean permission() {
		return Perms.LIKE.has(sender);
	}
}

