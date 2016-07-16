import java.io.BufferedReader;
import java.io.FileReader;


//<<<<<<< HEAD
// 000
// 111
//=======
// 333
// 444


>>>>>>> 0d8bfd39c32588840a0ae42d515700ae950f1507
public class SHA1 {
    //SHA1 produces a 20-byte message digest
    public final static int DIGEST_SIZE = 20;
    private int[] hState;
    private long mlCount;
    private byte[] mDigestBits;
    private int[] mBlock;
    private int mnBlockIndex;

    public SHA1() {
        hState = new int[5];
        mBlock = new int[16];
        mDigestBits = new byte[DIGEST_SIZE];
        reset();
    }

    public void clear() {
        for (int i = 0; i < hState.length; i++) {
            hState[i] = 0;
        }
        mlCount = 0;
        for (int i = 0; i < mDigestBits.length; i++) {
            mDigestBits[i] = 0;
        }
        for (int i = 0; i < mBlock.length; i++) {
            mBlock[i] = 0;
        }
        mnBlockIndex = 0;
    }

    final int rol(int nValue, int nBits) {
        return ((nValue << nBits) | (nValue >>> (32 - nBits)));
    }

    final int blk0(int i) {
        return (mBlock[i] = (rol(mBlock[i], 24) & 0xff00ff00) |
                (rol(mBlock[i], 8) & 0x00ff00ff));
    }

    final int blk(int i) {
        return (mBlock[i & 15] = rol(mBlock[(i + 13) & 15] ^ mBlock[(i + 8) & 15] ^
                mBlock[(i + 2) & 15] ^ mBlock[i & 15], 1));
    }

    void main_loop(int a, int b, int c, int d, int e) {
        int temp, f = 0, k = 0;
        for (int i = 0; i < 80; i++) {
            if (i <= 19) {
                f = (b & c) | ((~b) & d);
                k = 0x5A827999;
            } else if (i <= 39) {
                f = b ^ c ^ d;
                k = 0x6ED9EBA1;
            } else if (i <= 59) {
                f = (b & c) | (b & d) | (c & d);
                k = 0x8F1BBCDC;
            } else if (i <= 79) {
                f = b ^ c ^ d;
                k = 0xCA62C1D6;
            }
            if (i < 16) {
                temp = rol(a, 5) + f + e + k + blk0(i);
            } else {
                temp = rol(a, 5) + f + e + k + blk(i);
            }
            e = d;
            d = c;
            c = rol(b, 30);
            b = a;
            a = temp;
        }
        hState[0] += a;
        hState[1] += b;
        hState[2] += c;
        hState[3] += d;
        hState[4] += e;
    }

    public void reset() {

        hState[0] = 0x67452301;
        hState[1] = 0xefcdab89;
        hState[2] = 0x98badcfe;
        hState[3] = 0x10325476;
        hState[4] = 0xc3d2e1f0;
        mlCount = 0;
        mDigestBits = new byte[20];
        mnBlockIndex = 0;
    }


    /**
     * adds a single byte to the digest
     */
    public void update(byte bB) {
        int nMask = (mnBlockIndex & 3) << 3;
        mlCount += 8;
        mBlock[mnBlockIndex >> 2] &= ~(0xff << nMask);
        mBlock[mnBlockIndex >> 2] |= (bB & 0xff) << nMask;
        mnBlockIndex++;
        if (mnBlockIndex == 64) {
            main_loop(hState[0], hState[1], hState[2], hState[3], hState[4]);
            mnBlockIndex = 0;
        }
    }

    public void update(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            update(data[i]);
        }
    }


    public void update(String sData) {
        for (int i = 0; i < sData.length(); i++)
            update((byte) (sData.charAt(i) & 0x0ff));
    }

    public void finalize() {
        byte bits[] = new byte[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (byte) ((mlCount >>> (((7 - i) << 3))) & 0xff);
        }
        update((byte) 128);
        while (mnBlockIndex != 56) {
            update((byte) 0);
        }
        for (int i = 0; i < bits.length; i++) {
            update(bits[i]);
        }
        for (int i = 0; i < 20; i++) {
            mDigestBits[i] = (byte) ((hState[i >> 2] >> ((3 - (i & 3)) << 3)) & 0xff);
        }

    }

    public String getResult() {
        String result = "";
        for (int i = 0; i < DIGEST_SIZE; i++) {
            if (i != 0 && (i % 4 == 0)) {
                result += " ";
            }
            if (Integer.toHexString(mDigestBits[i] & 0xFF).length() != 2) {
                result += "0" + Integer.toHexString(mDigestBits[i] & 0xFF);
            } else {
                result += Integer.toHexString(mDigestBits[i] & 0xFF);
            }
        }
        return result;
    }

    public static String[] compare(String filePath1, String filePath2, long[] exeT) throws Exception {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        BufferedReader br1 = new BufferedReader(new FileReader(filePath1));
        BufferedReader br2 = new BufferedReader(new FileReader(filePath2));
        String newLine = null;
        while ((newLine = br1.readLine()) != null) {
            sb1.append(newLine);
        }
        while ((newLine = br2.readLine()) != null) {
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
        long milisec = (endTime - startTime) / 2000;
        exeT[0] = milisec;
        return new String[]{ret1, ret2};
    }

}
