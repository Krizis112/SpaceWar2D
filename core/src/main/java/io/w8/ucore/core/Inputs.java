package io.w8.ucore.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import io.w8.ucore.util.Input;

public class Inputs{
	private static final boolean[] buttons = new boolean[5];
	private static final IntSet keysReleased = new IntSet();
	private static final InputMultiplexer plex = new InputMultiplexer();
	private static int scroll = 0;
	private static InputProcessor listen = new InputAdapter(){
		public boolean scrolled(int amount){
			scroll = -amount;
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			keysReleased.add(keycode);
			return false;
		}
	};
	private static final Array<InputDevice> devices = new Array<>();

	static{
		plex.addProcessor(listen);
		devices.add(new InputDevice(DeviceType.keyboard, "Keyboard"));
	}

	public static void setDebug(boolean debug){
		// controller debugging removed
	}

	public static void useControllers(boolean use){
		// controller support removed
	}

	public static InputMultiplexer getProcessor(){
		return plex;
	}

	public static Array<InputDevice> getDevices(){
		return devices;
	}

	/**Call this at the end of each render loop.*/
	public static void update(){
		for(int i = 0; i < buttons.length; i ++){
			buttons[i] = Gdx.input.isButtonPressed(i);
		}
		scroll = 0;
		keysReleased.clear();
	}

	public static void clearProcessors(){
		plex.getProcessors().clear();
	}

	/**Adds another input processor to the chain.*/
	public static void addProcessor(InputProcessor listener){
		plex.addProcessor(listener);
		Gdx.input.setInputProcessor(plex);
	}

	/**Adds another input processor to the chain at a specific index.*/
	public static void addProcessor(int index, InputProcessor listener){
		plex.addProcessor(index, listener);
		Gdx.input.setInputProcessor(plex);
	}

	public static void flipProcessors(){
		plex.getProcessors().reverse();
	}

	public static boolean keyDown(String name){
		return keyDown("default", name);
	}

	public static boolean keyTap(String name){
		return keyTap("default", name);
	}

	public static boolean keyRelease(String name){
		return keyRelease("default", name);
	}

	public static boolean keyDown(Input input){
		return keyDown(input, KeyBinds.getSection("default").device);
	}

	public static boolean keyDown(Input input, InputDevice device){
		if(input == Input.UNSET)
			return false;

		if(input.type == Input.Type.key){
			return input.code >= 0 && Gdx.input.isKeyPressed(input.code);
		}else if(input.type == Input.Type.mouse){
			return Gdx.input.isButtonPressed(input.code);
		}
		return false;
	}

	public static boolean keyDown(String section, String name){
		KeyBinds.Section s = KeyBinds.getSection(section);
		if(KeyBinds.has(section, name)){
			Input input = KeyBinds.get(section, s.device.type, name);
			return keyDown(input, s.device);
		}else{
			Input input = KeyBinds.get(section, DeviceType.keyboard, name);
			return keyDown(input, getKeyboard());
		}
	}

	public static boolean keyTap(Input input){
		return keyTap(input, KeyBinds.getSection("default").device);
	}

	public static boolean keyTap(Input input, InputDevice device){
		if(input == Input.UNSET)
			return false;

		if(input.type == Input.Type.key){
			return Gdx.input.isKeyJustPressed(input.code);
		}else if(input.type == Input.Type.mouse){
			return Gdx.input.isButtonPressed(input.code) && !buttons[input.code];
		}
		return false;
	}

	public static boolean keyTap(String section, String name){
		KeyBinds.Section s = KeyBinds.getSection(section);
		if(KeyBinds.has(section, name)){
			Input input = KeyBinds.get(section, name);
			return keyTap(input, s.device);
		}else{
			Input input = KeyBinds.get(section, DeviceType.keyboard, name);
			return keyTap(input, getKeyboard());
		}
	}

	public static boolean keyRelease(Input input){
		return keyRelease(input, KeyBinds.getSection("default").device);
	}

	public static boolean keyRelease(Input input, InputDevice device){
		if(input == Input.UNSET)
			return false;

		if(input.type == Input.Type.key){
			return keysReleased.contains(input.code);
		}else if(input.type == Input.Type.mouse){
			return !Gdx.input.isButtonPressed(input.code) && buttons[input.code];
		}
		return false;
	}

	public static boolean keyRelease(String section, String name){
		KeyBinds.Section s = KeyBinds.getSection(section);
		Input input = KeyBinds.get(section, name);
		if(KeyBinds.has(section, name)){
			return keyRelease(input, s.device);
		}else{
			return keyRelease(input, getKeyboard());
		}
	}

	public static boolean getAxisActive(String axis){
		return Math.abs(getAxis("default", axis)) > 0;
	}

	public static float getAxis(String axis){
		return getAxis("default", axis);
	}

	public static float getAxis(String section, String name){
		KeyBinds.Section s = KeyBinds.getSection(section);
		Axis axis = KeyBinds.getAxis(section, name);

		if(axis.min == Input.SCROLL){
			return scroll();
		}

		boolean min = keyDown(axis.min, s.device);
		boolean max = keyDown(axis.max, s.device);
		return (min && max) || (!min && !max) ? 0 : min ? -1 : 1;
	}

	public static InputDevice getKeyboard(){
		return devices.get(0);
	}

	public static boolean buttonDown(int button){
		return Gdx.input.isButtonPressed(button);
	}

	public static boolean buttonUp(int button){
		return Gdx.input.isButtonPressed(button) && !buttons[button];
	}

	public static boolean buttonRelease(int button){
		return !Gdx.input.isButtonPressed(button) && buttons[button];
	}

	public static int scroll(){
		return scroll;
	}

	public static boolean scrolled(){
		return Math.abs(scroll) > 0;
	}

	static void dispose(){
		plex.getProcessors().clear();
	}

	/**Represents an input device (keyboard only in this build).*/
	public static class InputDevice{
		public final DeviceType type;
		public final String name;

		public InputDevice(DeviceType type, String name){
			this.type = type;
			this.name = name;
		}
	}

	//TODO 2D axes, like controller sticks, as having two different axes is confusing.
	/**Represents an input axis.*/
	public static class Axis{
		public Input min, max;

		/**Constructor for axis-only inputs.*/
		public Axis(Input axis){
			min = max = axis;
		}

		/**Constructor for dual-direction inputs.*/
		public Axis(Input min, Input max){
			this.min = min;
			this.max = max;
		}
	}

	public enum DeviceType{
		keyboard
	}
}
