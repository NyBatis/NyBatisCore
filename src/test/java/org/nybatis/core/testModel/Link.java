package org.nybatis.core.testModel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.StringUtil;

@SuppressWarnings( "restriction" )
public class Link {

	private Integer id;
	private String  title;
	private String  groupName;
	private int     execCount;
	private String  lastUsedDt;
	private String  execPath;
	private String  execOption;
	private String  cmdPrev;
	private String  cmdNext;
	private String  description;

    private Image   icon;
	private String  iconByte;

	private final static String  ICON_IMAGE_TYPE = "png";

	public Link() {}

	public Link( File file ) {

		title = FileUtil.removeExtention( file.getName() );

		execPath = getFilePath( file );

		setIcon( file );

	}

	public void setIconByte( String iconByte ) {

		this.iconByte = iconByte;

		ByteArrayInputStream stream = new ByteArrayInputStream( (byte[]) StringUtil.decode( iconByte ) );

        try {

        	BufferedImage image = ImageIO.read( stream );

        	icon = SwingFXUtils.toFXImage( image, null );

        } catch( IOException e ) {
        	NLogger.error( e );
        }


	}

	public String getIconByte() {
		return iconByte;
	}

	public void setIcon( Image icon ) {

		this.icon = icon;

		try {

			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			ImageIO.write( SwingFXUtils.fromFXImage(icon, null), ICON_IMAGE_TYPE, stream );

			iconByte = StringUtil.encode( stream.toByteArray() );

			setIconByte( iconByte );

		} catch( IOException e ) {
	        NLogger.error( e );
        }

	}

	public void setIcon( File file ) {

		icon     = null;
		iconByte = null;

		if( file == null || ! file.exists() ) return;

		if( file.canExecute() ) {

			ImageIcon readIcon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon( file );

			icon = SwingFXUtils.toFXImage( (BufferedImage) readIcon.getImage(), null );

		} else {

			icon = new Image( file.getAbsolutePath() );

		}

		setIcon( icon );

	}

	public Image getIcon() {
		return icon;
	}

	public String getKeyword() {
		return StringUtil.nvl( title ) + " :: " + StringUtil.nvl( groupName );
	}

	public IconTitle getIconTitle() {
		return new IconTitle( icon, title );
	}

	public boolean isRelativePath() {

		if( execPath == null ) return false;

		if( execPath.startsWith( "."  + File.pathSeparator ) ) return true;
		return execPath.startsWith( ".." + File.pathSeparator );

	}

	private String getFilePath( File file ) {

		String filePath = file.getAbsolutePath();

		try {

			filePath = FileUtil.toRelativePath( Const.path.getBase(), filePath );

		} catch( IllegalArgumentException | UncheckedIOException e ) {
			NLogger.error( e );
		}

		return filePath;

	}

	public Link clone() {
		return Reflector.clone( this );
	}

	public Integer getId() {
		return id;
	}

	public void setId( Integer id ) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName( String groupName ) {
		this.groupName = groupName;
	}

	public int getExecCount() {
		return execCount;
	}

	public void setExecCount( int execCount ) {
		this.execCount = execCount;
	}

	public void addExecCount() {
		this.execCount ++;
	}

	public String getLastUsedDt() {
		return lastUsedDt;
	}

	public void setLastUsedDt( String lastUsedDt ) {
		this.lastUsedDt = lastUsedDt;
	}

	public String getExecPath() {
		return execPath;
	}

	public void setExecPath( String execPath ) {
		this.execPath = execPath;
	}

	public String getExecOption() {
		return execOption;
	}

	public void setExecOption( String execOption ) {
		this.execOption = execOption;
	}

	public String getCmdPrev() {
		return cmdPrev;
	}

	public void setCmdPrev( String cmdPrev ) {
		this.cmdPrev = cmdPrev;
	}

	public String getCmdNext() {
		return cmdNext;
	}

	public void setCmdNext( String cmdNext ) {
		this.cmdNext = cmdNext;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public String toString() {
		return Reflector.toString( this );
	}

}
