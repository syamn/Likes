/**
 * Likes - Package: syam.likes.player
 * Created: 2012/10/10 16:43:35
 */
package syam.likes.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;

/**
 * PlayerProfile (PlayerProfile.java)
 * @author syam(syamn)
 */
public class PlayerProfile {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private String playerName;
	private boolean loaded = false;

	/* mySQL Stuff */
	private int playerID;

	/* flag */
	private boolean dirty;

	/* Data */
	private Byte status;
	private Long lastgivetime = 0L;

	private int like_give;
	private int like_receive;

	/**
	 * コンストラクタ
	 * @param playerName プレイヤー名
	 * @param addNew 新規プレイヤーとしてデータを読み込むかどうか
	 */
	public PlayerProfile(String playerName, boolean addNew){
		this.playerName = playerName;

		if (!loadMySQL() && addNew){
			addMySQLPlayer();
			loaded = true;
		}
	}

	/**
	 * データベースからプレイヤーデータを読み込み
	 * @return 正常終了すればtrue、基本データテーブルにデータがなければfalse
	 */
	public boolean loadMySQL(){
		Database db = LikesPlugin.getDatabases();

		// プレイヤーID(DB割り当て)を読み出す
		playerID = db.getInt("SELECT player_id FROM " + db.getTablePrefix() + "users WHERE player_name = ?", playerName);

		// プレイヤー基本テーブルにデータがなければ何もしない
		if (playerID == 0){
			return false;
		}

		/* *** profilesテーブルデータ読み込み *************** */
		HashMap<Integer, ArrayList<String>> profileDatas = db.read(
				"SELECT `status`, `like_give`, `like_receive`, `lastgivetime` FROM " + db.getTablePrefix() + "profiles WHERE player_id = ?", playerID);
		ArrayList<String> dataValues = profileDatas.get(1);

		if (dataValues == null){
			// 新規レコード追加
			log.warning(playerName + " does not exist in the profile table. Their profile will be reset.");
			db.write("INSERT INTO " + db.getTablePrefix() + "profile (player_id) VALUES (?)", playerID);
		}else{
			// データ読み出し
			this.status = Byte.valueOf(dataValues.get(0));
			this.like_give = Integer.valueOf(dataValues.get(1));
			this.like_receive = Integer.valueOf(dataValues.get(2));
			this.lastgivetime = Long.valueOf(dataValues.get(3));

			dataValues.clear();
		}

		// 読み込み正常終了
		loaded = true;
		dirty = false;
		return true;
	}

	/**
	 * 新規ユーザーデータをMySQLデータベースに追加
	 */
	private void addMySQLPlayer(){
		Database db = LikesPlugin.getDatabases();

		db.write("INSERT INTO " + db.getTablePrefix() + "users (player_name) VALUES (?)", playerName); // usersテーブル
		playerID = db.getInt("SELECT player_id FROM "+db.getTablePrefix() + "users WHERE player_name = ?", playerName);
		db.write("INSERT INTO " + db.getTablePrefix() + "profiles (player_id) VALUES (?)", playerID); // profilesテーブル
	}

	/**
	 * プレイヤーデータをMySQLデータベースに保存
	 */
	public void save(boolean force){
		if (dirty || force){
			Database db = LikesPlugin.getDatabases();

			// データベースupdate

			/* profilesテーブル */
			/*db.write("UPDATE " + db.getTablePrefix() + "profiles SET " +
					"`status` = " + this.status +
					", `like_give` = " + this.like_give +
					", `like_receive` = " + this.like_receive +
					", `lastgivetime` = " + this.lastgivetime.intValue() +
					" WHERE player_id = " + playerID);
			*/
			db.write("UPDATE " + db.getTablePrefix() + "profiles SET " +
					"`status` = ?" +
					", `like_give` = ?" +
					", `like_receive` = ?" +
					", `lastgivetime` = ?" +
					" WHERE player_id = ?",
					this.status.intValue(), this.like_give, this.like_receive, this.lastgivetime.intValue(),
					this.playerID);
			dirty = false;
		}
	}
	public void save(){
		save(false);
	}

	/* getter / setter */
	public int getPlayerID(){
		return playerID;
	}
	public String getPlayerName(){
		return playerName;
	}
	public boolean isLoaded(){
		return loaded;
	}

	/* Data */
	// status
	public void setStatus(final int status){
		this.status = (byte) status;
		this.dirty = true;
	}
	public int getStatus(){
		return this.status;
	}

	// likeGive
	public void setLikeGiveCount(final int count){
		this.like_give = count;
		this.dirty = true;
	}
	public void addLikeGiveCount(){
		this.like_give = this.like_give + 1;
		this.dirty = true;
	}
	public int getLikeGiveCount(){
		return this.like_give;
	}

	// likeReceive
	public void setLikeReceiveCount(final int count){
		this.like_receive = count;
		this.dirty = true;
	}
	public void addLikeReceiveCount(){
		this.like_receive = this.like_receive + 1;
		this.dirty = true;
	}
	public int getLikeReceiveCount(){
		return this.like_receive;
	}

	// lastGiveTime
	public void setLastGiveTime(long time){
		this.lastgivetime = time;
		this.dirty = true;
	}
	public void updateLastGiveTime(){
		this.lastgivetime = System.currentTimeMillis() / 1000;
		this.dirty = true;
	}
	public long getlastGiveTime(){
		return this.lastgivetime;
	}
}
