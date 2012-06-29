package net.worldoftomorrow.nala.ni;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum Perms {
	ADMIN("noitem.admin"),
	ALLITEMS("noitem.allitems"),
	NOCRAFT("noitem.nocraft."),
	NOPICKUP("noitem.nopickup."),
	NODROP("noitem.nodrop."),
	NOBREW("noitem.nobrew."),
	NOUSE("noitem.nouse."),
	NOHOLD("noitem.nohold."),
	NOWEAR("noitem.nowear."),
	NOCOOK("noitem.nocook.");

	private final String perm;

	private Perms(String perm) {
		this.perm = perm;
	}

	public String getPerm() {
		return this.perm;
	}

	public boolean has(Player p) {
		if (perm.equalsIgnoreCase(Perms.ALLITEMS.getPerm())
				|| perm.equalsIgnoreCase(Perms.ADMIN.getPerm())) {
			if (p.isOp())
				return true;
		}
		return this.check(p, this.perm);
	}

	public boolean has(Player p, ItemStack stack) {
		if (Perms.ALLITEMS.has(p))
			return false;
		if (stack != null) {
			int id = stack.getTypeId();
			int data = stack.getDurability();
			if (perm.equalsIgnoreCase(Perms.NOCOOK.getPerm())
					|| perm.equalsIgnoreCase(Perms.NOCRAFT.getPerm())
					|| perm.equalsIgnoreCase(Perms.NODROP.getPerm())
					|| perm.equalsIgnoreCase(Perms.NOHOLD.getPerm())
					|| perm.equalsIgnoreCase(Perms.NOPICKUP.getPerm())
					|| perm.equalsIgnoreCase(Perms.NOUSE.getPerm())
					|| perm.equalsIgnoreCase(Perms.NOWEAR.getPerm())) {
				String numPerm = perm + id;
				numPerm = data != 0 ? numPerm + "." + data : numPerm;
				if (this.check(p, numPerm)) {
					return true;
				}
				String namePerm = perm + this.getItemName(id);
				namePerm = data != 0 ? namePerm + "." + data : namePerm;
				if (this.check(p, namePerm)) {
					return true;
				}
				return false;
			} else {
				Log.severe("Something tried to check for an invalid permission. \n Report this to the author please!");
				return true;
			}
		} else {
			Log.severe("Something tried to check for a permission with a null stack. \n Report this to the author please!");
			return true;
		}
	}

	public boolean has(Player p, int data, int ingredient) {
		if (Perms.ALLITEMS.has(p))
			return false;
		if (perm.equalsIgnoreCase(Perms.NOBREW.getPerm())) {
			return this.check(p, perm + data + "." + ingredient);
		}
		Log.severe("Something tried to check an item using the brewing perm checker. \n Report this to the author please!");
		return true;
	}

	public boolean has(Player p, int id) {
		if (Perms.ALLITEMS.has(p))
			return false;
		if (perm.equalsIgnoreCase(Perms.NOWEAR.getPerm())) {
			if (this.check(p, perm + id))
				return true;
			return this.check(p, perm + this.getItemName(id));
		}
		return true;
	}

	private boolean check(Player p, String permission) {
		if (Vault.vaultPerms) {
			return Vault.has(p, permission);
		} else {
			return p.hasPermission(permission);
		}
	}

	private String getItemName(int id) {
		if (Tools.isTool(id))
			return Tools.getTool(id).getName();
		if (Armor.isArmor(id))
			return Armor.getArmour(id).getName();
		if (Cookable.isCookable(id))
			return Cookable.getItem(id).getName();
		if (TekkitTools.isTekkitTool(id))
			return TekkitTools.getTool(id).getName();
		return Integer.toString(id);
	}
}
