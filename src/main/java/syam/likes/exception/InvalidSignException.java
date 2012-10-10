/**
 * Likes - Package: syam.likes.exception
 * Created: 2012/10/11 7:19:44
 */
package syam.likes.exception;

/**
 * InvalidSignException (InvalidSignException.java)
 * @author syam(syamn)
 */
public class InvalidSignException extends LikesPluginException{
	private static final long serialVersionUID = -6394767435855458354L;

	public InvalidSignException(){
	}

	public InvalidSignException(String message){
		super(message);
	}

	public InvalidSignException(Throwable cause){
		super(cause);
	}

	public InvalidSignException(String message, Throwable cause){
		super(message, cause);
	}
}
