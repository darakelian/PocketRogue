package xyz.vec3d.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import xyz.vec3d.game.PocketRogue;

/**
 *
 */
public class DesktopLauncher {

	/**
	 *
	 * @param arg
     */
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new PocketRogue(), config);
	}

}