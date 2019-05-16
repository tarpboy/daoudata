package com.devcrane.payfun.daou.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 11/2/2016.
 */

public class CertHelper {

    public static boolean cert(Activity at){

        Context context = at.getApplicationContext();
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        String cert = "";
        String extractedCert="";
        try {
            PackageInfo packageInfo = packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature certSignature =  packageInfo.signatures[0];
            MessageDigest msgDigest = MessageDigest.getInstance("SHA1");
            msgDigest.update(certSignature.toByteArray());
            cert = Base64Utils.base64Encode(msgDigest.digest());
            String apkPath = context.getPackageCodePath();
            BHelper.db("package info:"+ apkPath);
            BHelper.db("cert:"+cert);
            extractedCert = APKCertExtractor.execute(apkPath);
            BHelper.db("extractedCert:"+extractedCert);
            if(!cert.equals("") && cert.equals(extractedCert))
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return  false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return  false;
        }catch (Exception ex){
            ex.printStackTrace();
            return  false;
        }
        return  false;
    }

}
