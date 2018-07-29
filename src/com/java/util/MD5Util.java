package com.java.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MD5Util {

	public static String MD5Encode(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5= MessageDigest.getInstance("md5");
		//jdk1.8
		//java.util.Base64.getEncoder().encodeToString("����)//����
		//java.util.Base64.getDecoder().decode(�ַ������ݣ�//����
		return Base64.getEncoder().encodeToString((md5.digest(str.getBytes("utf-8"))));
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		System.out.println(MD5Util.MD5Encode("12345"));
	}
}
