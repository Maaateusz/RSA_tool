package com.Maateusz.rsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

@Controller
public class RSAController {

    Logger logger = LoggerFactory.getLogger(RSAController.class);
//    private RSAkey rsakey;

    @GetMapping("/rsa")
    public String rsa(){
        return "rsa";
    }

//    @GetMapping("/generatersa2")
//    @ResponseBody
//    public String generatersa2(){
//        rsakey = new RSAkey();
//        return rsakey.toString();
//    }

    @GetMapping(value = "/generatersa")
    @ResponseBody
    public String generatersa() throws NoSuchAlgorithmException {
        RSAKeyPairGenerator rsaKeys = new RSAKeyPairGenerator();
        logger.info("Generated Keys: " + rsaKeys.toString());
        return rsaKeys.toString();
    }

//    @PostMapping("/encrypt")
//    @ResponseBody
//    public String encrypt(@RequestBody String public_key, String data)
//            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
//        String encrypted_text = new String(RSAKeyPairGenerator.encrypt(data, public_key));
//        //String s = Base64.getEncoder().encodeToString(bytes); new String(bytes, StandardCharsets.UTF_8);
//        return encrypted_text;
//    }

    //public void process(@RequestBody MultiValueMap<String, String> values) { logger.info("Values:{}", values);

    @PostMapping("/encrypt")
    @ResponseBody
    public String encrypt(@RequestBody HashMap<String, String> hashMap) //consumes = MediaType.APPLICATION_JSON_VALUE
            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
        logger.info("Request: " + hashMap);
        byte[] encrypted_text = RSAKeyPairGenerator.encrypt(hashMap.get("data"), hashMap.get("public_key"));
        logger.info("Encrypted Data: " + Base64.getEncoder().encodeToString(encrypted_text));
        return Base64.getEncoder().encodeToString(encrypted_text);
    }

    @PostMapping("/decrypt")
    @ResponseBody
    public String decrypt(@RequestBody HashMap<String, String> hashMap)
            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
        logger.info("Request Data: " + hashMap);
        String decrypted_text = RSAKeyPairGenerator.decrypt(hashMap.get("data"), hashMap.get("public_key"));
        logger.info("Decrypted Data: " + decrypted_text);
        return decrypted_text;
    }

}
