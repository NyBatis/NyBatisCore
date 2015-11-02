package org.nybatis.core.testModel;

import javafx.scene.image.Image;

@SuppressWarnings( "restriction" )
public class IconTitle {

	private Image   icon;
	private String  title;

	public IconTitle( Image icon, String title ) {
		this.icon  = icon;
		this.title = title;
	}

	public Image getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

}
