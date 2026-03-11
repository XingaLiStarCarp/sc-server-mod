package mcbase.entity.mob;

import mcbase.entity.Humanoid;
import mcbase.entity.Humanoid.PlayerModelAsset;
import mcbase.entity.ProxyRenderEntity;
import mcbase.entity.player.BlankPlayer;
import mcbase.entity.player.PlayerData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface ProxyRenderPlayer extends ProxyRenderEntity<Player, PlayerModelAsset>, Humanoid {

	/**
	 * 禁止覆写此方法。<br>
	 * 创建一个虚假的玩家渲染实体。<br>
	 */
	@Override
	public default Player blankRenderingEntity(Entity bindEntity) {
		return BlankPlayer.blankLocalPlayer(bindEntity);
	}

	@Override
	public default Player syncRenderingEntityData() {
		Player renderingEntity = ProxyRenderEntity.super.syncRenderingEntityData();
		Entity bindEntity = bindEntity();
		PlayerData.syncEntityData(bindEntity, renderingEntity);
		return renderingEntity;
	}

	public interface ProxyRenderPlayerEntity extends ProxyRenderPlayer, HumanoidEntity {
		@Override
		default SynchedEntityData getEntityData() {
			return ProxyRenderPlayer.super.getEntityData();
		}
	}
}
