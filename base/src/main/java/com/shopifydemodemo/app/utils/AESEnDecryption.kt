package com.shopifydemodemo.app.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESEnDecryption {
    fun  data() {
        var msg = "978978546546546546978546"
        var keyStr = "khklhkhklhhighiogh"
        var ivStr = "huiguigkugkjugkjugjugug865312365361"
        var msg_byte = msg . toByteArray(Charsets.UTF_8)
        System.out.println("Before Encrypt: " + msg)
        var ans = encrypt (ivStr, keyStr, msg.toByteArray())
        System.out.println("After Encrypt: " +  String (ans, Charsets.UTF_8))
        var ansBase64 = encryptStrAndToBase64 (ivStr, keyStr, msg)
        System.out.println("After Encrypt & To Base64: " + ansBase64)
        var deans = decrypt (ivStr, keyStr, ans)
        System.out.println("After Decrypt: " +  String (deans, Charsets.UTF_8))
        var deansBase64 = decryptStrAndFromBase64 (ivStr, keyStr, ansBase64)
        System.out.println("After Decrypt & From Base64: " + deansBase64)
    }
      fun encrypt( ivStr:String,  keyStr:String,  bytes:ByteArray): ByteArray  {
            var md = MessageDigest.getInstance("MD5")
            md.update(ivStr.toByteArray())
            val ivBytes = md.digest()
            var sha = MessageDigest.getInstance("SHA-256")
            sha.update(keyStr.toByteArray())
            var keyBytes = sha.digest()
            return encrypt(ivBytes, keyBytes, bytes)
        }

      fun  encrypt( ivBytes:ByteArray,  keyBytes:ByteArray,  bytes:ByteArray) :ByteArray{
            var ivSpec:AlgorithmParameterSpec = IvParameterSpec(ivBytes)
            var newKey:SecretKeySpec =  SecretKeySpec(keyBytes, "AES")
            var cipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
            return cipher.doFinal(bytes)
        }

       fun decrypt( ivStr:String,  keyStr:String,  bytes:ByteArray):ByteArray{
          var  md = MessageDigest.getInstance("MD5")
          md.update(ivStr.toByteArray())
          var ivBytes = md.digest()
          var sha = MessageDigest.getInstance("SHA-256")
          sha.update(keyStr.toByteArray())
          var keyBytes = sha.digest()
          return decrypt(ivBytes, keyBytes, bytes)
      }
      fun  decrypt(ivBytes:ByteArray, keyBytes:ByteArray, bytes:ByteArray)  :ByteArray{
            var ivSpec =  IvParameterSpec(ivBytes);
            var newKey =  SecretKeySpec(keyBytes, "AES");
            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
            return cipher.doFinal(bytes);
        }
     fun  encryptStrAndToBase64( ivStr:String,  keyStr:String,  enStr:String) :String{
         var bytes = encrypt(keyStr, keyStr, enStr.toByteArray(Charsets.UTF_8));
         return  String(Base64.encode(bytes ,Base64.DEFAULT), Charsets.UTF_8);
     }

     fun  decryptStrAndFromBase64( ivStr:String,  keyStr:String,  deStr:String):String{
         var bytes = decrypt(keyStr, keyStr, Base64.decode(deStr.toByteArray(Charsets.UTF_8),Base64.DEFAULT));
         return  String(bytes, Charsets.UTF_8);
     }
}