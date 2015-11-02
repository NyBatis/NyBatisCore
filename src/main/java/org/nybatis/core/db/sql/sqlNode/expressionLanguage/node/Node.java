package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.EmptyNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NullNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.OperatorNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.DivideNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.MinusNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.MultipleNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.PlusNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.RemainderNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.SquareNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.EqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.GreaterEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.GreaterThanNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.LessEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.LessThanNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.LikeEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.NotEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.NotLikeEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.logical.AndNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.logical.OrNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.variable.VariableNode;

public abstract class Node {

	public abstract int getPriority();

	public VariableNode toVariableNode() {
		return (VariableNode) this;
	}
	
	public StringValueNode toStringValueNode() {
		return (StringValueNode) this;
	}
	
	public NumericValueNode toNumericValueNode() {
		return (NumericValueNode) this;
	}
	
	public BooleanValueNode toBooleanValueNode() {
		return (BooleanValueNode) this;
	}
	
	public EmptyNode toEmptyNode() {
		return (EmptyNode) this;
	}
	
	public NullNode toNullNode() {
		return (NullNode) this;
	}
	
	public OperatorNode toOperatorNode() {
		return (OperatorNode) this;
	}
	
	public DivideNode toDivideNode() {
		return (DivideNode) this;
	}

	public MinusNode toMinusNode() {
		return (MinusNode) this;
	}

	public MultipleNode toMultipleNode() {
		return (MultipleNode) this;
	}
	
	public PlusNode toPlusNode() {
		return (PlusNode) this;
	}
	
	public RemainderNode toRemainderNode() {
		return (RemainderNode) this;
	}
	
	public SquareNode toSquareNode() {
		return (SquareNode) this;
	}
	
	public EqualNode toEqualNode() {
		return (EqualNode) this;
	}
	
	public NotEqualNode toNotEqualNode() {
		return (NotEqualNode) this;
	}

	public GreaterEqualNode toGreaterEqualNode() {
		return (GreaterEqualNode) this;
	}
	
	public GreaterThanNode toGreaterThanNode() {
		return (GreaterThanNode) this;
	}
	
	public LessEqualNode toLessEqualNode() {
		return (LessEqualNode) this;
	}
	
	public LessThanNode toLessThanNode() {
		return (LessThanNode) this;
	}
	
	public LikeEqualNode toLikeEqualNode() {
		return (LikeEqualNode) this;
	}
	
	public NotLikeEqualNode toNotLikeEqualNode() {
		return (NotLikeEqualNode) this;
	}
	
	public AndNode toAndNode() {
		return (AndNode) this;
	}
	
	public OrNode toOrNode() {
		return (OrNode) this;
	}

	public boolean isNull() {
		return this instanceof NullNode;
	}

	public boolean isEmpty() {
		return this instanceof EmptyNode;
	}

	public boolean isString() {
		return this instanceof StringValueNode;
	}

	public boolean isNumeric() {
		return this instanceof NumericValueNode;
	}

	public boolean isOperator() {
		return this instanceof OperatorNode;
	}

}
