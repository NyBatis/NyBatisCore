package org.nybatis.core.conf;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.node.Node;

/**
 * Basic File Configuration Assistor.
 * <pre>
 * It reads XML format document configuration from file (/conf/common.xml)
 * </pre>
 */
public class NConfigulator {

    private static NXml conf;

    private static Map<String, String> cached = new HashMap<>();

    static {
    	loadXml();
    }

	/**
	 * Get childNodes using xpath expression.
	 *
	 * <p></p>
	 *
	 * <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <th>Expression</th>
	 *    <th>Description</th>
	 *  </tr>
	 *  <tr>
	 *    <td>nodename</td>
	 *    <td>Selects all nodes have [nodename]</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/</td>
	 *    <td>Selects from the root node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//</td>
	 *    <td>Selects from the root node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.</td>
	 *    <td>Selects the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>..</td>
	 *    <td>Selects the parent of the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>./</td>
	 *    <td>Selects from the current node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//</td>
	 *    <td>Selects from the current node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>@</td>
	 *    <td>Selects attributes</td>
	 *  </tr>
	 *  </table>
	 *
	 *  <p>Example</p>
	 *
	 *  <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <td>Path</td>
	 *    <td>Result</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employee</td>
	 *    <td>Selects all nodes with the name “employee”</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employees/employee</td>
	 *    <td>Selects all employee elements that are children of employees</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee</td>
	 *    <td>Selects all book elements no matter where they are in the document</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[1]</td>
	 *    <td>Selects the first employee element that is the child of the employees element.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()]</td>
	 *    <td>Selects the last employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()-1]</td>
	 *    <td>Selects the last but one employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee[@type='admin']</td>
	 *    <td>Selects all the employee elements that have an attribute named type with a value of 'admin'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//*[@id='c2']</td>
	 *    <td>Selects all elements that have an attribute named id with a value of 'c2'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[local-name()='granada']</td>
	 *    <td>Selects all elements that their tag name equals 'granada'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' for example 'SelectName', 'PeriodName' IloveNameInSpace' and so on.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name'][local-name() != 'SurrName']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' and not equals to 'SurrName'</td>
	 *  </tr>
	 *  </table>
	 *
	 * @param xpath xPath Expression
	 * @return Node List
	 */
    public static List<Node> getNodes( String xpath ) {
        return conf.getChildNodes( xpath );
    }

	/**
	 * Get node of first childNode from root using xpath expression.
	 *
	 * <p></p>
	 *
	 * <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <th>Expression</th>
	 *    <th>Description</th>
	 *  </tr>
	 *  <tr>
	 *    <td>nodename</td>
	 *    <td>Selects all nodes have [nodename]</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/</td>
	 *    <td>Selects from the root node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//</td>
	 *    <td>Selects from the root node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.</td>
	 *    <td>Selects the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>..</td>
	 *    <td>Selects the parent of the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>./</td>
	 *    <td>Selects from the current node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//</td>
	 *    <td>Selects from the current node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>@</td>
	 *    <td>Selects attributes</td>
	 *  </tr>
	 *  </table>
	 *
	 *  <p>Example</p>
	 *
	 *  <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <td>Path</td>
	 *    <td>Result</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employee</td>
	 *    <td>Selects all nodes with the name “employee”</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employees/employee</td>
	 *    <td>Selects all employee elements that are children of employees</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee</td>
	 *    <td>Selects all book elements no matter where they are in the document</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[1]</td>
	 *    <td>Selects the first employee element that is the child of the employees element.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()]</td>
	 *    <td>Selects the last employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()-1]</td>
	 *    <td>Selects the last but one employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee[@type='admin']</td>
	 *    <td>Selects all the employee elements that have an attribute named type with a value of 'admin'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//*[@id='c2']</td>
	 *    <td>Selects all elements that have an attribute named id with a value of 'c2'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[local-name()='granada']</td>
	 *    <td>Selects all elements that their tag name equals 'granada'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' for example 'SelectName', 'PeriodName' IloveNameInSpace' and so on.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name'][local-name() != 'SurrName']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' and not equals to 'SurrName'</td>
	 *  </tr>
	 *  </table>
	 *
	 * @param xpath xPath Expression
	 * @return Node
	 */
    public static Node getNode( String xpath ) {
        return conf.getChildNode( xpath );
    }

	/**
	 * Get value of first childNode from root using xpath expression.
	 *
	 * <p></p>
	 *
	 * <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <th>Expression</th>
	 *    <th>Description</th>
	 *  </tr>
	 *  <tr>
	 *    <td>nodename</td>
	 *    <td>Selects all nodes have [nodename]</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/</td>
	 *    <td>Selects from the root node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//</td>
	 *    <td>Selects from the root node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.</td>
	 *    <td>Selects the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>..</td>
	 *    <td>Selects the parent of the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>./</td>
	 *    <td>Selects from the current node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//</td>
	 *    <td>Selects from the current node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>@</td>
	 *    <td>Selects attributes</td>
	 *  </tr>
	 *  </table>
	 *
	 *  <p>Example</p>
	 *
	 *  <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <td>Path</td>
	 *    <td>Result</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employee</td>
	 *    <td>Selects all nodes with the name “employee”</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employees/employee</td>
	 *    <td>Selects all employee elements that are children of employees</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee</td>
	 *    <td>Selects all book elements no matter where they are in the document</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[1]</td>
	 *    <td>Selects the first employee element that is the child of the employees element.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()]</td>
	 *    <td>Selects the last employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()-1]</td>
	 *    <td>Selects the last but one employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee[@type='admin']</td>
	 *    <td>Selects all the employee elements that have an attribute named type with a value of 'admin'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//*[@id='c2']</td>
	 *    <td>Selects all elements that have an attribute named id with a value of 'c2'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[local-name()='granada']</td>
	 *    <td>Selects all elements that their tag name equals 'granada'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' for example 'SelectName', 'PeriodName' IloveNameInSpace' and so on.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name'][local-name() != 'SurrName']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' and not equals to 'SurrName'</td>
	 *  </tr>
	 *  </table>
	 *
	 * @param xpath xPath Expression
	 * @return Value
	 */
    public static String getValue( String xpath ) {

    	if( cached.containsKey(xpath) ) return cached.get(xpath);

    	String val = conf.getChildNode( xpath ).getValue();

    	return getBindingValue( xpath, val );

    }

	/**
	 * Get value of childNodes using xpath expression.
	 *
	 * <p></p>
	 *
	 * <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <th>Expression</th>
	 *    <th>Description</th>
	 *  </tr>
	 *  <tr>
	 *    <td>nodename</td>
	 *    <td>Selects all nodes have [nodename]</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/</td>
	 *    <td>Selects from the root node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//</td>
	 *    <td>Selects from the root node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.</td>
	 *    <td>Selects the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>..</td>
	 *    <td>Selects the parent of the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>./</td>
	 *    <td>Selects from the current node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//</td>
	 *    <td>Selects from the current node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>@</td>
	 *    <td>Selects attributes</td>
	 *  </tr>
	 *  </table>
	 *
	 *  <p>Example</p>
	 *
	 *  <table border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <td>Path</td>
	 *    <td>Result</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employee</td>
	 *    <td>Selects all nodes with the name “employee”</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employees/employee</td>
	 *    <td>Selects all employee elements that are children of employees</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee</td>
	 *    <td>Selects all book elements no matter where they are in the document</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[1]</td>
	 *    <td>Selects the first employee element that is the child of the employees element.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()]</td>
	 *    <td>Selects the last employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()-1]</td>
	 *    <td>Selects the last but one employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee[@type='admin']</td>
	 *    <td>Selects all the employee elements that have an attribute named type with a value of 'admin'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//*[@id='c2']</td>
	 *    <td>Selects all elements that have an attribute named id with a value of 'c2'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[local-name()='granada']</td>
	 *    <td>Selects all elements that their tag name equals 'granada'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' for example 'SelectName', 'PeriodName' IloveNameInSpace' and so on.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name'][local-name() != 'SurrName']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' and not equals to 'SurrName'</td>
	 *  </tr>
	 *  </table>
	 *
	 * @param xpath xPath Expression
	 * @return Value List
	 */
    public static List<String> getValues( String xpath ) {

    	List<String> values = new ArrayList<>();

    	for( Node node : getNodes(xpath) ) {
    		values.add( getValue(node) );
    	}

    	return values;

    }

	/**
	 * Get value from node.
	 *
	 * <pre>
	 * node itself can give the value but this method can bind defined default value.
	 * </pre>
	 *
	 * @param node
	 * @return text binded defined default value
	 */
    public static String getValue( Node node ) {

    	if( node.isNull() ) return "";

    	String xpath = node.getXpath();

    	if( cached.containsKey(xpath) ) return cached.get(xpath);

    	return getBindingValue( xpath, node.getValue() );

    }

	/**
	 * Refresh cache and reload configuration from file.
	 */
    public static void refresh() {
    	cached.clear();
    	loadXml();
    }

    private static void loadXml() {
    	conf = new NXml( Paths.get(Const.path.getConfig(), Const.profile.apply( "common.xml" )), true );
    }

	/**
	 * Get configuration file path to load xml
	 *
	 * @return configuration file path
	 */
	public static String getFilePath() {
		return Const.path.getConfig();
	}

    private static String getBindingValue( String key, String value ) {

    	if( StringUtil.isEmpty( value ) ) {
    		value = "";
    	} else {
    		value = value
    			.replaceAll( "#\\{root\\}", Const.path.getRoot() )
    			.replaceAll( "#\\{base\\}", Const.path.getBase() )
    			;
    	}

    	cached.put( key, value );

    	return cached.get( key );

    }

}
