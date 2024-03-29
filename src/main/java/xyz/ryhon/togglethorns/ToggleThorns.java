package xyz.ryhon.togglethorns;

import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public class ToggleThorns extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		PaperLib.suggestPaper(this);

		getServer().getPluginManager().registerEvents(this, this);

		getCommand("togglethorns").setExecutor(new CommandExecutor() {
			public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
					@NotNull String[] args) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("This command may only be ran by players!");
					return false;
				}

				Player p = (Player) sender;
				toggleThornsDisabled(p);
				if (isThornsDisabled(p))
				{
					p.playSound(p.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_OUT, 1f, 1f);
					p.sendMessage("Thorns is now " + ChatColor.RED + "DISABLED");
				}
				else
				{
					p.playSound(p.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 1f, 1f);
					p.sendMessage("Thorns is now " + ChatColor.GREEN + "ENABLED");
				}

				return true;
			}
		});
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent e)
	{
		if (e.getCause() != DamageCause.THORNS)
			return;

		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (isThornsDisabled(p)) {
				p.sendActionBar(ChatColor.DARK_GRAY + "/tth - Thorns damage prevented");
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void on(PlayerItemDamageEvent e)
	{
		if(e.getItem().getEnchantmentLevel(Enchantment.THORNS) != 0)
		{
			if(isThornsDisabled(e.getPlayer()))
			{
				// Thorns reduces durability by additional 2 points, separately from damage caused by taking damage
				if(e.getDamage() == 2)
				{
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	static NamespacedKey ThornsDisabledKey = new NamespacedKey("togglethorns", "disabled");

	boolean isThornsDisabled(Player p) {
		PersistentDataContainer psc = p.getPersistentDataContainer();
		if (!psc.has(ThornsDisabledKey, PersistentDataType.BYTE))
			return false;

		return psc.get(ThornsDisabledKey, PersistentDataType.BYTE) != 0;
	}

	void setThornsDisabled(Player p, boolean disabled) {
		PersistentDataContainer psc = p.getPersistentDataContainer();
		psc.set(ThornsDisabledKey, PersistentDataType.BYTE, disabled ? (byte) 1 : (byte) 0);
	}

	void toggleThornsDisabled(Player p) {
		setThornsDisabled(p, !isThornsDisabled(p));
	}
}
