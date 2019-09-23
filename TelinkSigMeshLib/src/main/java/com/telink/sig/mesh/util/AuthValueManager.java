package com.telink.sig.mesh.util;

import android.util.Log;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.macs.CMac;
import org.spongycastle.crypto.params.KeyParameter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: iotDemo
 * @description: ${description}
 * @author: jarLiao
 * @create: 2019-07-26
 **/
public class AuthValueManager {

//    public static void main(String[] args) {
//
//        String ss = "d6 00 7f cc 1f b0 2d b2 72 a6 ac 6f c4 9b 40 0e ";
//        String[] name =  ss.split(" ");
////        Integer.valueOf(name)
//        byte[] data = new byte[16];
//        for (int i = 0; i < name.length; i++) {
//            int x = Integer.valueOf(name[i],16);
//            data[i] = (byte) x;
//        }
//
//        String tripleStr = getTripleForMac("abcdf0f1f2f3").getTripleString();
//        System.out.println("tripleStr = "+tripleStr);
//        byte[] encode = getSHA256(tripleStr);
//        System.out.println("SHA256 byte = "+getHexString(encode));
//        byte[] authValue = new byte[16];
//        System.arraycopy(encode,0,authValue,0,16);
//        System.out.println("auth byte = "+getHexString(authValue));
//
////        String s1 = "8e a1 ea 01 37 0e 8f ae ab 51 2f 2b e9 c4 99 c8 ";
////        String[] name =  s1.split(" ");
//////        Integer.valueOf(name)
////        byte[] data1 = new byte[16];
////        for (int i = 0; i < name.length; i++) {
////            int x = Integer.valueOf(name[i],16);
////            data1[i] = (byte) x;
////        }
////
////        String s2 = "0c da aa 2a cf 2f b4 00 03 dd be fd 95 79 4c 5d ";
////        String[] name2 =  s2.split(" ");
//////        Integer.valueOf(name)
////        byte[] data2 = new byte[16];
////        for (int i = 0; i < name.length; i++) {
////            int x = Integer.valueOf(name2[i],16);
////            data2[i] = (byte) x;
////        }
////
////        System.out.println("us -"+compareConfirm(data1,data2));
//    }

    private static Map<String,String> authMap = new HashMap<>();
    public static void setAuthData(String mac,String authStr){
        authMap.put(mac,authStr);
    }

    public static byte[] getAuthData(String mac){
        byte[] data = new byte[16];
        String str = authMap.get(mac);
        String[] splits =  str.split(" ");
        for (int i = 0; i < splits.length; i++) {
            int x = Integer.valueOf(splits[i],16);
            data[i] = (byte) x;
        }
        return data;
    }

    public static byte[] getAuthValue(String mac){
        String tripleStr = getTripleForMac(mac).getTripleString();
        byte[] encode = getSHA256(tripleStr);
        byte[] authValue = new byte[16];
        System.arraycopy(encode,0,authValue,0,16);
        return authValue;
    }

    public static byte[] aesCmac(byte[] content, byte[] key) {
        CipherParameters cipherParameters = new KeyParameter(key);
        BlockCipher blockCipher = new AESEngine();
        CMac mac = new CMac(blockCipher);

        mac.init(cipherParameters);
        mac.update(content, 0, content.length);
        byte[] re = new byte[16];
        mac.doFinal(re, 0);
        return re;
    }

    public static List<Triple> getTripleList(){
        List<Triple> tripleList = new ArrayList<>();
        tripleList.add(new Triple(168930,"53daed805bc534a4a93c825ed20a7063","abcdf0f1f2f3"));
        tripleList.add(new Triple(4973,"3c67796ab744debc153bc0f163acf5db","f8a7630c1738"));
        tripleList.add(new Triple(4973,"1c2ab6ca0decb025f7b804409418d484","f8a7630c1739"));
        tripleList.add(new Triple(4973,"23579ec462dfb2acc1c33ffab729e168","f8a7630c173a"));
        tripleList.add(new Triple(4973,"b4f80dc36d55fbb84a76afb208c5e87c","f8a7630c173b"));
        tripleList.add(new Triple(4973,"d9c096c75849fbe1f0a16e13e344ad82","f8a7630c173c"));
        tripleList.add(new Triple(4973,"89d248f9213be66919cc75b66996c58c","f8a7630c173d"));
        tripleList.add(new Triple(4973,"e3b9d5cd0bb02ad8be1afffa3f00690e","f8a7630c173e"));
        tripleList.add(new Triple(4973,"cc8af2e86cb1cede68cfbc88835ed4d9","f8a7630c173f"));
        tripleList.add(new Triple(4973,"6ef845a385c87a77103fd240f6ee6443","f8a7630c1740"));
        tripleList.add(new Triple(4973,"10223d84fa05244f86311fda94f0e6b9","f8a7630c1741"));
        return tripleList;
    }

    public static Triple getTripleForMac(String mac){
        for (Triple triple : getTripleList()) {
            if (triple.mac.equals(mac)) {
                return triple;
            }
        }
        return null;
    }

    /**
     * 利用java原生的类实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    public static byte[] getSHA256(String str){
        MessageDigest messageDigest;
        byte[] encode = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes());
            encode =  messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encode;
    }

    public static boolean compareConfirm(byte[] confirm, byte[] aConfirm){
        String a = getHexString(aConfirm);
        String b = getHexString(confirm);
        System.out.println("compareConfirm() called with: confirm = [" + a + "], aConfirm = [" + b  + "]");
        return a.equals(b);
    }



    public static String getHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x",b)).append(" ");
        }
        return builder.toString();
    }

    public static class Triple{
        int productId;
        String productSecret;
        String mac;

        public Triple(int productId, String productSecret, String mac) {
            this.productId = productId;
            this.productSecret = productSecret;
            this.mac = mac;
        }

        public String getTripleString(){
            StringBuilder stringBuilder = new StringBuilder();
            String hexPid = String.format("%08x",productId);
            stringBuilder.append(hexPid).append(",").append(mac).append(",").append(productSecret);
            return stringBuilder.toString();
        }
    }

}
