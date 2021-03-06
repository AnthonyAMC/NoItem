
/* Copyright (c) 2013, Worldoftomorrow.net
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package net.worldoftomorrow.noitem.features.defaults;

import net.worldoftomorrow.noitem.NoItem;
import net.worldoftomorrow.noitem.features.NIFeature;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Use extends NIFeature {

	public Use() {
		super("Use", "You are not allowed to use %i!", true);
		// TODO Test feature
	}

	@EventHandler
	public void handlePlayerDamageEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		Entity e = event.getDamager();
		if (e instanceof Player) {
			Player p = (Player) e;
			ItemStack inHand = p.getItemInHand();
			if (NoItem.getLists().isTool(inHand) && NoItem.getPM().has(p, this, inHand)) {
				event.setCancelled(true);
				this.doNotify(p, inHand);
			}
		}
	}

	@EventHandler
	public void handlePlayerShearEntity(PlayerShearEntityEvent event) {
		if (event.isCancelled()) return;
		Player p = event.getPlayer();
		ItemStack inHand = p.getItemInHand();
		if (NoItem.getLists().getTools().isShear(inHand) && NoItem.getPM().has(p, this, inHand)) {
			event.setCancelled(true);
			this.doNotify(p, inHand);
		}
	}

	@EventHandler
	public void handleUseInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		Player p = event.getPlayer();
		ItemStack inHand = p.getItemInHand();
		if (NoItem.getLists().isTool(inHand) && NoItem.getPM().has(p, this, inHand)) {
			event.setCancelled(true);
			this.doNotify(p, inHand);
		}
	}
}
