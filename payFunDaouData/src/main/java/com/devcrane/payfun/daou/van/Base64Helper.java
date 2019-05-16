package com.devcrane.payfun.daou.van;

public class Base64Helper {
private static byte[] mBase64EncMap, mBase64DecMap;
	
	static{
		byte[] base64Map = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J',
				(byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', 
				(byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', 
				(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', 
				(byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', 
				(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };
		mBase64EncMap = base64Map;
		byte[] base64DecodeMap={ (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127,
				(byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127,
				(byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127,
				(byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127,
				(byte)127, (byte)127, (byte)127,  (byte)(byte)62, (byte)127, (byte)127, (byte)127,  (byte)63,  (byte)52,  (byte)53,
				(byte)54,  (byte)55,  (byte)56,  (byte)57,  (byte)58,  (byte)59,  (byte)60,  (byte)61, (byte)127, (byte)127,
				(byte)127,  (byte)64, (byte)127, (byte)127, (byte)127,   (byte)0,   (byte)1,   (byte)2,   (byte)3,   (byte)4,
				(byte)5,   (byte)6,   (byte)7,   (byte)8,   (byte)9,  (byte)10,  (byte)11,  (byte)12,  (byte)13,  (byte)14,
				(byte)15,  (byte)16,  (byte)17,  (byte)18,  (byte)19,  (byte)20,  (byte)21,  (byte)22,  (byte)23,  (byte)24,
				(byte)25, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127,  (byte)26,  (byte)27,  (byte)28,
			     (byte)29,  (byte)30,  (byte)31,  (byte)32,  (byte)33,  (byte)34,  (byte)35,  (byte)36,  (byte)37,  (byte)38,
			     (byte)39,  (byte)40,  (byte)41,  (byte)42,  (byte)43,  (byte)44,  (byte)45,  (byte)46,  (byte)47,  (byte)48,
			     (byte)49,  (byte)50,  (byte)51, (byte)127, (byte)127, (byte)127, (byte)127, (byte)127};
		mBase64DecMap = base64DecodeMap;
	}
	public static String encode(byte[] aData){
		
		if ((aData == null) || (aData.length == 0))
			throw new IllegalArgumentException("Can not encode NULL or empty byte array.");
		int src_len = aData.length;
		int dst_len = ((aData.length+3) / 3) * 4;
		byte encodedBuf[] = new byte[dst_len];
		
		int i, n, C1, C2, C3;
		n = ( src_len << 3 ) / 6;
		switch( ( src_len << 3 ) - ( n * 6 ) )
	    {
	        case  2: n += 3; break;
	        case  4: n += 2; break;
	        default: break;
	    }
		int srcIndex=0,dstIndex = 0;
		n = ( src_len / 3 ) * 3;
		for(i=0,srcIndex=0; i<n-2; i+=3){
		
			C1 = aData[srcIndex++];
			C2 = aData[srcIndex++];
			C3 = aData[srcIndex++];
			encodedBuf[dstIndex++] = mBase64EncMap[(C1>>2) & 0x3f];
			encodedBuf[dstIndex++] = mBase64EncMap[(((C1 &  3) << 4) + (C2 >> 4)) & 0x3F];
			encodedBuf[dstIndex++] = mBase64EncMap[(((C2 & 15) << 2) + (C3 >> 6)) & 0x3F];
			encodedBuf[dstIndex++] = mBase64EncMap[C3 & 0x3F];
			
		}
		if(i<src_len){
			C1 = aData[srcIndex++];
	        C2 = ( ( i + 1 ) < src_len ) ? aData[srcIndex++] : 0;
	        encodedBuf[dstIndex++] = mBase64EncMap[(C1 >> 2) & 0x3F];
	        encodedBuf[dstIndex++] = mBase64EncMap[(((C1 & 3) << 4) + (C2 >> 4)) & 0x3F];
	        if( ( i + 1 ) < src_len )
	        	encodedBuf[dstIndex++] = mBase64EncMap[((C2 & 15) << 2) & 0x3F];
	        else
	        	encodedBuf[dstIndex++] = (byte)'=';
	        encodedBuf[dstIndex++] = (byte)'=';
		}
		byte[] retBuff = new byte[dstIndex];
		System.arraycopy(encodedBuf, 0, retBuff, 0, dstIndex);
		String result = new String(retBuff);
		return result;
		
	}
public static byte[] decode(String aData){
		
		int i, n, j, x;
		byte[] src = aData.getBytes();
		int src_len = src.length;
		
		if ((aData == null) || (aData.length() == 0))
			throw new IllegalArgumentException("Can not decode NULL or empty string.");
		
		for( i = n = j = 0; i < src_len; i++ ){
			if( ( src_len - i ) >= 2 && src[i] == '\r' && src[i + 1] == '\n' )
	            continue;
	        if( src[i] == '\n' )
	            continue;
	        if( src[i] == '=' && ++j > 2 )
	        	throw new IllegalArgumentException("invalid character");
	        if( src[i] > 127 || mBase64DecMap[src[i]] == 127 )
	        	throw new IllegalArgumentException("invalid character");
	        if( mBase64DecMap[src[i]] < 64 && j != 0 )
	        	throw new IllegalArgumentException("invalid character");
	        n++;
		}
		n = ( ( n * 6 ) + 7 ) >> 3;
        n -= j;
        int srcIndex=0,dstIndex = 0; 
        byte decodedBuf[] = new byte[src_len - src.length / 4];
        for( j = 3, n = x = 0; i > 0; i--, srcIndex++ )
        {
             if( src[srcIndex] == '\r' || src[srcIndex] == '\n' )
                 continue;

             if((int) mBase64DecMap[src[srcIndex]] == 64 )
            	 j-=1;
             x  = ( x << 6 ) | ( mBase64DecMap[src[srcIndex]] & 0x3F );

             if( ++n == 4 )
             {
                 n = 0;
                 if( j > 0 ) decodedBuf[dstIndex++] = (byte)( x >> 16 );
                 if( j > 1 ) decodedBuf[dstIndex++] = (byte)( x >>  8 );
                 if( j > 2 ) decodedBuf[dstIndex++] = (byte)( x       );
             }
         }
        byte[] retBuff = new byte[dstIndex];
        
        System.arraycopy(decodedBuf, 0, retBuff, 0, dstIndex);
		return retBuff;
	}

}
