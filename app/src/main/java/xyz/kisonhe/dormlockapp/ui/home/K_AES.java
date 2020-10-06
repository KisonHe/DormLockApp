package xyz.kisonhe.dormlockapp.ui.home;
import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;
//
////分三步：以AES举例说明
////
////        /**
////         * 加密方式：AES
////         * 工作模式：ECB,CBC,CTR,OFB,CFB
////         * 填充模式：PKCS5Padding，PKCS7Padding，ZEROPadding等等
////         */
////        String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";//AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
////
////
//////第一步：获取对象
////        Cipher cipher = Cipher.getInstance(String transformation);//传 CBC_PKCS5_PADDING
////
////
//////第二步：设置初始化参数
////        /**
////         * 第一个参数：传类型，是加密Cipher.ENCRYPT_MODE，还是解密Cipher.DECRYPT_MODE
////         * 第二个参数：传密钥key，我们这里传的是SecretKeySpec，它实现SecretKey，SecretKey实现Key接口。
////         *             SecretKeySpec keySpec = new SecretKeySpec(raw, AES);//第一个参数传密钥byte数组，第二个参数传加密类型也就是"AES"字符串即可
////         * 第三个参数：传偏移量AlgorithmParameterSpec，我们这里传的IvParameterSpec，他实现AlgorithmParameterSpec接口，iv偏移量传默认的16个0的字节数组
////         *              new IvParameterSpec(new byte[cipher.getBlockSize()])，这里是传的默认的16个0的byte数组，也是常用的方式
////         */
////        cipher.init(int opmode, Key key, AlgorithmParameterSpec params)
////
////
////        第三步：加密。传需要加密的字符串的byte数组
////        cipher.doFinal(byte[] input)
////
////        经过以上三步：AES加密就算完成了，加密之后就需要解密，辣么，怎么解密呢？
////
////        草鸡简单，第二步初始化的时候，第一个参数传解密即可，其他都是一样的。
//
//
////Every SecretKey has an associated algorithm name. You cannot use a SecretKey with algorithm "DES" in a context where an AES key is needed, for example.
////
////        In your code, the following line produces a SecretKey:
////
////        SecretKey secretKey = factory.generateSecret(spec);
////        However, at this point the key is not an AES key. If you were to call secretKey.getAlgorithm(), the result is "PBKDF2WithHmacSHA1". You need some way of telling Java that this is actually an AES key.
////
////        The easiest way to do this is to construct a new SecretKeySpec object, using the original key data and explicitly specifying the algorithm name:
////
////        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(),"AES");
////        Note: I would personally declare secret as a SecretKey, since I don't think you'll need to care about the concrete implementation after this.
////
//


public class K_AES {

    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";//AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String AES = "AES";//AES 加密


    /**
     * 加密过程
     *
     * @param raw   密钥的数组
     * @param clear 需要加密的byte数组
     * @return 加密后的String
     * @throws Exception 异常
     */

    /**
     * usage example
     * mAESShowText.setText(K_AES.encrypt("12345678azxcvbnm".getBytes(),mAESRaw.getText().toString().getBytes()));
     * */
    public static String encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        Log.d("localde",decrypt(raw,encrypted));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /*
     * 解密
     */
    public static String decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return new String(cipher.doFinal(encrypted));

    }

    public static int GetKey(){
        int Key = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 100 + Calendar.getInstance().get(Calendar.MINUTE);
        return Key;
    }

}


