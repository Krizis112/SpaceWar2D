package io.w8.ucore.function;

import com.badlogic.gdx.math.Rectangle;

public interface TileHitboxProvider{
	public void getHitbox(int x, int y, Rectangle out);
}
