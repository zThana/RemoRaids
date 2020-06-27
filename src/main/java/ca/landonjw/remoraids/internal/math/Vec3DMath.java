package ca.landonjw.remoraids.internal.math;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

public class Vec3DMath {

	public static boolean withinChunk(Chunk chunk, Vec3d delegate) {
		Vec3d min = new Vec3d(chunk.x << 4, 0, chunk.z << 4);
		Vec3d max = min.add(15, 0, 15);

		return delegate.x >= min.x && delegate.z >= min.z && delegate.x <= max.x && delegate.z <= max.z;
	}

}
