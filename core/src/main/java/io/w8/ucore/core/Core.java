package io.w8.ucore.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;
import io.w8.ucore.util.Atlas;

public class Core{
	public static OrthographicCamera camera = new OrthographicCamera();
	public static Batch batch;
	public static Atlas atlas;
	public static BitmapFont font;
	public static int cameraScale = 1;

	public static I18NBundle bundle;

	/* Disposes of all resources and internal systems. */
	public static void dispose(){
		Graphics.dispose();
		Inputs.dispose();
		Sounds.dispose();
		Musics.dispose();
		Timers.dispose();

		if(batch != null){
			batch.dispose();
			batch = null;
		}

		if(atlas != null){
			atlas.dispose();
			atlas = null;
		}

		if(font != null){
			font.dispose();
			font = null;
		}
	}
}
