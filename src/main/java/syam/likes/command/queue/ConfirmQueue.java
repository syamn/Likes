/**
 * Likes - Package: syam.likes.command.queue
 * Created: 2012/10/13 2:58:20
 */
package syam.likes.command.queue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import syam.likes.LikesPlugin;

/**
 * ConfirmQueue (ConfirmQueue.java)
 * @author syam(syamn)
 */
public class ConfirmQueue {
	private LikesPlugin plugin;
	private List<QueuedCommand> queue;

	/**
	 * コンストラクタ
	 * @param plugin
	 */
	public ConfirmQueue(final LikesPlugin plugin) {
		this.plugin = plugin;

		queue = new ArrayList<QueuedCommand>();
	}


	/**
	 * キューにコマンドを追加する
	 * @param sender CommandSender
	 * @param queueable Queueable
	 * @param args List<String>
	 * @param seconds int
	 */
	public void addQueue(CommandSender sender, Queueable queueable, List<String> args, int seconds){
		cancelQueue(sender);
		this.queue.add(new QueuedCommand(sender, queueable, args, seconds));
	}

	/**
	 * キューのコマンドを実行する
	 * @param sender コマンド送信者
	 */
	public boolean confirmQueue(CommandSender sender){
		for (QueuedCommand cmd : this.queue){
			if (cmd.getSender().equals(sender)){
				cmd.execute();
				this.queue.remove(cmd);
				return true;
			}
		}
		return false;
	}

	/**
	 * キューから指定したコマンド送信者のコマンドを削除する
	 * @param sender CommandSender
	 */
	public void cancelQueue(CommandSender sender){
		QueuedCommand cmd = null;
		for (QueuedCommand check : this.queue){
			if (check.getSender().equals(sender)){
				cmd = check;
				break;
			}
		}
		if (cmd != null){
			this.queue.remove(cmd);
		}
	}
}

