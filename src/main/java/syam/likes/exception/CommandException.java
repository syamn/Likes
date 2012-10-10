/**
 * Likes - Package: syam.likes.exception
 * Created: 2012/10/10 20:35:21
 */
package syam.likes.exception;

/**
 * CommandException (CommandException.java)
 * @author syam(syamn)
 */
public class CommandException extends Exception{
	private static final long serialVersionUID = 1733069958786229627L;

	public CommandException(String message){
		super(message);
	}

	public CommandException(Throwable cause){
		super(cause);
	}

	public CommandException(String message, Throwable cause){
		super(message, cause);
	}
}