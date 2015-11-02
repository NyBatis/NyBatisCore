package org.nybatis.core.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.xml.reader.DeformedXmlReader;

public class NXmlDeformed extends NXml {

	public NXmlDeformed() {
		super();
	}

	public NXmlDeformed( String xml ) throws ParseException, IoException {
		super( xml, true );
	}

	public NXmlDeformed( File file ) throws ParseException, IoException {
		super( file, true );
	}

	public NXmlDeformed( Path path ) throws ParseException, IoException {
		super( path, true );
	}

	public NXmlDeformed( String xml, boolean ignoreDtd ) throws ParseException, IoException {
		super( xml, ignoreDtd );
	}

	public NXmlDeformed( File file, boolean ignoreDtd ) throws ParseException, IoException {
		super( file, ignoreDtd );
	}

	public NXmlDeformed( Path path, boolean ignoreDtd ) throws ParseException, IoException {
		super( path, ignoreDtd );
	}

    public NXmlDeformed readFrom( String xml, boolean ignoreDtd ) throws ParseException, IoException {
  	    readXml( new DeformedXmlReader().readFrom(xml), ignoreDtd );
  	    return this;
    }

    public NXmlDeformed readFrom( File file, boolean ignoreDtd ) throws ParseException, IoException {
    	readXml( new DeformedXmlReader().readFrom(file), ignoreDtd );
    	return this;
    }

	private void readXml( String xml, boolean ignoreDtd ) throws ParseException, IoException {
	    readFrom( new ByteArrayInputStream(xml.getBytes()), ignoreDtd );
    }

}
