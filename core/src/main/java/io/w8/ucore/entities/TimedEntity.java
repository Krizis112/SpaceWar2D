package io.w8.ucore.entities;

import io.w8.ucore.core.Timers;
import io.w8.ucore.util.Mathf;
import io.w8.ucore.util.Scalable;

public abstract class TimedEntity extends Entity implements Scalable{
	public float lifetime;
	public float time;
	
	@Override
	public void update(){
		time += Timers.delta();

		time = Mathf.clamp(time, 0, lifetime);
		
		if(time >= lifetime) remove();
	}

	@Override
	public float fin() {
		return time/lifetime;
	}
}
