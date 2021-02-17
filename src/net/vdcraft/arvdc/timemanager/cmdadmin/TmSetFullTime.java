package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetFullTime extends MainTM {

	/**
	 * CMD /tm set elapsedDays [today|number] [world]
	 */
	public static void cmdSetDay(CommandSender sender, long elapsedDays, String world) {
		// If using a world name in several parts
		if (sender instanceof Player)
			world = ValuesConverter.restoreSpacesInString(world);
		// Adapt wrong values in the arg
		if (elapsedDays < 0 ) elapsedDays = 0L;

		// Modify all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetDay(sender, elapsedDays, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Modify targeted world's timer
			World w = Bukkit.getWorld(world);
			long t = w.getTime();
			long tickToSet = (elapsedDays * 24000) + t;
			if (elapsedDays > 0 && t > 18000 && MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase(CF_NEWDAYAT_0H00)) {
				tickToSet = tickToSet - 24000;
			}
			w.setFullTime(tickToSet);
			// Notifications
			if (elapsedDays == 0 && t > 18000 && MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase(CF_NEWDAYAT_0H00)) {
				String hour = ValuesConverter.formattedTimeFromTick(t);
				MsgHandler.infoMsg(tooLateForDayZeroMsg1 + hour + tooLateForDayZeroMsg2); // Console final msg (always)
				MsgHandler.playerMsg(sender, tooLateForDayZeroMsg1 + "§e" + hour + "§r" + tooLateForDayZeroMsg2); // Console final msg (always)
			}
			elapsedDays = ValuesConverter.elapsedDaysFromTick(w.getFullTime());
			String date = ValuesConverter.dateFromElapsedDays(elapsedDays, "yyyy") + "-" + ValuesConverter.dateFromElapsedDays(elapsedDays, "mm") + "-" + ValuesConverter.dateFromElapsedDays(elapsedDays, "dd");
			MsgHandler.infoMsg(worldFullTimeChgMsg + " " + world + " " + worldTimeChgMsg2 + " #" + elapsedDays + " (" + date + ")."); // Console final msg (always)
			MsgHandler.playerMsg(sender, worldFullTimeChgMsg + " §e" + world + "§r " + worldTimeChgMsg2 + " §e#" + elapsedDays + " §r(§e" + date + "§r)."); // Player final msg (in case)
		}
		// Else, return an error and help message
		else TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set elapsedDays");
	}
};