package org.nybatis.core.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author nayasis@gmail.com
 * @since 2018-05-30
 */
public class RsaEncryptorTest {

    @Test
    public void test() {

        RsaEncryptor encryptor = new RsaEncryptor();

        KeyPair keyPair = encryptor.generateKey();

        System.out.println( "public  key : " + keyPair.getEncodedPublicKey() );
        System.out.println( "private key : " + keyPair.getEncodedPrivateKey() );

        System.out.println( "---------------------------------------------------" );

        String plainText = "Hello RSA world !";

        String encrypt = encryptor.encrypt( plainText, keyPair.getPublicKey() );

        System.out.println( encrypt );

        String decrypt = encryptor.decrypt( encrypt, keyPair.getPrivateKey() );

        System.out.println( decrypt );

        Assert.assertEquals( plainText, decrypt );

    }

}