/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/10 20:09:24
 */
package syam.likes.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.exception.CommandException;
import syam.likes.manager.PlayerManager;
import syam.likes.manager.SignManager;
import syam.likes.permission.Perms;
import syam.likes.player.PlayerProfile;
import syam.likes.util.Actions;
import syam.likes.util.Util;

/**
 * CreateCommand (CreateCommand.java)
 * @author syam(syamn)
 */
public class CreateCommand extends BaseCommand{
	public CreateCommand(){
		bePlayer = true;
		name = "create";
		argLength = 1;
		usage = "<id> [description] <- create your new sign";
	}

	@Override
	public void execute() throws CommandException {
		// Check sign
		final Sign sign = Actions.getSign(SignManager.getSelectedSign(player));
		if (sign == null || sign.getBlock() == null){
			throw new CommandException("&c先に設定対象の看板を右クリックで選択してください！");
		}
		final Location loc = sign.getLocation();

		// Check sign name
		final String signName = args.remove(0).trim();
		if (!Pattern.compile("^[a-zA-Z0-9]{2,15}$").matcher(signName).matches()){
			throw new CommandException("&c建築物IDは半角英数字2～15文字で入力してください！");
		}

		// Get profile
		PlayerProfile prof = PlayerManager.getProfile(player.getName());
		if (prof == null || !prof.isLoaded()){
			throw new CommandException("&cあなたの情報が正しく読み込めていません");
		}
		final int playerID = prof.getPlayerID();

		// Get database
		Database db = LikesPlugin.getDatabases();
		HashMap<Integer, ArrayList<String>> result = db.read("SELECT `sign_id` FROM " + db.getTablePrefix() + "signs WHERE `player_id` = ? AND `sign_name` = ?", playerID, signName);
		if (result.size() > 0){
			throw new CommandException("&cあなたは既に同じ建築物IDの看板を設定しています！");
		}

		// Check description
		String description = null;
		if (args.size() > 0){
			description = Util.join(args, " ").trim();
			if (description.length() > 100){
				throw new CommandException("&c建築物説明の文章が長すぎます！");
			}
		}

		// Check already
		if (SignManager.isLikesSign(loc)){
			throw new CommandException("&cこの看板は既に設定されています！");
		}

		// Pay cost
		boolean paid = false;
		double cost = plugin.getConfigs().getCost_Create();
		if (plugin.getConfigs().getUseVault() && cost > 0 && !Perms.FREE_LIKE.has(player)){
			paid = Actions.takeMoney(player.getName(), cost);
			if (!paid){
				throw new CommandException("&cお金が足りません！ " + Actions.getCurrencyString(cost) + "必要です！");
			}
		}

		// create!
		boolean created = SignManager.createSign(sign, player, signName, description);
		if (!created){
			throw new CommandException("&c新規看板の登録処理中にエラーが発生しました！");
		}

		// 看板更新
		sign.setLine(1, "0");
		sign.setLine(2, signName);
		if (player.getName().length() > 15) { sign.setLine(3, player.getName().substring(0, 13) + ".."); }
		else { sign.setLine(3, player.getName()); }
		sign.update();

		String msg = "&a建築物名'"+signName+"'で新規の看板を登録しました！";
		if (paid) msg = msg + " &c(-" + Actions.getCurrencyString(cost) + ")";
		Actions.message(player, msg);
	}

	@Override
	public boolean permission() {
		return Perms.CREATE.has(sender);
	}
}
