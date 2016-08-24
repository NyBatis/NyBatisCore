package org.nybatis.core.util;

import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author nayasis
 * @since 2016-08-24
 */
public class EncryptorTest {

    private String originalValue = "정화수가 만들었어요. password 漢文";

    Encryptor encryptor = new Encryptor();

    @Test
    public void basic() {
        String secretKey = "비밀키입니다.";
        checkValidWorking( secretKey );
    }

    @Test
    public void nullTest() {
        String secretKey = null;
        checkValidWorking( secretKey );
    }

    @Test
    public void generateKey() {
        String key = encryptor.generateKey();
        NLogger.debug( key );
        checkValidWorking( key );
    }

    private void checkValidWorking( String secretKey ) {

        String encrypted = encryptor.encrypt( originalValue, secretKey );
        String decrypted = encryptor.decrypt( encrypted, secretKey );

        NLogger.debug( "encrypted : {}", encrypted );
        NLogger.debug( "decrypted : {}", decrypted );

        assertEquals( decrypted, originalValue );
    }

}