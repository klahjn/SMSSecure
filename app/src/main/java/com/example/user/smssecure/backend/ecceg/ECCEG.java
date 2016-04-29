/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.user.smssecure.backend.ecceg;

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

    public static String encrypt(String datain, BigInteger privateKey) {
        EllipticCurve ec = new EllipticCurve(new BigInteger("-1"), new BigInteger("188"), new BigInteger("98764321261"));
        Point b = ec.solveForY(new BigInteger("11"), new BigInteger("20"));
        ec.setBase(b);

        Point publicKey = ec.solveForY(privateKey, new BigInteger("20"));
        Point[] encoded = Koblitz.encode(datain, ec);

        Point[][] encrypted = new Point[encoded.length][2];
        BigInteger K = Gen.generateK(ec.getP());
        for (int x = 0; x < encoded.length; x++) {
            encrypted[x] = encrypt(ec,encoded[x], K, publicKey);
        }
        String hex = Point.bytesToHex(getBytes(encrypted));

        return hex;
    }
        //NOTE: AT THIS POINT, save the encrypted data in form of byte[] from getByte().

        // THEN, when decrypting, create new ECCEG object and do the following:
    public static String decrypt(String hex, BigInteger publicKeyX){
        EllipticCurve ec = new EllipticCurve(new BigInteger("-1"), new BigInteger("188"), new BigInteger("98764321261"));
        Point b = ec.solveForY(new BigInteger("11"), new BigInteger("20"));
        ec.setBase(b);

        byte[] bt = Point.hexToByte(hex);
        Point[] encryptedfromFile = new Point[bt.length];
        encryptedfromFile = Koblitz.encode(new String(bt), ec);
        Point[] decrypted = new Point[encryptedfromFile.length/2];
        for (int z=0; z<encryptedfromFile.length/2; z++) {
            Point[] enc = new Point[2];
            enc[0] = encryptedfromFile[z*2];
            enc[1] = encryptedfromFile[z*2+1];
            decrypted[z] = decrypt(ec,enc,publicKeyX);
            System.out.println(decrypted[z]);
        }

        System.out.println("\ndecoding");
        String newdec = Koblitz.decode(decrypted, new BigInteger("20"));

        System.out.println();
        System.out.println("WITHOUT doDecrypt: " + newdec);
        return newdec;
    }

}
