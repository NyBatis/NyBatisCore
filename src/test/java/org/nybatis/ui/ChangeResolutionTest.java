package org.nybatis.ui;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class ChangeResolutionTest {

	public void test() {

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		GraphicsDevice[] screenDevices = env.getScreenDevices();
		
		for( GraphicsDevice screenDevice : screenDevices ) {
			
			System.out.println( screenDevice );

			System.out.println( screenDevice.getDisplayMode().getHeight() );
			System.out.println( screenDevice.getDisplayMode().getWidth() );
			System.out.println( screenDevice.getDisplayMode().getBitDepth() );
			System.out.println( screenDevice.getDisplayMode().getRefreshRate() );
			System.out.println( screenDevice.isDisplayChangeSupported() );
			
			// Here is evidence !!
			
		}
		
		GraphicsDevice defaultScreenDevice = env.getDefaultScreenDevice();
		
		DisplayMode dm = new DisplayMode( 1024, 768, 32, 60 );
		
		defaultScreenDevice.setDisplayMode( dm );
		
	}

}
