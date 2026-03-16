package mcbase.extended.sbw;

import com.atsuishio.superbwarfare.data.gun.DefaultGunData;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.item.HandGrenade;
import com.atsuishio.superbwarfare.item.gun.GunItem;

import mcbase.registry.Registers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SbwWeapons {
	public static final boolean isGun(ItemStack item) {
		return item.getItem() instanceof GunItem;
	}

	public static final boolean isGrenade(ItemStack item) {
		return item.getItem() instanceof HandGrenade;
	}

	public static final boolean isVehicle(Entity entity) {
		return entity instanceof VehicleEntity;
	}

	public static final GunData getGunData(ItemStack item) {
		return GunData.from(item);
	}

	public static final DefaultGunData getDefaultGunData(GunItem gun) {
		return GunData.getDefault(gun);
	}

	/**
	 * 返回枪械
	 * 
	 * @param item
	 * @return
	 */
	public static final GunItem getGunItem(ItemStack item) {
		return item.getItem() instanceof GunItem gunItem ? gunItem : null;
	}

	public static final ItemStack getGun(GunItem gun, int ammoCount) {
		ItemStack gunItem = gun.getDefaultInstance();
		GunData data = GunData.from(gunItem);
		data.ammo.set(ammoCount);
		return gunItem;
	}

	public static final ItemStack getGun(ResourceLocation gunId, int ammoCount) {
		return getGun((GunItem) Registers.item(gunId), ammoCount);
	}

	public static final ItemStack getGun(ResourceLocation gunId) {
		GunItem gun = (GunItem) Registers.item(gunId);
		DefaultGunData data = getDefaultGunData(gun);
		return getGun(gun, data.magazine);
	}

	public static final ItemStack getGun(String gunId) {
		return getGun(ResourceLocation.parse(gunId));
	}

	/**
	 * 获取指定的枪械的子弹数
	 * 
	 * @param gun
	 * @return
	 */
	public static final int getGunAmmoCount(GunItem gun) {
		return getDefaultGunData(gun).magazine;
	}

	public static final int getGunAmmoCount(ItemStack gunItem) {
		return gunItem.getItem() instanceof GunItem gun ? getGunAmmoCount(gun) : 0;
	}

	/**
	 * 隐藏载具上的实体
	 * 
	 * @param entity
	 * @return
	 */
	public static final boolean hideVehiclePassenger(LivingEntity entity) {
		if (entity.getVehicle() instanceof VehicleEntity vehicle) {
			return vehicle.hidePassenger(entity);
		}
		return false;
	}
}
