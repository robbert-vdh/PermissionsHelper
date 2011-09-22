package me.coolblinger.permissionshelper;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import javax.print.DocFlavor;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PermissionsHelper extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {

	}

	public void onEnable() {
		PluginDescriptionFile pdFile = this.getDescription();
		log.info(pdFile.getName() + " version " + pdFile.getVersion() + " loaded!");
		log.info(pdFile.getName() + " will generate a permissions list in approximately thirty seconds.");
		BukkitScheduler bScheduler = this.getServer().getScheduler();
		bScheduler.scheduleAsyncDelayedTask(this, new Runnable() {
			public void run() {
				generatePermissionsList();
			}
		}, 600);
	}

	@SuppressWarnings({"ResultOfMethodCallIgnored"})
	public void generatePermissionsList () {
		log.info("PermissionsHelper will now generate a list of permissions and write them to plugins/PermissionsHelper/list.txt");
		PluginManager pm = this.getServer().getPluginManager();
		Plugin[] plugins = pm.getPlugins();
		List<String> permissionsList = new ArrayList<String>();
		List<String> skippedList = new ArrayList<String>();
		for (Plugin plugin:plugins) {
			if (plugin.getDescription().getPermissions().isEmpty()) {
				skippedList.add("#" + plugin.getDescription().getName());
				continue;
			}
			for (Permission permission:plugin.getDescription().getPermissions()) {
				permissionsList.add("- " + permission.getName() + ": true #" + plugin.getDescription().getName());
			}
		}
		File file = new File("plugins" + File.separator + "PermissionsHelper" + File.separator + "list.txt");
		try {
			file.getParentFile().mkdir();
			file.createNewFile();
			try {
				PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
				for (String s:permissionsList) {
					printWriter.write(s);
					printWriter.println();
				}
				if (!skippedList.isEmpty()) {
					printWriter.println();
					printWriter.println();
					printWriter.write("#Plugins that don't have any included permissions nodes:");
					printWriter.println();
					for (String s:skippedList) {
						printWriter.write(s);
						printWriter.println();
					}
				}
				printWriter.close();
				log.info("Writing has finished.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.warning("Writing has failed!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("Writing has failed!");
		}
	}
}
