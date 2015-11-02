package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.EmptyNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NullNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.EqualityNode;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;

public class LikeEqualNode extends EqualityNode {

	public String toString() {
		return  "like";
	}
	
	@Override
  public BooleanValueNode calculate( Node preNode, Node postNode ) throws SqlParseException {

		String preVal  = getStringValue( preNode );
		
		String postVal = getStringValue( postNode );
		
	  return new BooleanValueNode( StringUtil.like( preVal, postVal ) );

	}
	
	private String getStringValue( Node preNode ) throws SqlParseException {
	    
		String value = "";

	    if( preNode instanceof NumericValueNode ) {

			value = preNode.toString();
			
		} else if( preNode instanceof StringValueNode ) {

			value = ((StringValueNode) preNode).getValue();
			
		} else if( ! (preNode instanceof NullNode || preNode instanceof EmptyNode ) ) {
			throw new SqlParseException( "Node[{}] can not be converted to string value.", preNode );
		}
    
	    return value;
	
	}

}
