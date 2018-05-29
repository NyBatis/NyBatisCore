package org.nybatis.core.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * Encryptor with RSA algorithm
 *
 * @author nayasis@gmail.com
 * @since 2018-05-30
 */
public class RsaEncryptor {

    public KeyPair generateKey() {

        KeyPair keyPair = new KeyPair();

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance( "RSA" );
            keyPairGenerator.initialize(512, secureRandom );
            keyPair.setKeyPair( keyPairGenerator.genKeyPair() );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return keyPair;

    }

    public String encrypt( String value, PublicKey publicKey ) {
        try {
            byte[] encrypted = getCipher(publicKey).doFinal( getBytes(value) );
            return Base64.encodeBase64String( encrypted );
        } catch( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException( e );
        }
    }

    public String encrypt( String value, String publicKey ) {
        KeyPair keyPair = new KeyPair();
        keyPair.setPublicKey( publicKey );
        return encrypt( value, keyPair.getPublicKey() );
    }

    public String decrypt( String value, PrivateKey privateKey ) {
        byte[] bytes = Base64.decodeBase64( getBytes( value ) );
        try {
            byte[] decrypted = getCipher(privateKey).doFinal( bytes );
            return new String( decrypted );
        } catch( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException( e );
        }
    }

    public String decrypt( String value, String privateKey ) {
        KeyPair keyPair = new KeyPair();
        keyPair.setPrivateKey( privateKey );
        return decrypt( value, keyPair.getPrivateKey() );
    }

    private byte[] getBytes( String value ) {
        return StringUtil.nvl(value).getBytes();
    }

    private Cipher getCipher( Key key ) {
        try {
            Cipher cipher = Cipher.getInstance( "RSA" );
            if( key instanceof PublicKey ) {
                cipher.init(Cipher.ENCRYPT_MODE, key );
            } else if( key instanceof PrivateKey ) {
                cipher.init(Cipher.DECRYPT_MODE, key );
            }
            return cipher;
        } catch( NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e ) {
            throw new RuntimeException( e );
        }
    }

}
