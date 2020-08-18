package com.Maateusz.rsa;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class RSAKeyPairGenerator {

    private String create_date;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static int key_lenght = 1024;
    // RSA/None/OAEPWithSHA1AndMGF1Padding ; insecure -> RSA/ECB/PKCS1Padding ; RSA/ECB/OAEPWithSHA1AndMGF1Padding ;
    private static String transformation_type = "RSA/None/OAEPWithSHA1AndMGF1Padding";

    public RSAKeyPairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(key_lenght);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        create_date = sdf.format(date);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return String.format(
                "{\"RSAkey\":{\"create_date\":\"%s\",\"public_key\":\"%s\",\"private_key\":\"%s\",\"key_lenght\":\"%s\"}}",
                    create_date,
                    Base64.getEncoder().encodeToString(publicKey.getEncoded()),
                    Base64.getEncoder().encodeToString(privateKey.getEncoded()),
                    String.valueOf(key_lenght) );
    }

    //---------------------ENCRYPTION-------------------------------//

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static byte[] encrypt(String data, String publicKey)
            throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(transformation_type);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    //---------------------DECRYPTION-------------------------------//

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        //PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64PrivateKey.getBytes());
        //PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static String decrypt(byte[] data, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Cipher cipher = Cipher.getInstance(transformation_type);
        //Cipher cipher = Cipher.getInstance(Cipher.ENCRYPT_MODE);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
        //return cipher.doFinal(data).toString();
    }

    public static String decrypt(String data, String base64PrivateKey)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
        //return decrypt(data.getBytes(), getPrivateKey(base64PrivateKey));
    }

}
// https://www.devglan.com/java8/rsa-encryption-decryption-java
// test https://www.devglan.com/online-tools/rsa-encryption-decryption