import java.io.BufferedReader;
import java.io.FileReader;

public class SHA1 {

    public final static int DIGEST_SIZE = 20;

    private int[]  m_state;
    private long   m_lCount;
    private byte[] m_digestBits;
    private int[]  m_block;
    private int    m_nBlockIndex;

    public SHA1() {
      m_state = new int[5];
      m_block = new int[16];
      m_digestBits = new byte[DIGEST_SIZE];
      reset(); 
    };

    public void clear() {
      int i;
      for (i = 0; i < m_state.length; i++) 
        m_state[i] = 0;
      m_lCount = 0;
      for (i = 0; i < m_digestBits.length; i++) 
        m_digestBits[i] = 0;
      for (i = 0; i < m_block.length; i++) 
        m_block[i] = 0;
      m_nBlockIndex = 0;
    };  
    
    final int rol(int nValue,
                 int nBits) {

      return ((nValue << nBits) | (nValue >>> (32 - nBits)));
    };

    final int blk0(int i) {
      return (m_block[i] = (rol(m_block[i],24) & 0xff00ff00) | 
                            (rol(m_block[i], 8) & 0x00ff00ff));
    };

    final int blk(int nI) {
      return (m_block[nI & 15] = rol(m_block[(nI +  13) & 15] ^ m_block[(nI + 8) & 15] ^
                                     m_block[(nI + 2) & 15] ^ m_block[nI & 15], 1));
    };
    
    void main_loop(int a, int b, int c, int d, int e){
        int temp,f=0,k=0;
        for(int i = 0; i< 80; i++){
            if(i<=19){
                f =  (b & c) | ((~b) & d);
                k = 0x5A827999;
            }else if(i<=39)
            {
                f = b ^ c ^d;
                k = 0x6ED9EBA1;
            }else if(i<=59)
            {
                f = (b & c) | (b & d) | (c & d);
                k = 0x8F1BBCDC;
            }else if(i<=79)
            {
                f = b ^ c ^ d;
                k = 0xCA62C1D6;
            }
            if(i < 16){          
               temp = rol(a,5) + f + e + k + blk0(i);
            }else{
                temp = rol(a,5) + f + e + k + blk(i);
            }
            e = d;
            d = c;
            c = rol(b,30);
            b = a;
            a = temp;
        }
        m_state[0] += a;
        m_state[1] += b;
        m_state[2] += c;
        m_state[3] += d;
        m_state[4] += e;
    }

    public void reset() {

      m_state[0] = 0x67452301;
      m_state[1] = 0xefcdab89;
      m_state[2] = 0x98badcfe;
      m_state[3] = 0x10325476;
      m_state[4] = 0xc3d2e1f0;
      m_lCount = 0;
      m_digestBits = new byte[20];
      m_nBlockIndex = 0;
    };


    /**
      * adds a single byte to the digest
      */
    public void update(byte bB) {

        int nMask = (m_nBlockIndex & 3) << 3;

        m_lCount += 8;
        m_block[m_nBlockIndex >> 2] &= ~(0xff << nMask);
        m_block[m_nBlockIndex >> 2] |= (bB & 0xff) << nMask;
        m_nBlockIndex++;
        if (m_nBlockIndex == 64) {
          main_loop(m_state[0],m_state[1],m_state[2],m_state[3],m_state[4]);
          m_nBlockIndex = 0;
        };
    };

    public void update(byte[] data) {
     
      for (int nI = 0; nI < data.length; nI++)
        update(data[nI]);
    };
 
    public void update(String sData) {
     
      for (int nI = 0; nI < sData.length(); nI++)
        update((byte)(sData.charAt(nI) & 0x0ff));
    };
 
    public void finalize() {

        int i;
        byte bits[] = new byte[8];

        for (i = 0; i < 8; i++) {
          bits[i] = (byte)((m_lCount >>> (((7 - i) << 3))) & 0xff);
        };

        update((byte) 128);
        while (m_nBlockIndex != 56)
          update((byte) 0);

        for (i = 0; i < bits.length; i++)
          update(bits[i]);

        for (i = 0; i < 20; i++) {
          m_digestBits[i] = (byte)((m_state[i >> 2] >> ((3 - (i & 3)) << 3)) & 0xff);
        };
    };

    public String getResult(){
        String result = "";
        for(int i = 0; i < DIGEST_SIZE; i++){
            if(i!=0 && (i%4 == 0))
                result += " ";
            if( Integer.toHexString(m_digestBits[i]&0xFF).length() != 2)
                result += "0"+Integer.toHexString(m_digestBits[i]&0xFF);
            else
                result += Integer.toHexString(m_digestBits[i]&0xFF);
        }
        return result;
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
    	SHA1 sha1 = new SHA1();
    	sha1.update(sb1.toString());
    	sha1.finalize();
    	String ret1 = sha1.getResult();
    	
    	sha1 = new SHA1();
    	sha1.update(sb2.toString());
    	sha1.finalize();
    	String ret2 = sha1.getResult();
    	long endTime = System.nanoTime();
        long milisec = (endTime - startTime)/2000;
    	
        exeT[0] = milisec;
    	return new String[]{ret1, ret2};
    }
    
    

}