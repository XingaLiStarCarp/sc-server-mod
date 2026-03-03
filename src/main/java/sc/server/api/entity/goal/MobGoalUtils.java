package sc.server.api.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MobGoalUtils {
	/**
	 * 当前位置的脚下方块是否是可站立的。
	 * 
	 * @param level
	 * @param pos
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static final boolean isStandable(Level level, Vec3 pos) {
		BlockPos groundPos = new BlockPos((int) pos.x, (int) pos.y - 1, (int) pos.z);
		if (!level.isInWorldBounds(groundPos)) {
			return false;
		} else {
			return level.getBlockState(groundPos).isSolid();
		}
	}
}
