package org.railsdroid.core.util;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Log;
/**
 * 
 * 对字符串进行加密解密的工具类,包含MD5，SHA-1，DES
 * 
 * 
 * @author 张青
 */
public abstract class EncryptionUtils{
	 
	/**
	 * 
	 * 对字符串进行MD5加密
	 *
	 * @param password 要加密的密码信息
	 * @return 密码进行MD5加密后的密码
	 * 
	 * @author 张青 2013-9-26
	 */
	public static String encryptToMD5(String password){
		byte[] digesta = null;
		try{
			//得到一个MD5的消息摘要
			MessageDigest mdi = MessageDigest.getInstance("MD5");
			//添加要进行计算摘要的信息
			mdi.update(password.getBytes());
			//得到该摘要
			digesta = mdi.digest();
		}catch (NoSuchAlgorithmException e)	{
			Log.e("无此加密方式：", e.toString());
		}
		
		return (byteToHex(digesta)).toLowerCase();
	}
	/**
	 * 
	 * 对字符串进行SHA－１加密,SHA-256及SHA-512生成密码太长，不支持
	 *
	 * @param password 要加密的密码信息
	 * @return　密码进行MD5加密后的密码
	 * 
	 * @author 张青 2013-9-26
	 */
	public static String encryptToSHA(String password){
		byte[] digesta = null;
		try{
			//得到一个MD5的消息摘要
			MessageDigest mdi = MessageDigest.getInstance("SHA-1");
			//添加要进行计算摘要的信息
			mdi.update(password.getBytes());
			//得到该摘要
			digesta = mdi.digest();
		}catch (NoSuchAlgorithmException e)	{
			Log.e("无此加密方式：", e.toString());
		}
		
		return   byteToHex(digesta);
				
	}
	/**
	 * 创建密匙
	 * 
	 * @param algorithm 加密算法,可用 DES,DESede,Blowfish
	 * @return SecretKey 秘密（对称）密钥
	 */
	public static SecretKey createSecretKey(String algorithm){
		// 声明KeyGenerator对象
		KeyGenerator keygen;
		// 声明 密钥对象
		SecretKey deskey = null;
		try {
			// 返回生成指定算法的秘密密钥的 KeyGenerator 对象
			keygen = KeyGenerator.getInstance(algorithm);
			// 生成一个密钥
			deskey = keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			Log.e("无此加密方式：", e.toString());
		}
		// 返回密匙
		return deskey;
	}
	/**
	 * 根据密匙进行DES加密
	 * 
	 * @param key 密匙
	 * @param info 要加密的信息
	 * @return String 加密后的信息
	 */
	public static String encryptToDES(SecretKey key, String info) {
		// 定义 加密算法,可用 DES,DESede,Blowfish
		String Algorithm = "DES";
		// 加密随机数生成器 (RNG),(可以不写)
		SecureRandom sr = new SecureRandom();
		// 定义要生成的密文
		byte[] cipherByte = null;
		try {
			// 得到加密/解密器
			Cipher c1 = Cipher.getInstance(Algorithm);
			// 用指定的密钥和模式初始化Cipher对象
			// 参数:(ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE,UNWRAP_MODE)
			c1.init(Cipher.ENCRYPT_MODE, key, sr);
			// 对要加密的内容进行编码处理,
			cipherByte = c1.doFinal(info.getBytes());
		} catch (Exception e) {
			Log.e("无此加密方式：", e.toString());
		}
		// 返回密文的十六进制形式
		return byteToHex(cipherByte);
	}
	/**
	 * 根据密匙进行DES解密
	 * 
	 * @param key 密匙
	 * @param sInfo 要解密的密文
	 * @return String 返回解密后信息
	 */
	public static String decryptByDES(SecretKey key, String sInfo) {
		// 定义 加密算法,
		String Algorithm = "DES";
		// 加密随机数生成器 (RNG)
		SecureRandom sr = new SecureRandom();
		byte[] cipherByte = null;
		try {
			// 得到加密/解密器
			Cipher c1 = Cipher.getInstance(Algorithm);
			// 用指定的密钥和模式初始化Cipher对象
			c1.init(Cipher.DECRYPT_MODE, key, sr);
			// 对要解密的内容进行编码处理
			cipherByte = c1.doFinal(hexToByte(sInfo));
		} catch (Exception e) {
			Log.e("无此加密方式：", e.toString());
		}
		return new String(cipherByte);
	}
	/**
	 * 十六进制字符串转化二进制
	 * 
	 * @param hex 十六进制字符串
	 * @return
	 */
	public static byte[] hexToByte(String hex) {
		byte[] ret = new byte[8];
		byte[] tmp = hex.getBytes();
		
		for (int i = 0; i < 8; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	
	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0 byte
	 * @param src1 byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
	
	
	/**
	 * 
	 * 将二进制转化为16进制字符串
	 *
	 * @param pwd 二进制字节数组
	 * @return String 转化16进制后的字符串
	 * 
	 * @author 张青 2013-9-26
	 */
	public static String byteToHex(byte[] pwd){
		StringBuilder hs = new StringBuilder("");
		String temp = "";
		for(int i = 0; i < pwd.length; i++){
			temp = Integer.toHexString(pwd[i]&0XFF);
			if(temp.length()==1){
				hs.append("0").append(temp);
			}else{
				hs.append(temp);
			}
		}
		return hs.toString().toUpperCase();
	}
	/**
	 * 创建密匙组，并将公匙，私匙放入到指定文件中
	 * 
	 * 默认放入mykeys.bat文件中
	 */
	public static void createPairKey() {
		try {
			// 根据特定的算法一个密钥对生成器
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA");
			// 加密随机数生成器 (RNG)
			SecureRandom random = new SecureRandom();
			// 重新设置此随机对象的种子
			random.setSeed(1000);
			// 使用给定的随机源（和默认的参数集合）初始化确定密钥大小的密钥对生成器
			keygen.initialize(512, random);// keygen.initialize(512);
			// 生成密钥组
			KeyPair keys = keygen.generateKeyPair();
			// 得到公匙
			PublicKey pubkey = keys.getPublic();
			// 得到私匙
			PrivateKey prikey = keys.getPrivate();
			// 将公匙私匙写入到文件当中
			doObjToFile("mykeys.bat", new Object[] { prikey, pubkey });
		} catch (NoSuchAlgorithmException e) {
			Log.e("不支持此种加密：", e.toString());
		}
	}
	
	/**
	 * 利用私匙对信息进行签名 把签名后的信息放入到指定的文件中
	 * 
	 * @param info 要签名的信息
	 * @param signfile 存入的文件
	 */
	public static void signToInfo(String info, String signfile) {
		// 从文件当中读取私匙
		PrivateKey myprikey = (PrivateKey) getObjFromFile("mykeys.bat", 1);
		// 从文件中读取公匙
		PublicKey mypubkey = (PublicKey) getObjFromFile("mykeys.bat", 2);
		try {
			// Signature 对象可用来生成和验证数字签名
			Signature signet = Signature.getInstance("DSA");
			// 初始化签署签名的私钥
			signet.initSign(myprikey);
			// 更新要由字节签名或验证的数据
			signet.update(info.getBytes());
			// 签署或验证所有更新字节的签名，返回签名
			byte[] signed = signet.sign();
			
			// 将数字签名,公匙,信息放入文件中
			doObjToFile(signfile, new Object[] { signed, mypubkey, info });
		} catch (Exception e) {
			Log.e("异常：",e.toString());
		}
	}
	
	/**
	 * 读取数字签名文件 根据公匙，签名，信息验证信息的合法性
	 * 
	 * @return true 验证成功 false 验证失败
	 */
	public static boolean validateSign(String signfile) {
		// 读取公匙
		PublicKey mypubkey = (PublicKey) getObjFromFile(signfile, 2);
		// 读取签名
		byte[] signed = (byte[]) getObjFromFile(signfile, 1);
		// 读取信息
		String info = (String) getObjFromFile(signfile, 3);
		try {
			// 初始一个Signature对象,并用公钥和签名进行验证
			Signature signetcheck = Signature.getInstance("DSA");
			// 初始化验证签名的公钥
			signetcheck.initVerify(mypubkey);
			// 使用指定的 byte 数组更新要签名或验证的数据
			signetcheck.update(info.getBytes());
			// 验证传入的签名
			return signetcheck.verify(signed);
		} catch (Exception e) {
			Log.e("异常：",e.toString());
			return false;
		}
	}
	
	/**
	 * 将指定的对象写入指定的文件
	 * 
	 * @param file 指定写入的文件
	 * @param objs 要写入的对象
	 */
	public static void doObjToFile(String file, Object[] objs) {
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			for (int i = 0; i < objs.length; i++) {
				oos.writeObject(objs[i]);
				
			}
		} catch (Exception e) {
			Log.e("文件不存在：",e.toString());
		} finally {
			try {
				oos.flush();
				oos.close();
			} catch (IOException e) {
				Log.e("文件不存在：",e.toString());
			}
		}
	}
	
	/**
	 * 返回在文件中指定位置的对象
	 * 
	 * @param file 指定的文件
	 * @param i 从1开始
	 * @return
	 */
	public static Object getObjFromFile(String file, int i) {
		ObjectInputStream ois = null;
		Object obj = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			for (int j = 0; j < i; j++) {
				obj = ois.readObject();
			}
		} catch (Exception e) {
			Log.e("文件不存在：",e.toString());
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				Log.e("文件不存在：",e.toString());
			}
		}
		return obj;
	}
}
