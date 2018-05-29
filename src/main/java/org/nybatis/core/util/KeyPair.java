package org.nybatis.core.util;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author nayasis@gmail.com
 * @since 2018-05-30
 */
public class KeyPair {

    private PublicKey  publicKey;
    private PrivateKey privateKey;

    public void setKeyPair( java.security.KeyPair keyPair ) {
        setPublicKey( keyPair.getPublic() );
        setPrivateKey( keyPair.getPrivate() );
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey( String publicKey ) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec( Base64.decodeBase64(publicKey) );
            this.publicKey = getKeyFactory().generatePublic( keySpec );
        } catch ( InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    private KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch( NoSuchAlgorithmException e ) {
            throw new RuntimeException( e );
        }
    }

    public void setPublicKey( PublicKey publicKey ) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey( PrivateKey privateKey ) {
        this.privateKey = privateKey;
    }

    public void setPrivateKey( String privateKey ) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec( Base64.decodeBase64(privateKey) );
            this.privateKey = getKeyFactory().generatePrivate( keySpec );
        } catch ( InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    public String getEncodedPublicKey() {
        return Base64.encodeBase64String( publicKey.getEncoded() );
    }

    public String getEncodedPrivateKey() {
        return Base64.encodeBase64String( privateKey.getEncoded() );
    }

}
