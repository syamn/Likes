/**
 * Likes - Package: syam.likes
 * Created: 2012/09/30 19:21:53
 */
package syam.likes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import syam.likes.command.BaseCommand;
import syam.likes.command.ConfirmCommand;
import syam.likes.command.CreateCommand;
import syam.likes.command.DeleteCommand;
import syam.likes.command.HelpCommand;
import syam.likes.command.LikeCommand;
import syam.likes.command.ReloadCommand;
import syam.likes.command.StatsCommand;
import syam.likes.command.queue.ConfirmQueue;
import syam.likes.database.Database;
import syam.likes.listener.BlockListener;
import syam.likes.listener.PlayerListener;
import syam.likes.listener.ServerListener;
import syam.likes.listener.SignProtectListener;
import syam.likes.manager.PlayerManager;
import syam.likes.manager.SignManager;
import syam.likes.permission.Perms;
import syam.likes.util.Metrics;

/**
 * LikesPlugin (LikesPlugin.java)
 * @author syam(syamn)
 */
public class LikesPlugin extends JavaPlugin{
	// ** Logger **
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[Likes] ";
	public final static String msgPrefix = "&6[Likes] &f";

	// ** Listener **
	ServerListener serverListener = new ServerListener(this);
	BlockListener blockListener = new BlockListener(this);
	PlayerListener playerListener = new PlayerListener(this);
	SignProtectListener protectListener = new SignProtectListener(this);

	// ** Commands **
	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

	// ** Private Classes **
	private ConfigurationManager config;
	private ConfirmQueue queue;

	// ** Static Variable **
	private static Database database;

	// ** Instance **
	private static LikesPlugin instance;

	// ** Hookup Plugins **
	private static Vault vault = null;
	private static Economy economy = null;

	/**
	 * プラグイン起動処理
	 */
	@Override
	public void onEnable(){
		instance  = this;
		PluginManager pm = getServer().getPluginManager();
		config = new ConfigurationManager(this);

		// loadconfig
		try{
			config.loadConfig(true);
		}catch (Exception ex){
			log.warning(logPrefix+"an error occured while trying to load the config file.");
			ex.printStackTrace();
		}

		// プラグインフック
		if (config.getUseVault()){
			config.setUseVault(setupVault());
		}

		// プラグインを無効にした場合進まないようにする
		if (!pm.isPluginEnabled(this)){
			return;
		}

		// 権限ハンドラセットアップ
		Perms.setupPermissionHandler();

		// Regist Listeners
		pm.registerEvents(serverListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(protectListener, this);

		// コマンド登録
		registerCommands();
		queue = new ConfirmQueue(this);

		// データベース接続
		database = new Database(this);
		database.createStructure();

		// マネージャ
		log.info(logPrefix + SignManager.loadSigns() + " like sign(s) loaded!"); // DB使用

		// プレイヤー追加
		for (Player player : getServer().getOnlinePlayers()){
			PlayerManager.addPlayer(player);
		}

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is enabled!");

		setupMetrics(); // mcstats
	}

	/**
	 * プラグイン停止処理
	 */
	@Override
	public void onDisable(){
		int i = PlayerManager.saveAll();
		log.info(logPrefix + i + " player(s) saved!");

		i = SignManager.saveAll();
		log.info(logPrefix + i + " sign(s) saved!");

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is disabled!");
	}

	/**
	 * コマンドを登録
	 */
	private void registerCommands(){
		// Intro Commands
		commands.add(new HelpCommand());
		commands.add(new ConfirmCommand());

		// General Commands
		commands.add(new LikeCommand());
		commands.add(new StatsCommand());
		commands.add(new CreateCommand());
		commands.add(new DeleteCommand());

		// Admin Commands
		commands.add(new ReloadCommand());
	}

	/**
	 * Vaultプラグインにフック
	 */
	public boolean setupVault(){
		Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
		if(plugin != null & plugin instanceof Vault) {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			// 経済概念のプラグインがロードされているかチェック
			if(economyProvider==null){
	        	log.warning(logPrefix+"Economy plugin NOT found. Disabled Vault plugin integration.");
		        return false;
			}

			try{
				vault = (Vault) plugin;
				economy = economyProvider.getProvider();

				if (vault == null || economy == null){
				    throw new NullPointerException();
				}
			} // 例外チェック
			catch(Exception e){
				log.warning(logPrefix+"Could NOT be hook to Vault plugin. Disabled Vault plugin integration.");
		        return false;
			}

			// Success
			log.info(logPrefix+"Hooked to Vault plugin!");
			return true;
		}
		else {
			// Vaultが見つからなかった
	        log.warning(logPrefix+"Vault plugin was NOT found! Disabled Vault integration.");
	        return false;
	    }
	}

	/**
     * Metricsセットアップ
     */
    private void setupMetrics(){
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            log.warning(logPrefix+"cant send metrics data!");
            ex.printStackTrace();
        }
    }

	/**
	 * コマンドが呼ばれた
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
		if (cmd.getName().equalsIgnoreCase("likes")){
			if(args.length == 0){
				// 引数ゼロはヘルプ表示
				args = new String[]{"help"};
			}

			outer:
			for (BaseCommand command : commands.toArray(new BaseCommand[0])){
				String[] cmds = command.name.split(" ");
				for (int i = 0; i < cmds.length; i++){
					if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])){
						continue outer;
					}
					// 実行
					return command.run(this, sender, args, commandLabel);
				}
			}
			// 有効コマンドなし ヘルプ表示
			new HelpCommand().run(this, sender, args, commandLabel);
			return true;
		}
		return false;
	}

	/* getter */
	/**
	 * コマンドを返す
	 * @return List<BaseCommand>
	 */
	public List<BaseCommand> getCommands(){
		return commands;
	}

	/**
	 * Confirmコマンドキューを返す
	 * @return ConfirmQueue
	 */
	public ConfirmQueue getQueue(){
		return queue;
	}

	/**
	 * Vaultを返す
	 * @return Vault
	 */
	public Vault getVault(){
		return vault;
	}

	/**
	 * Economyを返す
	 * @return Economy
	 */
	public Economy getEconomy(){
		return economy;
	}

	/**
	 * データベースを返す
	 * @return Database
	 */
	public static Database getDatabases(){
		return database;
	}

	/**
	 * 設定マネージャを返す
	 * @return ConfigurationManager
	 */
	public ConfigurationManager getConfigs() {
		return config;
	}

	/**
	 * インスタンスを返す
	 * @return Likesインスタンス
	 */
	public static LikesPlugin getInstance(){
		return instance;
	}
}