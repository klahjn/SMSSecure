/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.user.smssecure.backend.ecceg;

import android.os.Debug;

import java.math.BigInteger;

/**
 *
 * @author atia
 */
public class ECCEG {

//    private final EllipticCurve ec;
//    public static BigInteger privateKey;
//    public static Point publicKey;
//    public static String plain;
//    public static Point[][] encrypted;
//    public static Point[] encoded;
//    public static Point[] decrypted;
//
//    public static Point getPublicKey() {
//        return publicKey;
//    }
//
//    public static BigInteger getPrivateKey() {
//        return privateKey;
//    }
//
//    public void setPublicKey(Point publicKey) {
//        this.publicKey = publicKey;
//    }
//
//    public void setPrivateKey(BigInteger privateKey) {
//        this.privateKey = privateKey;
//    }

    public static BigInteger generatePrivateKey(EllipticCurve ec){
        return Gen.generateK(ec.getP());
    }

    public static Point generatePublicKey(EllipticCurve ec, BigInteger privateKey){
        Point publicKey = ec.multiply(ec.getBasePoint(), privateKey);
        return publicKey;
    }
    public static Point[] encrypt(EllipticCurve ec, Point pm, BigInteger k, Point publicKey) {
        // k ∈ [1, p-1]
        // pc = [(kB, (Pm + kPb)]
        Point[] pc = new Point[2];
        pc[0] = ec.multiply(ec.getBasePoint(), k);
        pc[1] = ec.add(pm, ec.multiply(publicKey, k));
        return pc;
    }

    public static Point decrypt(EllipticCurve ec, Point[] pc, BigInteger privateKey) {
        // (Pm + kPB) - b.kB
        Point pm = ec.subtract(pc[1], ec.multiply(pc[0], privateKey));
        System.out.println("dec0: " + pc[0]);
        System.out.println("dec1: " + pc[1]);
        return pm;
    }

    public String getEncryptedString(Point[][] encrypted) {
        String enc = "";
        for (int y=0; y<encrypted.length; y++) {
//            System.out.println(encrypted[y][0]+ "," + encrypted[y][1]);
            enc += "" + encrypted[y][0] + encrypted[y][1] + "\n";
        }
        return enc;
    }

    public static byte[] getBytes(Point[][] encrypted) {
        byte[][][] byt = new byte[encrypted.length][2][];
        for (int y=0; y<encrypted.length; y++) {
            byt[y][0] = encrypted[y][0].toBytes();
            byt[y][1] = encrypted[y][1].toBytes();
        }

        int flen = 0;
        for (int y=0; y<byt.length; y++) {
            flen += byt[y][0].length;
            flen += byt[y][1].length;
        }
        byte[] retval = new byte[flen];

        int i = 0;
        for (byte[][] byt1 : byt) {
            for (int z = 0; z < byt1[0].length; z++) {
                retval[i] = byt1[0][z];
                i++;
            }
            for (int z = 0; z < byt1[1].length; z++) {
                retval[i] = byt1[1][z];
                i++;
            }
        }

        return retval;
    }

    public static Point[][] setFromBytes(byte[] b) {
        int len = analyzeLength(b);
        System.out.println(len);
        Point[][] encrypted = new Point[len/2][2];
        int curlen;
        int endbyte = 0;
        int i = 0;
        int insertion = 0;
        for (int num=0; num< len; num++) {
            curlen = (b[i] & 0xff);
            byte current[] = new byte[curlen+1];
            endbyte = i+curlen+1;
            for (int j=0; i<endbyte; j++, i++) {
                current[j] = b[i];
            }
            curlen = (b[i] & 0xff);
            byte current2[] = new byte[curlen+current.length+1];
            System.arraycopy(current, 0, current2, 0, current.length);
            endbyte = i+curlen+1;
            for (int j=0; i<endbyte; j++, i++) {
                current2[j+current.length] = b[i];
            }

            if (num%2 == 0) {
                System.out.println(num);
                encrypted[insertion][0] = new Point(current2);
                System.out.print(encrypted[insertion][0] + ",");
            } else {
                System.out.println(num);
                encrypted[insertion][1] = new Point(current2);
                System.out.println(encrypted[insertion][1]);
                insertion++;
            }
        }
        return encrypted;
    }

    public static int analyzeLength(byte[] b) {
        int totalPoints = 0;
        for (int i=0; i< b.length; i++) {
            int curlen = (b[i] & 0xff);
            i = i+curlen;
            totalPoints++;
        }
        return totalPoints/2;
    }

    public String doDecrypt(EllipticCurve ec, byte[] bt, BigInteger privateKey) {
        Point[][] encrypted = setFromBytes(bt);
//		ECCEG ecc = new ECCEG(ec);

        //System.out.println("\nDecrypting..." + encoded.length + " " + encrypted.length);

        Point[] decrypted = new Point[encrypted.length];
        for (int z=0; z<encrypted.length; z++) {
            //System.out.println("enc0: " + encrypted[z][0]);
            // System.out.println("enc1: " + encrypted[z][1]);
            decrypted[z] = decrypt(ec, encrypted[z], privateKey);
        }

        // System.out.println("\ndecoding");
        String newdec = Koblitz.decode(decrypted, new BigInteger("20"));

        System.out.println();
        System.out.println("decoded: " + newdec);
        return newdec;
    }

    public static String encrypt(BigInteger datain, BigInteger privateKey) {
        System.out.println("hash encrypt: " + datain);
//        privateKey = new BigInteger("2030457");
//        privateKey = new BigInteger("2");
//        Calculate e = HASH (m), where HASH is a cryptographic hash function, such as
//        SHA-1
//        2. Select a random integer k from [1,n − 1]
//        3. Calculate r = x1 (mod n), where (x1, y1) = k * G. If r = 0, go to step 2
//        4. Calculate s = k − 1(e + dA.r)(mod n). If s = 0, go to step 2
//        5. The signature is the pair (r, s)
//        EllipticCurve ec = new EllipticCurve(new BigInteger("-1"), new BigInteger("188"), new BigInteger("98764321261"));
//        Point b = ec.solveForY(new BigInteger("11"), new BigInteger("20"));
        EllipticCurve ec = new EllipticCurve(new BigInteger("-3"), new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16), new BigInteger("6277101735386680763835789423207666416083908700390324961279"));
        ec.setOrder(new BigInteger("6277101735386680763835789423176059013767194773182842284081"));
        Point b = new Point(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16), new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16));
//        EllipticCurve ec = new EllipticCurve(new BigInteger("2"), new BigInteger("1"), new BigInteger("5"));
//        Point b = new Point(new BigInteger("3"), new BigInteger("3"));
        ec.setBase(b);
        BigInteger K;
        Point h;
        BigInteger R;
        BigInteger s;
        do{
            do{
                K = Gen.generateK(ec.getN());
//                System.out.println("[debug] [encrypt] K1 = " + K);
                h = ec.multiply(b,K);
//                System.out.println("[debug] [encrypt] K2 = " + K);
                R = h.getX().mod(ec.getN());
//                System.out.println("[debug] [encrypt] K3 = " + K);
            }while(R.equals(BigInteger.ZERO));
            s = K.modInverse(ec.getN()).multiply(datain.add(R.multiply(privateKey))).
                    mod(ec.getN());
//            System.out.println("[debug] [encrypt] K4 = " + K);
        }while(s.equals(BigInteger.ZERO));
//        System.out.println("[debug] [encrypt] e = " + datain);
//        System.out.println("[debug] [encrypt] K = " + K);
//        System.out.println("[debug] [encrypt] h = " + h.getX() + "," + h.getY());
//        System.out.println("[debug] [encrypt] r = " + R);
//        System.out.println("[debug] [encrypt] S = " + s);
        return "["+R+","+s+"]";
    }
        //NOTE: AT THIS POINT, save the encrypted data in form of byte[] from getByte().

        // THEN, when decrypting, create new ECCEG object and do the following:
    public static boolean verify(String ds, String hashMsg, Point publicKey){
//        Verify that r and s are integers in [1,n − 1]. If not, the signature is invalid
//        2. Calculate e = HASH (m), where HASH is the same function used in the signature
//                generation
//        3. Calculate w = s −1 (mod n)
//        4. Calculate u1 = ew (mod n) and u2 = rw (mod n)
//        5. Calculate (x1, y1) = u1G + u2QA
//        6. The signature is valid if x1 = r(mod n), invalid otherwise
//        EllipticCurve ec = new EllipticCurve(new BigInteger("-1"), new BigInteger("188"), new BigInteger("98764321261"));
        EllipticCurve ec = new EllipticCurve(new BigInteger("-3"), new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16), new BigInteger("6277101735386680763835789423207666416083908700390324961279"));
        ec.setOrder(new BigInteger("6277101735386680763835789423176059013767194773182842284081"));
//        Point b = ec.solveForY(new BigInteger("11"), new BigInteger("20"));
        Point b = new Point(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16), new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16));
//        EllipticCurve ec = new EllipticCurve(new BigInteger("2"), new BigInteger("1"), new BigInteger("5"));
//        Point b = new Point(new BigInteger("3"), new BigInteger("3"));
        ec.setBase(b);
//        System.out.println("[debug] [verify] base: " + b.getX() + "," + b.getY());
//        publicKey = ec.multiply(ec.getBasePoint(), new BigInteger("2030457"));
//        publicKey = ec.multiply(ec.getBasePoint(), new BigInteger("2"));
//        System.out.println("public key: " + publicKey.getX() + ", " + publicKey.getY());
        String r = ds.substring(ds.indexOf('[')+1, ds.indexOf(',')).trim();
        String s = ds.substring(ds.indexOf(',')+1, ds.indexOf(']')).trim();
        System.out.println("[debug] [verify] rs: " + r + "," + s);
        BigInteger bigS = new BigInteger(s);
        BigInteger bigR = new BigInteger(r);
        if((bigR.compareTo(ec.getN()) < 0) && (bigR.compareTo(BigInteger.ZERO) > 0)
            && (bigS.compareTo(ec.getN()) < 0) && (bigS.compareTo(BigInteger.ZERO) > 0)) {
            BigInteger w = bigS.modInverse(ec.getN());
            BigInteger hash = new BigInteger(hashMsg, 16);
//            System.out.println("hash verify: " + hash);
            BigInteger u1 = (hash.multiply(w)).mod(ec.getN());
            BigInteger u2 = (bigR.multiply(w)).mod(ec.getN());

            Point temp1 = ec.multiply(ec.getBasePoint(), u1);
            Point temp2 = ec.multiply(publicKey, u2);
            Point temp = ec.add(temp1, temp2);
//            Point huba = ec.multiply(ec.getBasePoint(), u1.add(u2.multiply(new BigInteger("2030457"))));
//            BigInteger harusnyaS = u1.add(u2.multiply(new BigInteger("2030457"))).mod(ec.getP());
            BigInteger t = bigR.mod(ec.getN());
//            System.out.println("[debug] [verify] r = " + bigR);
//            System.out.println("[debug] [verify] s = " + bigS);
//            System.out.println("[debug] [verify] e = " + hash);
//            System.out.println("[debug] [verify] u1 = " + u1);
//            System.out.println("[debug] [verify] u2 = " + u2);
//            System.out.println("[debug] [verify] harusnyaS = " + harusnyaS);
//            System.out.println("[debug] [verify] point = " + temp.getX() + "," + temp.getY());
//            System.out.println("[debug] [verify] huba = " + huba.getX() + "," + huba.getY());
//            System.out.println("x = " + temp.getX());
//            System.out.println("x mod p = " + temp.getX().mod(ec.getP()));
//            System.out.println("t = " + t);
            return temp.getX().mod(ec.getN()).equals(t);
        } else return false;
    }

}
