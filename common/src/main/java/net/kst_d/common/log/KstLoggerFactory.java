package net.kst_d.common.log;

import org.slf4j.LoggerFactory;

public class KstLoggerFactory {
    public static KstLogger logger(String name){
	return new KstLogger(LoggerFactory.getLogger(name));
    }

    public static KstLogger logger(Class<?> clazz){
        return new KstLogger(LoggerFactory.getLogger(clazz));
    }
}
