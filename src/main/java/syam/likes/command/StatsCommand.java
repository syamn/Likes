/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/13 4:15:28
 */
package syam.likes.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import syam.likes.exception.CommandException;
import syam.likes.manager.PlayerManager;
import syam.likes.manager.SignManager;
import syam.likes.permission.Perms;
import syam.likes.player.LPlayer;
import syam.likes.player.PlayerProfile;
import syam.likes.sign.LikeSign;
import syam.likes.util.Actions;


/**
 * StatsCommand (StatsCommand.java)
 * @author syam(syamn)
 */
public class StatsCommand extends BaseCommand{
	public StatsCommand(){
		bePlayer = false;
		name = "stats";
		argLength = 0;
		usage = "[player] <- show your stats";
	}
	@Override
	public void execute() throws CommandException {
		PlayerProfile prof = null;
		boolean other = false;

		// 自分の情報表示
		if (args.size() == 0){
			// check console
			if (!(sender instanceof Player)){
				throw new CommandException("&c情報を表示するユーザ名を入力してください");
			}

			// check permission
			if (!Perms.STATS_SELF.has(sender)){
				throw new CommandException("&cあなたはこのコマンドを使う権限がありません");
			}

			prof = PlayerManager.getProfile(player.getName());
		}
		// 他人の情報表示
		else{
			other = true;

			// check permission
			if (!Perms.STATS_OTHER.has(sender)){
				throw new CommandException("&cあなたは他人の情報を見る権限がありません");
			}

			LPlayer lPlayer = PlayerManager.getPlayer(args.get(0));

			// 対象者がログイン中かどうか
			if (lPlayer != null){
				prof = lPlayer.getProfile();
			}
			// オフライン
			else{
				prof = new PlayerProfile(args.get(0), false);

				if (!prof.isLoaded()){
					throw new CommandException("&c指定したプレイヤーの情報が見つかりません");
				}
			}
		}

		// check null
		if (prof == null){
			throw new CommandException("&cプレイヤー情報が正しく読み込めませんでした");
		}

		// メッセージ送信
		for (String line : buildStrings(prof, other)){
			Actions.message(sender, line);
		}
	}

	private List<String> buildStrings(PlayerProfile prof, boolean other){
		List<String> l = new ArrayList<String>();
		l.clear();

		// ヘッダー
		l.add(msgPrefix + "&aプレイヤー情報");
		if (other)
			l.add("&aプレイヤー: &6" + prof.getPlayerName());

		// 総合 *************************************************
		l.add("&6-=== 総合 ===-");
		l.add("&e評価された回数: &a" + prof.getLikeReceiveCount() + " Like(s) Point");
		l.add("&e　評価した回数: &a" + prof.getLikeGiveCount() + " Like(s) Point");

		// 建築物 *************************************************
		List<LikeSign> signs = SignManager.getLikeSignsByCreator(prof.getPlayerName());
		l.add("&6-=== 建築物(" + signs.size() + ") ===-");
		if (signs.size() == 0){
			l.add("&7なし");
		}else{
			for (LikeSign ls : signs){
				l.add(" &e" + ls.getName() + "&7 : &a" + ls.getLiked() + " Like(s) &7: " + Actions.getBlockLocationString(ls.getLocation()));
			}
		}

		return l;
	}

	@Override
	public boolean permission() {
		return (Perms.STATS_SELF.has(sender) ||
				Perms.STATS_OTHER.has(sender));
	}
}
