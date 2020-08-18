package com.Maateusz.rsa;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

public class RSAkey {

    private String create_date;
    private String public_key;
    private String private_key;
    private String data;
    private long p;
    private long q;

    public RSAkey(){
        create_RSA();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        create_date = sdf.format(date);
    }

    // VALID: { "RSAkey": [ {"create_date":"03/08/2020 11:06 AM","public_key":"public","private_key":"private"} ] }
    // RES:   {"RSAkey":{"create_date":"03/08/2020 17:18 PM","public_key":"public","private_key":"private"}}
    @Override
    public String toString() {
        return String.format(
                "{\"RSAkey\":{\"create_date\":\"%s\",\"public_key\":\"%s\",\"private_key\":\"%s\"}}",
                create_date, getPublic_key(), private_key);
    }

    public ObjectNode getJSON() {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("create_date", create_date);
        objectNode.put("public_key", getPublic_key());
        objectNode.put("private_key", private_key);
        return objectNode;
    }

    private void find_p_q(){
        Random generator = new Random();
        //generator.setSeed(System.currentTimeMillis());
        
        do{
            p = Math.abs(generator.nextLong()); // + (1L << 63)
        }
        while(!isPrime2(p));

        do{
            q = Math.abs(generator.nextLong());
        }
        while(!isPrime2(q));
    }

    public boolean isPrime(long number) { // very very slow
        return number > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(number)).noneMatch(n -> (number % n == 0));
    }

    //uses what's known as “Miller – Rabin and Lucas – Lehmer” primality tests
    public boolean isPrime2(long number) {
        BigInteger bigInt = BigInteger.valueOf(number);
        return bigInt.isProbablePrime(100);
    }

    private void create_RSA(){
        find_p_q();
        long phi = (p-1)*(q-1); //funkcja Eulera
        long n = p*q; //moduł

        long e; //wykładnik publiczny. względnie pierwsza z wyliczoną wartością funkcji Eulera Ø; NWD(e, Ø) = 1
        long d; //wykładnik prywatny, odwrotność modulo Ø liczby e

        for(e = 3; nwd(e, phi) != 1; e += 2);
        d = odwr_mod(e, phi);

        setPublic_key("(" + e + ", " + n + ")");
        private_key = "(" + d + ", " + n + ")";
    }

    // Funkcja obliczająca NWD dla dwóch liczb
    private long nwd(long a, long b)
    {
        long t;
        while(b != 0)
        {
            t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    // Funkcja obliczania odwrotności modulo n
    private long odwr_mod(long a, long n)
    {
        long a0, n0, p0, p1, q, r, t;
        p0 = 0; p1 = 1; a0 = a; n0 = n;
        q  = n0 / a0;
        r  = n0 % a0;
        while(r > 0)
        {
            t = p0 - q * p1;
            if(t >= 0) t = t % n;
            else t = n - ((-t) % n );
            p0 = p1;
            p1 = t;
            n0 = a0;
            a0 = r;
            q  = n0 / a0;
            r  = n0 % a0;
        }
        return p1;
    }

    // Funkcja oblicza modulo potęgę podanej liczby; pot_mod(t, e, n); t - kod rsa;
    private long pot_mod (long a, long w, long n)
    {
        long pot, wyn, q;
        // wykładnik w rozbieramy na sumę potęg 2
        // przy pomocy algorytmu Hornera. Dla reszt
        // niezerowych tworzymy iloczyn potęg a modulo n.
        pot = a; wyn = 1;
        for(q = w; q > 0; q /= 2)
        {
            if(q % 2 != 0) wyn = (wyn * pot) % n;
            pot = (pot * pot) % n; // kolejna potęga
        }
        return wyn;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
// https://eduinf.waw.pl/inf/alg/001_search/0067.php
//https://www.devglan.com/java8/rsa-encryption-decryption-java