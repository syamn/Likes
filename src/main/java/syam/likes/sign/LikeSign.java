/**
 * Likes - Package: syam.likes.sign
 * Created: 2012/10/10 19:24:10
 */
package syam.likes.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import syam.likes.LikesPlugin;
import syam.likes.database.Database;
import syam.likes.exception.CommandException;
import syam.likes.exception.LikesPluginException;
import syam.likes.manager.PlayerManager;
import syam.likes.player.LPlayer;
import syam.likes.player.PlayerProfile;
import syam.likes.util.Actions;
import syam.likes.util.Util;

/**
 * LikeSign (LikeSign.java)
 * @author syam(syamn)
 */
public class LikeSign {
	// Logger
	private static final Logger log = LikesPlugin.log;
	private static final String logPrefix = LikesPlugin.logPrefix;
	private static final String msgPrefix = LikesPlugin.msgPrefix;

	private final LikesPlugin plugin;
	private int signID;
	private Location loc;
	private String sign_name;
	private String creator;
	private String text = null;
	private int liked;
	private Long lastliked;
	private Long created;
	private int status;

	private boolean dirty = false;

	/**
	 * コンストラクタ
	 * @param plugin
	 * @param sign
	 */
	public LikeSign(final int signID, final String signName, final String playerName, final int status, final String text, final int liked, final long lastliked, final long created, final Location location){
		this.plugin = LikesPlugin.getInstance();

		this.signID = signID;
		this.sign_name = signName;
		this.creator = playerName;
		this.status = status;
		this.text = text;
		this.liked = liked;
		this.lastliked = lastliked;
		//this.created = (signID < 1) ? System.currentTimeMillis() / 1000 : created;
		this.created = created;
		this.loc = location;
	}

	public void save(boolean force){
		if (dirty || force){
			// Get Creator ID
			PlayerProfile prof = new PlayerProfile(creator, false);
			if (!prof.isLoaded() || prof.getPlayerID() == 0){
				throw new LikesPluginException("This player records does not exist! creator="+creator);
			}
			final int playerID = prof.getPlayerID();

			boolean addNew = (signID < 1) ? true : false;
			String signIDstr = (addNew) ? "null" : String.valueOf(this.signID);

			// Get DataBase
			Database database = LikesPlugin.getDatabases();
			final String tablePrefix = LikesPlugin.getInstance().getConfigs().getMySQLtablePrefix();

			// UPDATE
			database.write("REPLACE INTO " + tablePrefix + "signs VALUES " +
					"(" +
					signIDstr + ", " +
					"'" + this.sign_name + "', " +
					playerID + ", " +
					this.status + ", " +
					"'" + this.text + "', " +
					this.liked + ", " +
					this.lastliked.intValue() + ", " +
					this.created.intValue() + ", " +
					"'" + loc.getWorld().getName() + "', " +
					loc.getBlockX() + ", " +
					loc.getBlockY() + ", " +
					loc.getBlockZ() +
					")");

			// Get new signID
			if (addNew){
				this.signID = database.getInt("SELECT `sign_id` FROM "+tablePrefix + "signs WHERE `player_id` = " + playerID + " AND `sign_name` = '" + this.sign_name + "'");
				if (signID == 0){
					throw new LikesPluginException("Could not insert to " + tablePrefix + "signs table properly!");
				}
			}
		}
	}
	public void save(){
		save(false);
	}

	public List<String> getInformation(){
		List<String> ret = new ArrayList<String>();

		ret.add("&b建築物ID:&6 " + getUniqueID() + " &b建築者:&6 " + creator);
		ret.add("&b設置日:&6 " + Util.getDispTimeByUnixTime(this.created));
		ret.add("&b建築物紹介:&6 " + ((this.text == null || this.text.length() == 0) ? "&7(なし)" : this.text));
		ret.add("&bお気に入り登録ユーザー数:&6 " + this.liked);

		return ret;
	}

	public void updateSign(){
		Sign sign = Actions.getSign(this.loc);
		if (sign == null){
			log.warning(logPrefix+ "Could not update sign at " + Actions.getBlockLocationString(this.loc));
			return;
		}

		sign.setLine(0, "§a[Likes]");
		sign.setLine(1, String.valueOf(this.liked));
		sign.setLine(2, this.sign_name);
		sign.setLine(3, (this.creator.length() > 15) ? this.creator.substring(0, 13) + ".." : this.creator);

		sign.update();
	}

	public boolean addLike(final Player player, String text){
		if (player == null) throw new IllegalArgumentException("Player could not be null!");
		if (this.signID <= 0) {
			this.save(true);
		}

		// Get Profile
		PlayerProfile prof = PlayerManager.getProfile(player.getName());
		if (prof == null || !prof.isLoaded()){
			throw new IllegalArgumentException("Player profile does not found!");
		}
		final int playerID = prof.getPlayerID();
		final int timestamp = Util.getCurrentUnixSec().intValue();

		// Like!
		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = LikesPlugin.getInstance().getConfigs().getMySQLtablePrefix();

		boolean result = database.write("INSERT INTO " + tablePrefix + "likes VALUES " +
					"(null, " + playerID + ", " + this.signID + ", '" + text + "', " + timestamp + ")");

		if (result){
			// プロフィール更新
			prof.addLikeGiveCount();
			prof.setLastGiveTime(timestamp);

			// 看板データ更新
			this.addLiked();
			this.updateSign();

			// 所有者プロフィール更新
			LPlayer cLPlayer = PlayerManager.getPlayer(this.creator);
			PlayerProfile cProf = (cLPlayer != null) ? cLPlayer.getProfile() : new PlayerProfile(this.creator, false);
			if (cProf == null || !cProf.isLoaded()){
				throw new LikesPluginException("Creator profile does not exist! Creator=" + this.creator);
			}
			cProf.addLikeReceiveCount();

			// Save
			prof.save();
			cProf.save();
			this.save();

			return true;
		}else{
			return false;
		}
	}

	public boolean isAlreadyLiked(final Player player){
		if (player == null) throw new IllegalArgumentException("Player could not be null!");

		if (this.signID <= 0){
			this.save(true);
		}

		// Get Profile
		PlayerProfile prof = PlayerManager.getProfile(player.getName());
		if (prof == null || !prof.isLoaded()){
			throw new IllegalArgumentException("Player profile does not found!");
		}
		final int playerID = prof.getPlayerID();

		// Get DataBase
		Database database = LikesPlugin.getDatabases();
		final String tablePrefix = LikesPlugin.getInstance().getConfigs().getMySQLtablePrefix();

		// Get Result
		HashMap<Integer, ArrayList<String>> result = database.read("SELECT `like_id` FROM " + tablePrefix + "likes WHERE `player_id` = " + playerID + " AND `sign_id` = " + this.signID);
		if (result.size() > 0){
			return true;
		}else{
			return false;
		}
	}

	/* getter/setter */
	public int getSignID(){
		return this.signID;
	}
	public Location getLocation(){
		return this.loc;
	}
	public String getUniqueID(){
		return creator + "." + this.sign_name;
	}

	public String getName(){
		return this.sign_name;
	}
	public void setName(String name){
		this.sign_name = name;
		this.dirty = true;
	}

	public String getText(){
		return this.text;
	}
	public void setText(String text){
		this.text = text;
		this.dirty = true;
	}

	public int getLiked(){
		return this.liked;
	}
	public void setLiked(int liked){
		this.liked = liked;
		this.dirty = true;
	}
	public void addLiked(){
		this.liked = this.liked + 1;
		this.dirty = true;
	}

	public int getStatus(){
		return this.status;
	}
	public void setStatus(int status){
		if (status <= -100 || status >= 100) return;
		this.status = status;
		this.dirty = true;
	}
}
