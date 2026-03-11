package mcbase.entity.player;

import java.lang.reflect.Constructor;

import com.mojang.authlib.GameProfile;

import jvmsp.reflection;
import jvmsp.reflection.reflection_factory;
import mcbase.LogicalEnd;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BlankPlayer {

	static Constructor<?> AbstractClientPlayer_ctor;

	static {
		// 仅客户端有LocalPlayer类
		if (LogicalEnd.isClient()) {
			AbstractClientPlayer_ctor = reflection.find_constructor(AbstractClientPlayer.class, ClientLevel.class, GameProfile.class);
		}
	}

	public static Player blankAbstractClientPlayer(Class<? extends AbstractClientPlayer> abstractClientPlayerClazz, Level clientLevel, GameProfile profile) {
		if (clientLevel != null && clientLevel.isClientSide()) {
			return (Player) reflection_factory.construct(abstractClientPlayerClazz, AbstractClientPlayer_ctor, clientLevel, profile);
		} else {
			return null;
		}
	}

	public static Player blankLocalPlayer(Level clientLevel, GameProfile profile) {
		if (clientLevel != null && clientLevel.isClientSide()) {
			return (Player) reflection_factory.construct(LocalPlayer.class, AbstractClientPlayer_ctor, clientLevel, profile);
		} else {
			return null;
		}
	}

	public static Player blankLocalPlayer(GameProfile profile) {
		return blankLocalPlayer(LogicalEnd.clientLevel(), profile);
	}

	public static Player blankLocalPlayer(Entity initEntity) {
		return blankLocalPlayer(new GameProfile(initEntity.getUUID(), initEntity.getDisplayName().toString()));
	}
}
