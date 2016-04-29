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
public class Point {
    private BigInteger x, y;
    private boolean infinite;
    
    public Point() {
        x = new BigInteger("0");
        y = new BigInteger("0");
    }
    
    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
	
	public Point(byte[] b) {
		int xlen = (b[0] & 0xff);
		int ylen = (b[xlen+1] & 0xff);
		byte[] xb = new byte[xlen];
		byte[] yb = new byte[ylen];
		System.arraycopy(b, 1, xb, 0, xlen);
		System.arraycopy(b, xlen+2, yb, 0, ylen);
		x = new BigInteger(xb);
		y = new BigInteger(yb);
	}
    
    public BigInteger getX() {
        return x;
    }
    
    public BigInteger getY() {
        return y;
    }
    
    public void setY(BigInteger y) {
        this.y = y;
    }
    
    public void setX(BigInteger x) {
        this.x = x;
    }
    
	@Override
    public String toString() {
        String pointstr;
        pointstr = bytesToHex(this.toBytes());
        return pointstr;
    }
	
	public byte[] toBytes() {
		byte[] xbyte = x.toByteArray();
		byte[] ybyte = y.toByteArray();
		int len = 2 + xbyte.length + ybyte.length;
		
		byte[] retval = new byte[len];
		
		retval[0] = (byte) xbyte.length;
		System.arraycopy(xbyte, 0, retval, 1, xbyte.length);
		
		retval[xbyte.length+1] = (byte) ybyte.length;
		System.arraycopy(ybyte, 0, retval, xbyte.length+2, ybyte.length);
		
		return retval;
	}
    
    public boolean isEqual(Point b) {
        return this.x.equals(b.x) && this.y.equals(b.y);
    }
    
    public boolean isInf() {
        return this.x.equals(BigInteger.ZERO) && this.y.equals(BigInteger.ZERO);
    }
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static String bytesToHex(byte bytes) {
		char[] hexChars = new char[2];
		int v = bytes & 0xFF;
		hexChars[0] = hexArray[v >>> 4];
		hexChars[1] = hexArray[v & 0x0F];
		return new String(hexChars);
	}
	
	public static byte[] hexToByte(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
}
	
	 public static void main(String[] args) {
		 BigInteger a = new BigInteger("128");
		 Point x = new Point(a,a);
		 System.out.println(bytesToHex(x.toBytes()));
		 Point y = new Point(x.toBytes());
		 System.out.println(bytesToHex(y.toBytes()));
	 }
}
