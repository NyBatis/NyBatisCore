package org.nybatis.core.xml.nullNode;

import org.nybatis.core.xml.NXml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NullNodeList implements NodeList {

	@Override
    public Node item( int index ) {
	    return NXml.NULL_NODE;
    }

	@Override
    public int getLength() {
	    return 0;
    }

}
