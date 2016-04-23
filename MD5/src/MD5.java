import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class MD5
{
    private static final int   PARA_1     = 0x67452301;
    private static final int   PARA_2     = (int) 0xEFCDAB89L;
    private static final int   PARA_3     = (int) 0x98BADCFEL;
    private static final int   PARA_4     = 0x10325476;
    private static final int[] SHIFT_ARR = 
    	{ 7, 12, 17, 22, 5, 
    	  9, 14, 20, 4,11, 
    	  16, 23, 6, 10, 15, 21    
    	};
    
    private static final int[] GLOBAL_TABLE    = new int[64];
    static
    {
        for (int i = 0; i < 64; i++)
            GLOBAL_TABLE[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }
 
    public static byte[] computeMD5(byte[] message)
    {
        int msgLength = message.length;
        int numOfBlocks = ((msgLength + 8) >>> 6) + 1;
        int entireLen = numOfBlocks << 6;
        byte[] paddingBytes = new byte[entireLen - msgLength];
        paddingBytes[0] = (byte) 0x80;
        long messageLenBits = (long) msgLength << 3;
        
        
        for (int i = 0; i < 8; i++)
        {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLenBits;
            messageLenBits >>>= 8;
        }
        int[] buffer = new int[16];
        int a = PARA_1, b = PARA_2, c = PARA_3, d = PARA_4;
     
        for (int i = 0; i < numOfBlocks; i++)
        {
            int index = i << 6;
            for (int j = 0; j < 64; j++, index++)
                buffer[j >>> 2] = ((int) ((index < msgLength) ? 
                		message[index]  : paddingBytes[index - msgLength]) << 24) | (buffer[j >>> 2] >>> 8);
            int lastA = a, lastB = b, lastC = c, lastD = d;
            for (int j = 0; j < 64; j++)
            {
                int div16 = j >>> 4;
                int f = 0;
                int bIndex = j;
                switch (div16)
                {
                    case 0:
                        f = (b & c) | (~b & d);
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);
                        bIndex = (bIndex * 5 + 1) & 0x0F;
                        break;
                    case 2:
                        f = b ^ c ^ d;
                        bIndex = (bIndex * 3 + 5) & 0x0F;
                        break;
                    case 3:
                        f = c ^ (b | ~d);
                        bIndex = (bIndex * 7) & 0x0F;
                        break;
                }
                int temp = b
                        + Integer.rotateLeft(a + f + buffer[bIndex]
                                + GLOBAL_TABLE[j],
                                SHIFT_ARR[(div16 << 2) | (j & 3)]);
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            a += lastA;
            b += lastB;
            c += lastC;
            d += lastD;
        }
        byte[] md5 = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++)
        {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++)
            {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md5;
    }
 
    public static String toHexString(byte[] b)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
        {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }
    
    public static String[] compare(String filePath1, String filePath2, long[] exeT) throws Exception{
    	StringBuilder sb1 = new StringBuilder();
    	StringBuilder sb2 = new StringBuilder();
    	
    	BufferedReader br1 = new BufferedReader(new FileReader(filePath1));
    	BufferedReader br2 = new BufferedReader(new FileReader(filePath2));
    	
    	String newLine = null;
    	while((newLine = br1.readLine()) != null){
    		sb1.append(newLine);
    	}
    	
    	while((newLine = br2.readLine()) != null){
    		sb2.append(newLine);
    	}
    	
    	br1.close();
    	br2.close();
    	
        long startTime = System.nanoTime();
        
    	String hashCode1 = "0x" + toHexString(computeMD5(sb1.toString().getBytes()));
    	String hashCode2 = "0x" + toHexString(computeMD5(sb2.toString().getBytes()));;
    	
        long endTime = System.nanoTime();
        long milisec = (endTime - startTime)/2000;
        exeT[0] = milisec;
        
    	return new String[]{hashCode1, hashCode2};
    }
 

}