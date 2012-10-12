/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/13 2:57:17
 */
package syam.likes.command;

import syam.likes.exception.CommandException;

/**
 * ConfirmCommand (ConfirmCommand.java)
 * @author syam(syamn)
 */
public class ConfirmCommand extends BaseCommand {
	public ConfirmCommand(){
		bePlayer = false;
		name = "confirm";
		argLength = 0;
		usage = "<- command confirm";
	}

	@Override
	public void execute() throws CommandException {
		boolean ran = this.plugin.getQueue().confirmQueue(sender);
		if (!ran){
			throw new CommandException("&cあなたの実行待ちコマンドはありません！");
		}
	}

	@Override
	public boolean permission() {
		return true;
	}
}