/**
 * Likes - Package: syam.likes.permission
 * Created: 2012/10/01 0:23:08
 */
package syam.likes.permission;

import org.bukkit.permissions.Permissible;

import syam.likes.LikesPlugin;

/**
 * Perms (Perms.java)
 * @author syam(syamn)
 */
public enum Perms {
	/* 権限ノード */

	/* コマンド系 */
	// User Commands

	// Admin Commands
	RELOAD	("admin.reload"),

	// Setup Commands

	// 特殊系
	PLACESIGN ("user.placesign"),

	;

	// ノードヘッダー
	final String HEADER = "likes.";
	private String node;

	/**
	 * コンストラクタ
	 * @param node 権限ノード
	 */
	Perms(final String node){
		this.node = HEADER + node;
	}

	/**
	 * 指定したプレイヤーが権限を持っているか
	 * @param player Permissible. Player, CommandSender etc
	 * @return boolean
	 */
	public boolean has(final Permissible perm){
		if (perm == null) return false;
		return handler.has(perm, this.node);
	}

	/**
	 * 指定したプレイヤーが権限を持っているか(String)
	 * @param player PlayerName
	 * @return boolean
	 */
	public boolean has(final String playerName){
		if (playerName == null) return false;
		return has(LikesPlugin.getInstance().getServer().getPlayer(playerName));
	}

	/* ***** Static ***** */
	// 権限ハンドラ
	private static PermissionHandler handler = null;
	/**
	 * PermissionHandlerセットアップ
	 */
	public static void setupPermissionHandler(){
		if (handler == null){
			handler = PermissionHandler.getInstance();
		}
		handler.setupPermissions(true);
	}
}
