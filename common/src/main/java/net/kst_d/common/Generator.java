package net.kst_d.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Random;

public class Generator {
    final static Random RANDOM = new SecureRandom(CommonUtils.longToBytes(System.currentTimeMillis()));

    public enum DICT{
        numbers(new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'}),
	az09(new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
		'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'}),
        azAZ09(new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
        	'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
        	'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'
        	,
        	'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D',
        	'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'})
        ;
        final char[] symbols;
	DICT(char[] chars) {
	    symbols = chars;
	}
    }
    
    private static final String DEV_RANDOM = "/dev/urandom";

    public static final int DEFAULT_SID_LEN = 20;

    public static SID sid() {
        return new SID(randomString(DEFAULT_SID_LEN, DICT.az09.symbols));
    }


    public static String randomString(final int len, final char[] dict) {
	final char id[] = new char[len];
	final byte[] tmpArray = getRandomBytes(len);

	for (int j = 0; j < id.length; j++) {
	    id[j] = dict[((int) tmpArray[j] & 0xff) % dict.length];
	}

	return new String(id);
    }

    public static byte[] getRandomBytes(final int len) {
	final byte tmpArray[] = new byte[len];

	//Будем надеяться, что это Unix-like система
	try (InputStream inRnd = new FileInputStream(DEV_RANDOM)) {
	    inRnd.read(tmpArray);
	} catch (IOException e) {
	    //Как известно, в некоторых системах /dev/random отсутствует
	    //Для поддержки этих малоизвестных систем приходится использовать
	    //этот убогий код
	    synchronized (RANDOM) {
		RANDOM.nextBytes(tmpArray);
	    }
	}
	return tmpArray;
    }
}
