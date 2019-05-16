//
// Created by Administrator on 1/13/2017.
//

#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>

typedef unsigned char    BYTE;
typedef unsigned int    UINT;
typedef unsigned int	 DWORD;


void h2a_2(BYTE *src, BYTE *dest, BYTE len)
{
    BYTE i, j;
    BYTE tmp;

    j = 0;
    for (i = 0; i < len; i++)
    {
        tmp = (src[i] >> 4) & 0x0f;
        if (tmp > 9) tmp += 7;
        dest[j++] = tmp + '0';

        tmp = src[i] & 0x0f;
        if (tmp > 9) tmp += 7;
        dest[j++] = tmp + '0';
    }
}
//--- h2a_2 End

void n2h_2(BYTE *sbuf, BYTE *dbuf, BYTE len)
{
    unsigned int i, j;
    BYTE tmp;

    j = 0;
    for (i = 0; i < len;)
    {
        dbuf[j] = sbuf[i++] - 0x30;
        if (dbuf[j] > 9) dbuf[j] -= 7;
        dbuf[j] = dbuf[j] << 4;
        tmp = sbuf[i++] - 0x30;
        if (tmp > 9) tmp -= 7;
        dbuf[j] |= (tmp & 0x0f);
        j++;
    }
}
//--- n2h_2 End

void hexcpy(BYTE *dest, BYTE *sour, BYTE len, BYTE type)
{
    if (!type)
    {
        h2a_2(sour, dest, len);
    }
    else
    {
        n2h_2(sour, dest, len);
    }
}


//--- hexcpy End


void SetDataPos(BYTE *pDest, UINT nPos)
{
    BYTE *pMsg;
    int nBit;

    pMsg = pDest;

    if (nPos > 0) {
        nBit = (nPos - 1) % 8;
        pMsg += (nPos - 1) / 8;
    }
    else {
        return;
    }

    *pMsg = *pMsg | (0x0001 << (7 - nBit));
}


//--- BmpToSignImage
//		SrcBmp∏¶ DestSignµ•¿Ã≈∏∑Œ ∫Ø∞Ê
//		Parameter
//          DestSign : (O) DestSign¿∏∑Œ ∫Ø∞Êµ» µ•¿Ã≈Õ
//          SrcBmp : (I) SrcBmp

void BmpToSignImage(BYTE *DestSign, BYTE *SrcBmp)
{
    BYTE *pMsg;
    BYTE *pX;
    UINT x, y;
    UINT i, j, k;
    UINT nBits, nGap;
    UINT nCount = 0;
    DWORD dwBufferSize;

    nBits = 1;
    x = 128 * nBits / 8;
    y = 64;
    nGap = 128 * nBits; /* 8 Line */
    dwBufferSize = (128 * 64 * nBits) / 8;

    for (pMsg = SrcBmp + dwBufferSize - nGap; pMsg >= SrcBmp; pMsg -= nGap) {
        pX = pMsg;

        for (j = 0; j < x; j++) {
            for (k = 0; k < 8; k++) {
                for (i = 0; i < 8; i++) {
                    if ((*(pX + (x*i)) >> (7 - k)) & 0x0001)
                        ;
                    else
                        SetDataPos(DestSign, nCount + 1);

                    nCount++;
                }
            }

            pX++;
        }
    }
}

//--- Search_Char
//  ∆Ø¡§ πÆ¿⁄¿« ¿ßƒ°∏¶ √£¥¬ «‘ºˆ
//  Parameter
//          input_msg : (I) µ•¿Ã≈Õ
//   chr       : (I) √£∞Ì¿⁄ «œ¥¬ πÆ¿⁄
//  return
//   º∫∞¯ : πÆ¿⁄ ¿ßƒ°
//   Ω«∆– : 0

int Search_Char(char *input_msg, int chr)
{
    char *pdest;

    pdest = strchr(input_msg, chr);

    if (pdest != NULL)
        return (pdest - input_msg + 1);
    else
        return (0);
}
//--- Search_Char End

//--- Convert_to_char
//		HEX «¸Ωƒ¿« πÆ¿⁄ø≠¿ª ASCI πÆ¿⁄ø≠∑Œ ∫Ø»Ø«œ¥¬ «‘ºˆ
//		Parameter
//			hex_data_len : (I) HEX µ•¿Ã≈Õ ±Ê¿Ã
//          hex_data     : (I) HEX µ•¿Ã≈Õ
//			ascii_data   : (O) ASCII∑Œ ∫Ø»Øµ» µ•¿Ã≈Õ

void Convert_to_char(int hex_data_len, char *hex_data, unsigned char *ascii_data)
{
    int i, j;
    int a1, a2, a3;

    j = 0;

    for (i = 0; i < hex_data_len; i = i + 2) {
        a1 = Search_Char("0123456789ABCDEFabcdef", hex_data[i]);

        a1--;

        if (a1 > 15)
            a1 = a1 - 6;

        a2 = Search_Char("0123456789ABCDEFabcdef", hex_data[i + 1]);

        a2--;

        if (a2 > 15)
            a2 = a2 - 6;

        a3 = a1 * 16 + a2;

        ascii_data[j] = a3;

        j++;
    }
}


//--- RLECompress
//		POSøÎ ΩŒ¿Œ¿ÃπÃ¡ˆ RLE æ–√‡ «‘ºˆ
//		Parameter
//          output   : (O) Rle æ–√‡µ» ΩŒ¿Œµ•¿Ã≈∏
//          input    : (I) ΩŒ¿Œµ•¿Ã≈∏
//          length   : (I) ΩŒ¿Œµ•¿Ã≈∏ ≈©±‚
//		return
//			out : RLEµ» ΩŒ¿Œ≈©±‚

int RLECompress(BYTE *output, BYTE *input, int length)
{
    int count = 0, index, i;
    BYTE pixel;
    int out = 0;

    while (count<length)
    {
        index = count;
        pixel = input[index++];

        while (index<length && index - count<127 && input[index] == pixel) index++;

        if (index - count == 1)
        {
            while (index<length && index - count<127 && (input[index] != input[index - 1] || index>1 && input[index] != input[index - 2])) index++;

            while (index<length && input[index] == input[index - 1]) index--;

            output[out++] = (BYTE)(count - index);
            for (i = count; i<index; i++) output[out++] = input[i];
        }
        else
        {
            output[out++] = (BYTE)(index - count);
            output[out++] = pixel;
        }
        count = index;
    }

    return(out);
}
//--- RLECompress End


//--- NULLRLECompress
//		POSøÎ ΩŒ¿Œ¿ÃπÃ¡ˆ RLE æ–√‡ «‘ºˆ
//		Parameter
//          src   : (I) ΩŒ¿Œµ•¿Ã≈∏
//          dst   : (O) NULLRLE æ–√‡µ» ΩŒ¿Œµ•¿Ã≈∏
//		return
//			out : NULLRLEµ» ΩŒ¿Œ≈©±‚

int NULLRLECompress(BYTE *src, BYTE *dst)
{
    unsigned int i, k = 0;
    unsigned int len_sign, len_temp;
    unsigned int zerocnt;
    BYTE flgzero;
    BYTE temp[10] = { 0 };
    BYTE *Sign_Origin = src;
    BYTE temp_sign[4096];
    BYTE temp_data[4096];

    zerocnt = 0;
    flgzero = 0;

    //Sign_Orign -> Raw Data
    memset(temp_sign, 0x00, sizeof(temp_sign));
    memset(temp_data, 0x00, sizeof(temp_data));
    len_sign = 0;

    for (i = 0; i<1024; i++)
    {
        if (Sign_Origin[i] == 0x00)
        {
            flgzero = 1;
            zerocnt++;

            if (i == 1023)
            {
                if (zerocnt <= 255)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)zerocnt;
                }
                else
                if (zerocnt <= 510)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 255);
                }
                else
                if (zerocnt <= 765)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 510);
                }
                else
                if (zerocnt <= 1020)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 765);
                }
                else
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 1020);
                }
            }
        }
        else
        {
            if (flgzero)
            {
                if (zerocnt <= 255)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)zerocnt;
                }
                else
                if (zerocnt <= 510)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 255);
                }
                else
                if (zerocnt <= 765)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 510);
                }
                else
                if (zerocnt <= 1020)
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 765);
                }
                else
                {
                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = 0xFF;

                    temp_sign[len_sign++] = 0x00;
                    temp_sign[len_sign++] = (BYTE)(zerocnt - 1020);
                }

                flgzero = 0;
                zerocnt = 0;
            }

            temp_sign[len_sign++] = Sign_Origin[i];
        }
    }

    len_temp = RLECompress(temp_data, temp_sign, len_sign);
    sprintf((char*)temp, "%04d", len_sign);
    hexcpy(temp_sign, temp, 4, 1);
    memcpy(&temp_sign[2], temp_data, len_temp);

    len_sign = 2 + len_temp;
    memcpy(dst, temp_sign, len_sign);

    return len_sign;
}

//--- encodeBASE64
//		POSøÎ ΩŒ¿Œ¿ÃπÃ¡ˆ BASE64 «‘ºˆ
//		Parameter
//          pInput    : (I) ¿‘∑¬ µ•¿Ã≈∏
//          nLen      : (I) ¿‘∑¬ µ•¿Ã≈∏ ≈©±‚
//          pOutput   : (O) BASE64µ» µ•¿Ã≈∏
//          nOutLen   : (O) BASE64µ» µ•¿Ã≈∏ ≈©±‚
//		return
//			º∫∞¯ : 0

int encodeBASE64(BYTE *pInput, int nLen, char *pOutput, int *nOutLen)
{
    int i, j = 0;
    BYTE buf[4] = { 0 };
    const char vec[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    for (i = 0; i < nLen; i++)
    {
        if (((i / 3) > 0) && ((i % 3) == 0))
        {
            *(pOutput + j + 0) = vec[(buf[0] & 0xFC) >> 2];
            *(pOutput + j + 1) = vec[((buf[0] & 0x03) << 4) | (buf[1] >> 4)];
            *(pOutput + j + 2) = vec[((buf[1] & 0x0F) << 2) | (buf[2] >> 6)];
            *(pOutput + j + 3) = vec[buf[2] & 0x3F];
            j += 4;
        }
        buf[i % 3] = *(pInput + i);
    }

    switch (i % 3)
    {
        case 1:
            buf[1] = 0x0;
            *(pOutput + j + 0) = vec[(buf[0] & 0xFC) >> 2];
            *(pOutput + j + 1) = vec[((buf[0] & 0x03) << 4) | (buf[1] >> 4)];
            *(pOutput + j + 2) = '=';
            *(pOutput + j + 3) = '=';
            *(pOutput + j + 4) = '\0';
            break;
        case 2:
            buf[2] = 0x0;
            *(pOutput + j + 0) = vec[(buf[0] & 0xFC) >> 2];
            *(pOutput + j + 1) = vec[((buf[0] & 0x03) << 4) | (buf[1] >> 4)];
            *(pOutput + j + 2) = vec[((buf[1] & 0x0F) << 2) | (buf[2] >> 6)];
            *(pOutput + j + 3) = '=';
            *(pOutput + j + 4) = '\0';
            break;
        case 0:
            *(pOutput + j + 0) = vec[(buf[0] & 0xFC) >> 2];
            *(pOutput + j + 1) = vec[((buf[0] & 0x03) << 4) | (buf[1] >> 4)];
            *(pOutput + j + 2) = vec[((buf[1] & 0x0F) << 2) | (buf[2] >> 6)];
            *(pOutput + j + 3) = vec[buf[2] & 0x3F];
            *(pOutput + j + 4) = '\0';
            break;
    }
    *nOutLen = strlen((char *)pOutput);

    return(0);
}
//--- encodeBASE64 End


