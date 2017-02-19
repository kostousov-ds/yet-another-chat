package net.kst_d.common.log;

import net.kst_d.common.SID;

public class MethodLogger {
    private final KstLogger logger;
    private final SID sid;
    private final String method;
    private String common = null;
    private String prefix;

    public MethodLogger(KstLogger logger, SID sid, String method) {
	this.logger = logger;
	this.sid = sid;
	this.method = method;
	prefix = prefix();
    }

    public MethodLogger(KstLogger logger, SID sid, String method, String common) {
	this.logger = logger;
	this.sid = sid;
	this.method = method;
	this.common = common;
	prefix = prefix();
	
    }

    public String getCommon() {
	return common;
    }

    public void setCommon(String common) {
	this.common = common;
	prefix = prefix();
    }

    private String prefix() {
	return sid + " " + method + " : " + (common == null ? "" : common + " : ");
    }

    public String getName() {
	return logger.getName();
    }

    public boolean isTraceEnabled() {
	return logger.isTraceEnabled();
    }

    public void trace(String msg) {
	logger.trace(prefix + msg);
    }

    public void trace(String format, Object arg) {
	logger.trace(prefix + format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
	logger.trace(prefix + format, arg1, arg2);
    }

    public void trace(String format, Object... arguments) {
	logger.trace(prefix + format, arguments);
    }

    public void trace(String msg, Throwable t) {
	logger.trace(prefix + msg, t);
    }

    public boolean isDebugEnabled() {
	return logger.isDebugEnabled();
    }

    public void debug(String msg) {
	logger.debug(prefix + msg);
    }

    public void debug(String format, Object arg) {
	logger.debug(prefix + format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
	logger.debug(prefix + format, arg1, arg2);
    }

    public void debug(String format, Object... arguments) {
	logger.debug(prefix + format, arguments);
    }

    public void debug(String msg, Throwable t) {
	logger.debug(prefix + msg, t);
    }

    public boolean isInfoEnabled() {
	return logger.isInfoEnabled();
    }

    public void info(String msg) {
	logger.info(prefix + msg);
    }

    public void info(String format, Object arg) {
	logger.info(prefix + format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
	logger.info(prefix + format, arg1, arg2);
    }

    public void info(String format, Object... arguments) {
	logger.info(prefix + format, arguments);
    }

    public void info(String msg, Throwable t) {
	logger.info(prefix + msg, t);
    }

    public boolean isWarnEnabled() {
	return logger.isWarnEnabled();
    }

    public void warn(String msg) {
	logger.warn(prefix + msg);
    }

    public void warn(String format, Object arg) {
	logger.warn(prefix + format, arg);
    }

    public void warn(String format, Object... arguments) {
	logger.warn(prefix + format, arguments);
    }

    public void warn(String format, Object arg1, Object arg2) {
	logger.warn(prefix + format, arg1, arg2);
    }

    public void warn(String msg, Throwable t) {
	logger.warn(prefix + msg, t);
    }

    public boolean isErrorEnabled() {
	return logger.isErrorEnabled();
    }

    public void error(String msg) {
	logger.error(prefix + msg);
    }

    public void error(String format, Object arg) {
	logger.error(prefix + format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
	logger.error(prefix + format, arg1, arg2);
    }

    public void error(String format, Object... arguments) {
	logger.error(prefix + format, arguments);
    }

    public void error(String msg, Throwable t) {
	logger.error(prefix + msg, t);
    }
}
