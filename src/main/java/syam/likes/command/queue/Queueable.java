/**
 * Likes - Package: syam.likes.command.queue
 * Created: 2012/10/13 2:59:37
 */
package syam.likes.command.queue;

import java.util.List;

/**
 * Queueable (Queueable.java)
 * @author syam(syamn)
 */
public interface Queueable {
	void executeQueue(List<String> args);
}

