package io.w8.ucore;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Logger;
import io.w8.ucore.core.Timers;
import io.w8.ucore.function.Callable;
import io.w8.ucore.util.Log;

import java.lang.reflect.Field;

public class UCore{
	private static Logger logger;

	public static void profile(int iterations, Callable c1, Callable c2){
		//warmup
		for(int i = 0; i < iterations; i ++){
			c1.run();
			c2.run();
		}

		Timers.mark();
		for(int i = 0; i < iterations; i ++){
			c1.run();
		}
		Log.info("Time taken for procedure 1: {0}ms", Timers.elapsed());

		Timers.mark();
		for(int i = 0; i < iterations; i ++){
			c2.run();
		}
		Log.info("Time taken for procedure 2: {0}ms", Timers.elapsed());
	}

	public static boolean isAssets(){
		return Gdx.app != null && Gdx.app.getType() != ApplicationType.WebGL
				&& getProperty("user.name").equals("anuke")
				&& getAbsolute(Gdx.files.local("na").parent()).endsWith("assets");
	}

	public static String getProperty(String name){
		try{
			return System.getProperty(name);
		}catch(Exception e){
			return null;
		}
	}

	public static String getPropertyNotNull(String name){
		String s = getProperty(name);
		return s == null ? "" : s;
	}

	public static String getAbsolute(FileHandle file){
		return file.file().getAbsolutePath();
	}
	
	public static Object getPrivate(Object object, String name){
		try{
			Field field = object.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(object);
		}catch(ReflectiveOperationException e){
            throw new RuntimeException(e);
		}
	}

	public static Object getPrivate(Class<?> type, Object object, String name){
		try{
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.get(object);
		}catch(ReflectiveOperationException e){
			throw new RuntimeException(e);
		}
	}

	public static void setPrivate(Object object, String name, Object value){
		try{
			Field field = object.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(object, value);
		}catch(ReflectiveOperationException e){
			throw new RuntimeException(e);
		}
	}
}
