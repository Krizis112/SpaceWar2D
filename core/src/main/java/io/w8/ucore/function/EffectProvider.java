package io.w8.ucore.function;

import com.badlogic.gdx.graphics.Color;

import io.w8.ucore.core.Effects.Effect;

public interface EffectProvider{
	public void createEffect(Effect effect, Color color, float x, float y, float rotation);
}
