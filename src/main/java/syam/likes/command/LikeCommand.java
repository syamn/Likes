/**
 * Likes - Package: syam.likes.command
 * Created: 2012/10/11 23:43:39
 */
package syam.likes.command;

import syam.likes.exception.CommandException;

/**
 * LikeCommand (LikeCommand.java)
 * @author syam(syamn)
 */
public class LikeCommand extends BaseCommand{
	public LikeCommand(){
		bePlayer = false;
		name = "like";
		argLength = 0;
		usage = "<- Like It!";
	}

	@Override
	public void execute() throws CommandException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean permission() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
