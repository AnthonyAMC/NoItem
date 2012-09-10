package net.worldoftomorrow.nala.ni.CustomItems;

import java.util.List;

public class CustomFurnace extends CustomBlock {

	private final List<Short> resultSlots;
	private final List<Short> fuelSlots;
	private final List<Short> itemSlots;
	private final boolean usesFuel;

	// TODO: add an option for if it requires fuel or uses power
	public CustomFurnace(int id, short data, CustomType type,
			List<Short> resultSlots, List<Short> fuelSlots, List<Short> itemSlots,
			String name, boolean usesFuel) {
		super(id, data, type, name);
		this.resultSlots = resultSlots;
		this.fuelSlots = fuelSlots;
		this.itemSlots = itemSlots;
		this.usesFuel = usesFuel;
	}

	public List<Short> getResultSlots() {
		return this.resultSlots;
	}

	public List<Short> getFuelSlots() {
		return this.fuelSlots;
	}

	public List<Short> getItemSlots() {
		return this.itemSlots;
	}

	public boolean isResultSlot(short slot) {
		for (short s : this.resultSlots) {
			if (s == slot) {
				return true;
			}
		}
		return false;
	}

	public boolean isFuelSlot(short slot) {
		for (short s : this.fuelSlots) {
			if (s == slot) {
				return true;
			}
		}
		return false;
	}

	public boolean isItemSlot(short slot) {
		for (short s : this.itemSlots) {
			if (s == slot) {
				return true;
			}
		}
		return false;
	}

	public boolean usesFuel() {
		return this.usesFuel;
	}
}
