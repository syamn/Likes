/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/10 20:09:24
 */
package syam.likes.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.exception.CommandException;
import syam.likes.manager.PlayerManager;
import syam.likes.permission.Perms;
import syam.likes.player.LPlayer;
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
		usage = "<id> <- create your new sign";
	}

	@Override
	public void execute() throws CommandException {
		String id = args.get(0).trim();
		if (!Pattern.compile("^[a-zA-Z0-9]{2,20}$").matcher(id).matches()){
			throw new CommandException("&c建築物IDは半角英数字2～20文字で入力してください！");
		}

		PlayerProfile prof = PlayerManager.getProfile(player.getName());

		if (prof == null || !prof.isLoaded()){
			throw new CommandException("&cあなたの情報が正しく読み込めていません");
		}

		int playerID = prof.getPlayerID();

		Database database = LikesPlugin.getDatabases();
		String tablePrefix = plugin.getConfigs().getMySQLtablePrefix();

		HashMap<Integer, ArrayList<String>> checkList = database.read("SELECT `sign_id` FROM " + tablePrefix + "signs WHERE `player_id` = " + playerID + " AND `sign_name` = " + id);

		Actions.debug("checkList(keys): "+Util.join(checkList.keySet(), ", "));//debug

		//ArrayList<String> dataValues = checkList.get(0);

	}

	@Override
	public boolean permission() {
		return Perms.CREATE.has(sender);
	}
}
