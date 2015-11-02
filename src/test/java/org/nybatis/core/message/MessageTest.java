package org.nybatis.core.message;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;

public class MessageTest {

	@Test
	public void simpleTest() {

		Message.loadPool();

        Assert.assertEquals( "내가 미쳤지", Message.get( "ui.err.0001" ) );
        Assert.assertEquals( "이거 정말 돌아가 ?",                    Message.get( "ui.err.0002" ) );
        Assert.assertEquals( "It is not exits in KOREAN properties.", Message.get( "ui.err.0003" ) );
        Assert.assertEquals( "I am crazy..",                          Message.get( "ui.err.0001", Locale.ENGLISH ) );
		
	}

	@Test
	public void autoInitilizedTest() {
		
		Assert.assertEquals( "내가 미쳤지",                           Message.get( "ui.err.0001" ) );
		Assert.assertEquals( "이거 정말 돌아가 ?",                    Message.get( "ui.err.0002" ) );
		Assert.assertEquals( "It is not exits in KOREAN properties.", Message.get( "ui.err.0003" ) );
		Assert.assertEquals( "I am crazy..",                          Message.get( "ui.err.0001", Locale.ENGLISH ) );
		
	}

	@Test
	public void carriageReturnTest() {

		Assert.assertEquals( "삭제하시겠습니까 ? \n\n[]", Message.get( "msg.confirm.001" ) );
		
	}
	
}
