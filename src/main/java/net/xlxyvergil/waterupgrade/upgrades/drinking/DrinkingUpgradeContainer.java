package net.xlxyvergil.waterupgrade.upgrades.drinking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;

public class DrinkingUpgradeContainer extends UpgradeContainerBase<DrinkingUpgradeWrapper, DrinkingUpgradeContainer> {
	private static final String DATA_THIRST_LEVEL = "thirstLevel";
	private static final String DATA_DRINK_IMMEDIATELY_WHEN_HURT = "drinkImmediatelyWhenHurt";

	private final FilterLogicContainer<FilterLogic> filterLogicContainer;

	public DrinkingUpgradeContainer(Player player, int containerId, DrinkingUpgradeWrapper wrapper, UpgradeContainerType<DrinkingUpgradeWrapper, DrinkingUpgradeContainer> type) {
		super(player, containerId, wrapper, type);
		filterLogicContainer = new FilterLogicContainer<>(() -> upgradeWrapper.getFilterLogic(), this, slots::add);
	}

	@Override
	public void handleMessage(CompoundTag data) {
		if (data.contains(DATA_THIRST_LEVEL)) {
			setDrinkAtThirstLevel(ThirstLevel.fromName(data.getString(DATA_THIRST_LEVEL)));
		} else if (data.contains(DATA_DRINK_IMMEDIATELY_WHEN_HURT)) {
			setDrinkImmediatelyWhenHurt(data.getBoolean(DATA_DRINK_IMMEDIATELY_WHEN_HURT));
		}
		filterLogicContainer.handleMessage(data);
	}

	public FilterLogicContainer<FilterLogic> getFilterLogicContainer() {
		return filterLogicContainer;
	}

	public void setDrinkAtThirstLevel(ThirstLevel thirstLevel) {
		upgradeWrapper.setDrinkAtThirstLevel(thirstLevel);
		sendDataToServer(() -> NBTHelper.putEnumConstant(new CompoundTag(), DATA_THIRST_LEVEL, thirstLevel));
	}

	public ThirstLevel getDrinkAtThirstLevel() {
		return upgradeWrapper.getDrinkAtThirstLevel();
	}

	public void setDrinkImmediatelyWhenHurt(boolean drinkImmediatelyWhenHurt) {
		upgradeWrapper.setDrinkImmediatelyWhenHurt(drinkImmediatelyWhenHurt);
		sendBooleanToServer(DATA_DRINK_IMMEDIATELY_WHEN_HURT, drinkImmediatelyWhenHurt);
	}

	public boolean shouldDrinkImmediatelyWhenHurt() {
		return upgradeWrapper.shouldDrinkImmediatelyWhenHurt();
	}
}
