package mcbase.entity.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.mojang.authlib.GameProfile;

import cpw.mods.modlauncher.api.INameMappingService;
import jvmsp.reflection;
import jvmsp.reflection.reflection_factory;
import jvmsp.unsafe;
import mcbase.LogicalEnd;
import mcbase.entity.data.SynchedEntityDataOp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class BlankPlayer {

	private static Constructor<?> AbstractClientPlayer_ctor;

	private static Field ClientPacketListener_localGameProfile;

	static {
		// 仅客户端有LocalPlayer类
		if (LogicalEnd.isClient()) {
			AbstractClientPlayer_ctor = reflection.find_constructor(AbstractClientPlayer.class, ClientLevel.class, GameProfile.class);
			ClientPacketListener_localGameProfile = reflection.find_declared_field(ClientPacketListener.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_104886_"));
		}
	}

	public static final Player blankAbstractClientPlayer(Class<? extends AbstractClientPlayer> abstractClientPlayerClazz, Level clientLevel, GameProfile profile) {
		if (clientLevel != null && clientLevel.isClientSide()) {
			Player player = reflection_factory.construct(abstractClientPlayerClazz, AbstractClientPlayer_ctor, clientLevel, profile);
			SynchedEntityDataOp.showPlayerModelParts(player, SynchedEntityDataOp.PLAYER_MODEL_PART_ALL);// 必须手动设置要渲染的部件，否则PlayerRenderer不会渲染这些额外部件，双层皮肤必须。
			return player;
		} else {
			return null;
		}
	}

	public static final ClientPacketListener blankClientPacketListener(GameProfile profile) {
		if (LogicalEnd.isClient()) {
			ClientPacketListener listener = unsafe.allocate(ClientPacketListener.class);
			unsafe.write(listener, ClientPacketListener_localGameProfile, profile);
			return listener;
		} else {
			return null;
		}
	}

	public static final Player blankLocalPlayer(Level clientLevel, GameProfile profile) {
		if (clientLevel != null && clientLevel.isClientSide()) {
			// 由于有部分mod会Mixin类LocalPlayer的构造函数或添加新字段，因此这里采用直接调用构造函数避免新加的字段没有初始化。
			LocalPlayer player = new LocalPlayer(Minecraft.getInstance(), (ClientLevel) clientLevel, blankClientPacketListener(profile), null, null, false, false);
			SynchedEntityDataOp.showPlayerModelParts(player, SynchedEntityDataOp.PLAYER_MODEL_PART_ALL);
			return player;
		} else {
			return null;
		}
	}

	public static final Player blankLocalPlayer(GameProfile profile) {
		return blankLocalPlayer(LogicalEnd.clientLevel(), profile);
	}

	public static final Player blankLocalPlayer(Entity initEntity) {
		return blankLocalPlayer(new GameProfile(initEntity.getUUID(), initEntity.getDisplayName().toString()));
	}
}
