/**
 * Likes - Package: syam.likes.database
 * Created: 2012/10/10 7:03:56
 */
package syam.likes.database;

import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.manager.PlayerManager;
import syam.likes.manager.SignManager;

/**
 * MySQLReconnect (MySQLReconnect.java)
 * @author syam(syamn)
 */
public class MySQLReconnect implements Runnable{
	private final LikesPlugin plugin;

	public MySQLReconnect(final LikesPlugin plugin){
		this.plugin = plugin;
	}

	@Override
	public void run(){
		if (!Database.isConnected()){
			Database.connect();
			if (Database.isConnected()){
				PlayerManager.saveAll();
				PlayerManager.clearAll();

				for (Player player : plugin.getServer().getOnlinePlayers()){
					PlayerManager.addPlayer(player);
				}

				// DB使用データ保存
				PlayerManager.saveAll();
				SignManager.saveAll();
			}
		}
	}
}
