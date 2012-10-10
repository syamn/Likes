/**
 * Likes - Package: syam.likes.exception
 * Created: 2012/10/10 20:35:32
 */
package syam.likes.exception;

/**
 * LikesPluginException (LikesPluginException.java)
 * @author syam(syamn)
 */
public class LikesPluginException extends RuntimeException{
	private static final long serialVersionUID = 1101629164226195433L;

	public LikesPluginException(){
	}

	public LikesPluginException(String message){
		super(message);
	}

	public LikesPluginException(Throwable cause){
		super(cause);
	}

	public LikesPluginException(String message, Throwable cause){
		super(message, cause);
	}
}
