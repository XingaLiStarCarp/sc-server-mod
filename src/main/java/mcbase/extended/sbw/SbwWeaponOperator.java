package mcbase.extended.sbw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidAnimationMessage;

import mcbase.LogicalEnd;
import mcbase.extended.gun.GunOperator;
import mcbase.extended.tlm.entity.maid.ProxyRenderMaid.ProxyRenderMaidProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE)
public class SbwWeaponOperator implements GunOperator {
	protected final LivingEntity entity;
	protected InteractionHand hand;
	protected boolean isAim;

	protected boolean needCheckAmmo;

	private static final ArrayList<SbwWeaponOperator> GUN_TICK_LIST = new ArrayList<>();

	/**
	 * 需要手动tick枪械
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Iterator<SbwWeaponOperator> iter = GUN_TICK_LIST.iterator();
			while (iter.hasNext()) {
				SbwWeaponOperator op = iter.next();
				if (op.getGunHolder().isRemoved()) {
					iter.remove();
				} else {
					op.tick();
				}
			}
		}
	}

	/**
	 * 需要手动调用tick()更新
	 */
	public final void tick() {
		ItemStack gunItem = getGunItem();
		if (SbwWeapons.isGun(gunItem)) {
			SbwWeapons.getGunData(gunItem).tick(this.entity, this.hand == InteractionHand.MAIN_HAND);
		}
	}

	public SbwWeaponOperator(LivingEntity entity, InteractionHand hand) {
		this.entity = entity;
		this.hand = hand;
		GUN_TICK_LIST.add(this);// 需要手动tick
	}

	public SbwWeaponOperator(LivingEntity shooter) {
		this(shooter, InteractionHand.MAIN_HAND);
	}

	@Override
	public InteractionHand getGunHand() {
		return this.hand;
	}

	@Override
	public LivingEntity getGunHolder() {
		return this.entity;
	}

	@Override
	public final void aim(boolean isAim) {
		this.isAim = isAim;
	}

	@Override
	public final void bolt() {
		GunData gunData = getGunData();
		if (gunData.shouldStartBolt()) {
			gunData.startBolt();
		}
	}

	@Override
	public final void reload() {
		GunData gunData = this.getGunData();
		if (this.needCheckAmmo) {
			gunData.virtualAmmo.set(0);
		} else {
			// virtualAmmo为虚拟弹药数，它直接储存一个int数值，不需要从任何实体或背包消耗弹药。换弹时优先消耗虚拟弹药数，消耗完后再消耗背包弹药。
			// 如果不检查弹药，则每次换弹前都先设置足够大数量的弹药
			gunData.virtualAmmo.set(Integer.MAX_VALUE);
		}
		// shouldStartReloading()必须要虚拟弹药+背包弹药>0才能返回true
		if (gunData.shouldStartReloading(this.entity)) {
			gunData.startReload();
			if (this.entity.level() instanceof ClientLevel && this.entity instanceof ProxyRenderMaidProvider maidProvider) {
				maidProvider.proxyRenderMaid().playAnimation(MaidAnimationMessage.SWF_RELOAD);
			}
		}
	}

	@Override
	public final int getGunAmmo() {
		return getGunData().ammo.get();
	}

	@Override
	public final ItemStack getGunItem() {
		return entity.getItemInHand(hand);
	}

	public final GunItem getGun() {
		return SbwWeapons.getGunItem(getGunItem());
	}

	public final GunData getGunData() {
		return SbwWeapons.getGunData(getGunItem());
	}

	@Override
	@SuppressWarnings("unchecked")
	public final Void shoot(Vec3 target, UUID uuid, double spread) {
		ItemStack gunItem = getGunItem();
		GunItem gun = SbwWeapons.getGunItem(gunItem);
		GunData gunData = SbwWeapons.getGunData(gunItem);
		if (gun != null && gun.canShoot(gunData, this.entity)) {
			if (this.entity.level() instanceof ServerLevel serverLevel) {
				Vec3 shootPos = new Vec3(this.entity.getX(), this.entity.getEyeY(), this.entity.getZ());// 子弹出射位置
				gun.shoot(new ShootParameters(this.entity, this.entity, serverLevel, shootPos, target.subtract(shootPos), SbwWeapons.getGunData(gunItem), spread, this.isAim, uuid, target));
				if (gunData.overHeat.get()) {// 由于SBW只有玩家射击才会播放过热音效，因此这里需要手动播放过热音效
					serverLevel.playLocalSound(shootPos.x, shootPos.y, shootPos.z, ModSounds.OVERHEAT.get(), SoundSource.PLAYERS, 2.0f, 1.0f, true);
				}
			}
			if (LogicalEnd.isClient() && this.entity instanceof ProxyRenderMaidProvider maidProvider) {
				// 如果是客户端且有女仆则播放开火动作
				maidProvider.proxyRenderMaid().playAnimation(MaidAnimationMessage.SWF_FIRE);
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final Void shoot(Entity target, double spread) {
		return this.shoot(target.position(), target.getUUID(), spread);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final Void shoot(Entity target) {
		return this.shoot(target, 0);
	}

	@Override
	public boolean isGun(ItemStack item) {
		return SbwWeapons.isGun(item);
	}

	@Override
	public void setReloadNeedCheckAmmo(boolean needCheckAmmo) {
		this.needCheckAmmo = needCheckAmmo;
	}
}
