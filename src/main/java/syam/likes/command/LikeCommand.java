/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/11 23:43:39
 */
package syam.likes.command;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

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
		// Check target sign
		final Sign sign = Actions.getSign(SignManager.getSelectedSign(player));
		if (sign == null || sign.getBlock() == null){
			throw new CommandException("&c先に対象の看板を右クリックで選択してください！");
		}
		final LikeSign ls = SignManager.getLikeSign(sign.getLocation());
		if (ls == null){
			throw new CommandException("&cこの看板はまだ登録されていません！");
		}

		// Check comment
		String comment = null;
		if (args.size() > 0){
			comment = Util.join(args, " ").trim();
			if (comment.length() > 100){
				throw new CommandException("&cコメントが長すぎます！");
			}
		}

		// Check creator
		if (ls.getCreator().equals(player.getName())){
			throw new CommandException("&cそれは自分が設置した看板です！");
		}

		// Check time
		if (!PlayerManager.getPlayer(player.getName()).canDoLikeTime()){
			throw new CommandException("&c6時間に1回だけお気に入りに登録することができます！");
		}

		// Check already
		if (ls.isAlreadyLiked(player)){
			throw new CommandException("&cあなたは既にこの看板をお気に入りにしています！");
		}

		// Pay cost
		boolean paid = false;
		double cost = plugin.getConfigs().getCost_Like();
		if (plugin.getConfigs().getUseVault() && cost > 0 && !Perms.FREE_LIKE.has(player)){
			paid = Actions.takeMoney(player.getName(), cost);
			if (!paid){
				throw new CommandException("&cお金が足りません！ " + Actions.getCurrencyString(cost) + "必要です！");
			}
		}

		// do like!
		if (ls.addLike(player, comment)){
			String msg = "&aこの建築物をお気に入りに追加しました！";
			if (paid) msg = msg + " &c(-" + Actions.getCurrencyString(cost) + ")";
			Actions.message(player, msg);

			Player creator = Bukkit.getPlayer(ls.getCreator());
			if (creator != null && creator.isOnline()){
				Actions.message(sender, "&aプレイヤー'&6"+player.getName()+"&a'があなたの建築物'&6"+ls.getName()+"&a'をお気に入りに登録しました！");
				if (comment != null && comment.length() > 0){
					Actions.message(sender, "&aコメント: &6");
				}
			}
		}
		// error
		else{
			Actions.message(player, "&c内部エラーが発生しました。管理人へご連絡ください。");
			if (paid) Actions.addMoney(player.getName(), cost); // refund
		}
	}

	@Override
	public boolean permission() {
		return Perms.LIKE.has(sender);
	}
}

