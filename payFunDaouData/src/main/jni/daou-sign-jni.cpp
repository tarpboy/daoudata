//
// Created by Administrator on 1/13/2017.
//


#include "daou-lib.cpp"



extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_devcrane_payfun_daou_utility_DaouSignHelper_convertSignature(JNIEnv *env,
                                                                          jobject instance,
                                                                          jbyteArray bmp_) {

   int len_source = (*env).GetArrayLength(bmp_);

   __android_log_print(ANDROID_LOG_INFO,"DaouSign", "input size: %d",len_source);
   unsigned char* source_bmp =  new unsigned char[len_source];
   env->GetByteArrayRegion(bmp_,0,len_source, reinterpret_cast<jbyte *>(source_bmp));




   char tmpSignImg[2048 + 1];
   char tmpRleSign[2048 + 1];
   char tmpSignData[2048 + 1];
   memset(tmpSignImg, 0x00, sizeof(tmpSignImg));
   memset(tmpRleSign, 0x00, sizeof(tmpRleSign));
   memset(tmpSignData, 0x00, sizeof(tmpSignData));

   int  lenSignData = 0;
   BmpToSignImage((unsigned char*)tmpSignImg, source_bmp);
   __android_log_print(ANDROID_LOG_INFO,"DaouSign", "before compress");
   int final_size = NULLRLECompress((unsigned char*)tmpSignImg, (unsigned char*)tmpRleSign);

   encodeBASE64((unsigned char*)tmpRleSign, final_size, tmpSignData, &lenSignData);
   char destSignData[lenSignData+12];
   memset(destSignData, 0x00, sizeof(destSignData));
   int rc=0;
   sprintf(destSignData,"%-4s%-2s%-2s%04d", "ESMS", "00", "96", lenSignData);
   rc = 8 + 4;

   memcpy(destSignData + rc, tmpSignData, lenSignData);
   rc += lenSignData;

   __android_log_print(ANDROID_LOG_INFO,"DaouSign", "final size: %d",rc);

   jbyteArray result = env->NewByteArray (rc);
   env->SetByteArrayRegion (result, 0, rc,  reinterpret_cast<jbyte*>((unsigned char*)destSignData));

//   __android_log_print(ANDROID_LOG_INFO,"DaouSign", "final size: %d",final_size);
//   jbyteArray result = env->NewByteArray (final_size);
//   env->SetByteArrayRegion (result, 0, final_size,  reinterpret_cast<jbyte*>(tmpRleSign));




   //delete[] dest_bmp;
   env->ReleaseByteArrayElements(bmp_,(jbyte*)source_bmp, JNI_ABORT);
   //delete[] final_bmp;
   //env->DeleteLocalRef(bmp_);
   //jbyteArray result = env->NewByteArray (1024);
   return result;
}

