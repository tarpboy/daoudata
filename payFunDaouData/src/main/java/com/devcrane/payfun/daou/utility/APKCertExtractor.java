package com.devcrane.payfun.daou.utility;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



/**
 * APKCertExtractor
 * 
 * - APK 파일내의 인증 정보를 SHA1, base64 로 만들어 반환한다.
 * - APK 파일의 유효성을 검사한다. 
 * - 2개 이상의 서명을 사용하는 APK 파일에 대하여 사용할 수 없다. 
 *   하지만, Play store 에서도 2개 이상의 서명을 갖고 있는 APK 를 허용하지 않는다.  
 *     
 * ::사용법
 *   - APKCertExtractor.execute(apk 파일 경로);
 *   - 인증 검증 실패시 APKCertExtractionException 예외 발생.
 *   
 * http://dev.re.kr
 */
public class APKCertExtractor {
	
	public static void main(String[] args) {
		String cert = "";
		try {
			cert = APKCertExtractor.execute( "test.apk");
		} catch (APKCertExtractionException e) {
			e.printStackTrace();
			cert = e.getMessage();
		}
		System.out.println(cert);
	}
	

	/**
	 * 인증서 지문을 반환한다. 
	 * @param apkFilePath APK 파일의 경로. 
	 * @return
	 * @throws APKCertExtractionException
	 */
	@SuppressWarnings("resource")
	public static String execute(String apkFilePath) throws APKCertExtractionException {
				
		try {
			// JarFile 클래스는 java.util.zip.ZipFile 를 상속받아 구현되었다.
			// jar 파일에 대한 설명은 다음 블로그 페이지에 자세히 소개되어 있다. 
			// http//www.yunsobi.com/blog/62
			// 이 클래스는 jar(zip) 의 엔트리(파일 정보) 및 파일을 읽기 위하여 사용된다.
			JarFile jarFile = new JarFile(apkFilePath);
			// APK 파일 내의 AndroidManifest.xml 의 엔트리와 인증 정보를 읽어온다.  
			JarEntry manifestEntry = jarFile.getJarEntry("AndroidManifest.xml");
			if(manifestEntry == null) 
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.WrongAPKFormat, apkFilePath, null);
			
			Certificate[] certs = loadCertificates(jarFile, manifestEntry);
			if(certs == null || certs.length == 0) 
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.WrongCert, manifestEntry.getName(), null);
			
			Certificate cert= certs[0];

			// APK 파일 내의 모든 엔트리의 인증을 검증한다.  
			verifCertificates(jarFile, cert);
			
			// 바이트 배열 타입의 인증 정보를 SHA1 Base64로 변환.
			String hash;
			try {
				hash = certToSHA1(cert);
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
				// 인증서에 문제 있을 때 발생. 
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.WrongCert, apkFilePath, e);
			}
			return hash;
			
		} 
		// 파일 경로등에 문제 있을 때 발생한다. 
		catch (IOException e) {
			throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.ReadFail, apkFilePath, e);
		}
		 
	}
		
	/**
	 * APK 내부 파일의 인증을 확인한다.
	 * @param jarFile
	 * @param cert
	 */
	private static void verifCertificates(JarFile jarFile,Certificate cert) throws APKCertExtractionException {
		Enumeration<JarEntry> entries = jarFile.entries();
		
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry) entries.nextElement();
			// 서명되지 않는 디렉토리인 META-INF 는 건너뛴다. 
			if (jarEntry.isDirectory() || jarEntry.getName().startsWith("META-INF/") || jarEntry.getName().contains(".DS_Store")) {
				continue;
			}			
			Certificate[] certs = null;
			certs = loadCertificates(jarFile, jarEntry);
			if(certs == null || certs.length == 0) 
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.WrongCert, jarEntry.getName(), null);
			Certificate localCert = certs[0];
			// 인증 정보가 없는(서명이 되지 않은) 파일 발견. 
			// APK 압축을 풀고 임의의 파일을 넣거나 제거하여 다시 APK 파일로 압축했을 때 발생한다. 
			if (localCert == null) {				
				try { jarFile.close();} 
				catch (IOException e) { e.printStackTrace(); }
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.ForgeryAPK, jarEntry.getName(), null);
			}  
			// 인증 정보가 서로 다른 엔트리 발견. 
			// 물론 설치는 안 되겠지만, 위변조 시도되는 앱으로 의심된다. 
			else if (!cert.equals(localCert)) { 
				try { jarFile.close();} 
				catch (IOException e) { e.printStackTrace(); }
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.ForgeryAPK, jarEntry.getName(), null);
			}
		}
	}
	 
	 /**
	  * Certificates 객체를 반환한다.
	 * @throws APKCertExtractionException 
	  */
	private static Certificate[] loadCertificates(JarFile jarFile, JarEntry jarEntry) throws APKCertExtractionException {
		if(jarEntry == null || jarFile == null) return null;
		try {
			// JarEntry 로부터 Certificate 객체를 얻기 위해서는 JarEntry 를 검증하기 위하여 끝까지 다 읽어야 한다.
			// 이 과정에서 내부적으로 JarVerifier 클래스를 통하여 인증에 대한 검증이 이뤄어진다.
			byte[] buffer = new byte[1024];
			InputStream is = jarFile.getInputStream(jarEntry);
			 
			try {
				while (is.read(buffer, 0, buffer.length) != -1) {}
			// 테스트 결과 MATA-INF 폴더의 파일 내에 해당 엔트리의 인증값들은 있지만, 
			// 실제 파일이 존재하지 않을 경우 아래 예외가 발생한다.
			// 역시 위변조된 앱일 가능성이 크다. 
			// 물론 이 경우도 일반적인 안드로이드 폰에서는 설치조차 되지 않는다.
			} catch(SecurityException e) {
				throw APKCertExtractionException.newInstance(APKCertExtractionException.ErrType.ForgeryAPK, jarEntry.getName(), e);
			}
			is.close();
			buffer = null;
			return (Certificate[])jarEntry.getCertificates();
		} catch (IOException e) {
			System.err.println("Exception reading " + jarEntry.getName() + " in "
					+ jarFile.getName() + " " + e);
		}
		
		return null;
	}

	/**
	 * 서명 깂을 SHA1 해쉬로 변경하여 Base64 로 만든 String 값으로 반환한다. 
	 * @param cert
	 * @return
	 * @throws CertificateEncodingException 
	 */
	private static String certToSHA1(Certificate cert) throws CertificateEncodingException {
		byte[] certWith = null;
		// X509 인증 정보를 ASN.1 DER 구조의 byte 배열로 반환한다. 
		certWith = cert.getEncoded();
		MessageDigest md = null;
		try {
			// 인증 정보를 SHA1 해쉬로 변경. 
			md = MessageDigest.getInstance("SHA1");
			md.update(certWith);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return Base64Utils.base64Encode(md.digest());

	}

	/**
	 * 인증서 추출 예외상황.
	 * @author ice3x2
	 */
	public static class APKCertExtractionException extends Exception {
		private static final long serialVersionUID = 6796836839897143903L;
		private ErrType mError;
		protected static APKCertExtractionException newInstance(ErrType errType, String path, Throwable throwable) {
			String message = ErrType.toMessage(errType) + " (" + path + ")";
			APKCertExtractionException apkCertExtractionException = 	
					(throwable == null)?new APKCertExtractionException(message):new APKCertExtractionException(message, throwable);
			apkCertExtractionException.mError = errType;
			return apkCertExtractionException;
		}
		
		private APKCertExtractionException(String message, Throwable throwable) {
			super(message, throwable);
		}
		private  APKCertExtractionException(String message) {
			super(message);
		}
		/**
		 * 에러 타입을 반환한다.
		 * @return
		 */
		public ErrType getError() {
			return mError;
		}
		public static enum ErrType {
			/**
			 * 잘못된 APK 파일 포맷.
			 */
			WrongAPKFormat("Wrong APK file format."),
			/**
			 * APK 파일을 읽을 수 없음. 
			 */
			ReadFail("APK file read failed."),
			/**
			 * 잘못된 인증서. 파일에 대한 인증 정보가 존재하지 않는다.
			 */
			WrongCert("Wrong certificate. Certificate verified failed."),
			/**
			 * 위변조가 의심되는 APK. 인증 정보 검증에 문제가 있다.
			 */
			ForgeryAPK("Wrong certificate. This package is suspected with forgery apk.");
			private String message;
			private ErrType(String value) {
				message = value;
			}
			protected static String toMessage(ErrType errType) {
				return errType.message;
			}
		}
	}
}
