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
import syam.likes.manager.SetupManager;
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
		final Sign sign = SetupManager.getSelectedSign(player);
		if (sign == null || sign.getBlock() == null){
			throw new CommandException("&c先に設定対象の看板を右クリックで選択してください！");
		}
		final Location loc = sign.getLocation();

		final String id = args.remove(0).trim();
		if (!Pattern.compile("^[a-zA-Z0-9]{2,15}$").matcher(id).matches()){
			throw new CommandException("&c建築物IDは半角英数字2～15文字で入力してください！");
		}

		PlayerProfile prof = PlayerManager.getProfile(player.getName());

		if (prof == null || !prof.isLoaded()){
			throw new CommandException("&cあなたの情報が正しく読み込めていません");
		}

		final int playerID = prof.getPlayerID();

		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = plugin.getConfigs().getMySQLtablePrefix();

		HashMap<Integer, ArrayList<String>> result = database.read("SELECT `sign_id` FROM " + tablePrefix + "signs WHERE `player_id` = " + playerID + " AND `sign_name` = '" + id + "'");
		if (result.size() > 0){
			throw new CommandException("&cあなたは既に同じ建築物IDの看板を設定しています！");
		}

		String description = null;
		if (args.size() > 0){
			description = Util.join(args, " ").trim();
			if (description.length() > 100){
				throw new CommandException("&c建築物説明の文章が長すぎます！");
			}
		}

		result = database.read("SELECT `sign_id` FROM " + tablePrefix + "signs WHERE `world` = '" + loc.getWorld().getName() + "' AND `x` = " + loc.getBlockX() + " AND `y` = " + loc.getBlockY() + " AND `z` = " + loc.getBlockZ());
		if (result.size() > 0){
			throw new CommandException("&cこの看板は既に設定されています！");
		}

		// create
		boolean created = SignManager.createSign(sign, player, id, description);
		if (!created){
			throw new CommandException("&c新規評価看板の登録処理中にエラーが発生しました！");
		}

		// 看板更新
		sign.setLine(1, "0");
		sign.setLine(2, id);
		if (player.getName().length() > 15) { sign.setLine(3, player.getName().substring(0, 13) + ".."); }
		else { sign.setLine(3, player.getName()); }
		sign.update();

		Actions.message(player, "&a建築物名'"+id+"'で新規の評価看板を登録しました！");
	}

	@Override
	public boolean permission() {
		return Perms.CREATE.has(sender);
	}
}
