/**
 * LikesPlugin - Package: syam.likes.command
 * Created: 2012/09/30 23:45:35
 */
package syam.likes.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.util.Actions;

/**
 * BaseCommand (BaseCommand.java)
 * @author syam(syamn)
 */
public abstract class BaseCommand {
	// Logger
	protected static final Logger log = LikesPlugin.log;
	protected static final String logPrefix = LikesPlugin.logPrefix;
	protected static final String msgPrefix = LikesPlugin.msgPrefix;
	/* コマンド関係 */
	public CommandSender sender;
	public List<String> args = new ArrayList<String>();
	public String name;
	public int argLength = 0;
	public String usage;
	public boolean bePlayer = true;
	public Player player;
	public String command;
	public LikesPlugin plugin;

	public boolean run(LikesPlugin plugin, CommandSender sender, String[] preArgs, String cmd) {
		this.plugin = plugin;
		this.sender = sender;
		this.command = cmd;

		// 引数をソート
		args.clear();
		for (String arg : preArgs)
			args.add(arg);

		// 引数からコマンドの部分を取り除く
		// (コマンド名に含まれる半角スペースをカウント、リストの先頭から順にループで取り除く)
		for (int i = 0; i < name.split(" ").length && i < args.size(); i++)
			args.remove(0);

		// 引数の長さチェック
		if (argLength > args.size()){
			sendUsage();
			return true;
		}

		// 実行にプレイヤーであることが必要かチェックする
		if (bePlayer && !(sender instanceof Player)){
			Actions.message(sender, "&cThis command cannot run from Console!");
			return true;
		}
		if (sender instanceof Player){
			player = (Player)sender;
		}

		// 権限チェック
		if (!permission()){
			Actions.message(sender, "&cYou don't have permission to use this!");
			return true;
		}

		// 実行
		execute();

		return true;
	}

	/**
	 * コマンドを実際に実行する
	 * @return 成功すればtrue それ以外はfalse
	 */
	public abstract void execute();

	/**
	 * コマンド実行に必要な権限を持っているか検証する
	 * @return trueなら権限あり、falseなら権限なし
	 */
	public abstract boolean permission();

	/**
	 * コマンドの使い方を送信する
	 */
	public void sendUsage(){
		Actions.message(sender, "&c/"+this.command+" "+name+" "+usage);
	}
}