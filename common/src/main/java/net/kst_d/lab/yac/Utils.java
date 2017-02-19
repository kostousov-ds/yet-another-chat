package net.kst_d.lab.yac;

import java.io.Closeable;
import java.io.IOException;

import lombok.experimental.UtilityClass;
import net.kst_d.common.log.MethodLogger;

@UtilityClass
public class Utils {
    public static void closer(Closeable c, MethodLogger logger, String name) {
	try {
	    c.close();
	} catch (IOException e) {
	    logger.error("Can't close {}",name, e);
	}
    }
}
