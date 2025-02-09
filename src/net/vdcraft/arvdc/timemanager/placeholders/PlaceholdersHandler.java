package net.vdcraft.arvdc.timemanager.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class PlaceholdersHandler extends MainTM {

	/**
	 * Replaces any available placeholder by the corresponding String
	 * (returns a String)
	 */
	public static String replacePlaceholder(String placeholder, String world, String lang, Player p) {
		
		World w = Bukkit.getServer().getWorld(world);
		Long t = w.getTime();
		Long ft = w.getFullTime();
		Long ed = ValuesConverter.daysFromTick(ft);

		if (p != null) {
			long o = p.getPlayerTimeOffset();
			if (o != 0) {
				ft = p.getPlayerTime();
				t = ValuesConverter.correctDailyTicks(ft);
				ed = ValuesConverter.daysFromTick(ft);
				MsgHandler.debugMsg("Player §e" + p.getName() + "§b has a time offset of §e" + o + "§b ticks."); // Console debug msg
			}
		}
		
		switch (placeholder) {
		
		// Returns the current player's name
		case "{" + PH_PREFIX + PH_PLAYER + "}" :
			if (p != null) return p.getName();
		
		// Returns the current world's name
		case "{" + PH_PREFIX + PH_WORLD + "}" :
			return world;

		// Returns the current tick for the player's world
		case "{" + PH_PREFIX + PH_TICK + "}" :
			String tick = t.toString();
			return tick;

		// Returns the current time (in hh:mm:ss) for the player's world
		case "{" + PH_PREFIX + PH_TIME12 + "}" :
			String time12 = ValuesConverter.formattedTimeFromTick(t, PH_TIME12, true);
			return time12;
			
		// Returns the current time (in HH:mm:ss) for the player's world
		case "{" + PH_PREFIX + PH_TIME24 + "}" :
			String time24 = ValuesConverter.formattedTimeFromTick(t, PH_TIME24, true);
			return time24;

		// Returns the current hours (in hh) for the player's world
		case "{" + PH_PREFIX + PH_HOURS12 + "}" :
			String hh12 = ValuesConverter.formattedTimeFromTick(t, PH_HOURS12, true);
			return hh12;

		// Returns the current hours (in HH) for the player's world
		case "{" + PH_PREFIX + PH_HOURS24 + "}" :
			String hh24 = ValuesConverter.formattedTimeFromTick(t, PH_HOURS24, true);
			return hh24;

		// Returns the current minutes (in mm) for the player's world
		case "{" + PH_PREFIX + PH_MINUTES + "}" :
			String minutes = ValuesConverter.formattedTimeFromTick(t, PH_MINUTES, true);
			return minutes;

		// Returns the current seconds (in ss) for the player's world
		case "{" + PH_PREFIX + PH_SECONDS + "}" :
			String seconds = ValuesConverter.formattedTimeFromTick(t, PH_SECONDS, true);
			return seconds;

		// Returns the part of day (AM or PM) for the player's world
		case "{" + PH_PREFIX + PH_AMPM + "}" :
			String ampm = ValuesConverter.getAmPm(t);
			return ampm;

		// Returns the current day part for the player's world
		case "{" + PH_PREFIX + PH_DAYPART + "}" :
			String dayPart = ValuesConverter.getMCDayPart(t);
			return MainTM.getInstance().langConf.getString(LG_LANGUAGES + "." + lang + "." + LG_DAYPARTS + "." + dayPart);

		// Returns the current day # for the player's world
		case "{" + PH_PREFIX + PH_C_DAY + "}" :
			Long currentDay = ValuesConverter.daysFromTick(ft);
			return (++currentDay).toString();

		// Returns the total # of days elapsed for the player's world
		case "{" + PH_PREFIX + PH_E_DAYS + "}" :
			return ValuesConverter.daysFromTick(ft).toString();

		// Returns the name of the current day for the player's world
		case "{" + PH_PREFIX + PH_DAYNAME + "}" :
			String dayName = ValuesConverter.dayName(ft, lang);
			return dayName;

		// Returns the number of the current day in the year for the player's world
		case "{" + PH_PREFIX + PH_YEARDAY + "}" :
			Long yDay = ValuesConverter.dayInYear(ft);
			return yDay.toString();

		// Returns the number of the current week in the year for the player's world
		case "{" + PH_PREFIX + PH_YEARWEEK + "}" :
			Long yWeek = ValuesConverter.weekInYear(ft);
			return yWeek.toString();

		// Returns the number of elapsed weeks for the player's world
		case "{" + PH_PREFIX + PH_WEEK + "}" :
			Long week = ValuesConverter.weeksFromTick(ft);
			return week.toString();

		// Returns the current month's name for the player's world
		case "{" + PH_PREFIX + PH_MONTHNAME + "}" :
			String mm = "m" + ValuesConverter.dateFromElapsedDays(ed, "mm");
			return MainTM.getInstance().langConf.getString(LG_LANGUAGES + "." + lang + "." + LG_MONTHS + "." + mm);

		// Returns the current day in (00 format) for the player's world
		case "{" + PH_PREFIX + PH_DD + "}" :
			return ValuesConverter.dateFromElapsedDays(ed, "dd");

		// Returns the current month in (00 format) for the player's world
		case "{" + PH_PREFIX + PH_MM + "}" :
			return ValuesConverter.dateFromElapsedDays(ed, "mm");

		// Returns the current year in (00 format) for the player's world
		case "{" + PH_PREFIX + PH_YY + "}" :
			return ValuesConverter.dateFromElapsedDays(ed, "yy");

		// Returns the current year in (0000 format) for the player's world
		case "{" + PH_PREFIX + PH_YYYY + "}" :
			return ValuesConverter.dateFromElapsedDays(ed, "yyyy");

		default :
			return placeholder;
		}
	}

	/**
	 * Replaces all placeholders found in a String
	 * (returns a String)
	 */
	public static String replaceAllPlaceholders(String msg, String world, String lang, Player p) {
		if (msg.contains("{tm_")) {
			String[] phSlipt1 = msg.split("\\{");
			for (String ph1 : phSlipt1) {
				if (ph1.contains("}")) {
					String[] phSlipt2 = ph1.split("\\}");
					for (String ph2 : phSlipt2) {
						if (ph2.contains("tm_")) {
							ph2 = "{" + ph2 + "}";
							String ph3 = PlaceholdersHandler.replacePlaceholder(ph2, world, lang, p);
							MsgHandler.devMsg("A placeholder was detected : \"§e" + ph2 + "§9\" will be changed by \"§e" + ph3 + "§9\".");  // Console dev msg
							msg = msg.replace(ph2, ph3);
						}
					}
				}
			}
		}
		return msg;
	}

	/**
	 * Replaces all placeholders found in a String
	 * (returns a String)
	 */
	public static String replaceAllPlaceholders(String msg, String world, String lang) {
		return replaceAllPlaceholders(msg, world, lang, null);		
	}

	/**
	 * Replaces all placeholders found in a String
	 * (returns a String)
	 */
	public static String replaceAllPlaceholders(String msg, World w, String lang, Player p) {
		return replaceAllPlaceholders(msg, w.getName(), lang, p);		
	}

	/**
	 * Replaces all placeholders found in a String
	 * (returns a String)
	 */
	public static String replaceAllPlaceholders(String msg, World w, String lang) {
		return replaceAllPlaceholders(msg, w.getName(), lang, null);		
	}
};