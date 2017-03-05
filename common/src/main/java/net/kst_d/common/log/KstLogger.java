package net.kst_d.common.log;

import org.slf4j.Logger;

import net.kst_d.common.SID;

public class KstLogger {
    private Logger logger;

    KstLogger(Logger logger) {
	this.logger = logger;
    }

    public MethodLogger silentEnter(SID sid, String method) {
        return new MethodLogger(this, sid, method);
    }

    public String getName() {
	return logger.getName();
    }

    public boolean isTraceEnabled() {
	return logger.isTraceEnabled();
    }

    public void trace(String msg) {
	logger.trace(msg);
    }

    public void trace(String format, Object arg) {
	logger.trace(format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
	logger.trace(format, arg1, arg2);
    }

    public void trace(String format, Object... arguments) {
	logger.trace(format, arguments);
    }

    public void trace(String msg, Throwable t) {
	logger.trace(msg, t);
    }

    public boolean isDebugEnabled() {
	return logger.isDebugEnabled();
    }

    public void debug(String msg) {
	logger.debug(msg);
    }

    public void debug(String format, Object arg) {
	logger.debug(format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
	logger.debug(format, arg1, arg2);
    }

    public void debug(String format, Object... arguments) {
	logger.debug(format, arguments);
    }

    public void debug(String msg, Throwable t) {
	logger.debug(msg, t);
    }

    public boolean isInfoEnabled() {
	return logger.isInfoEnabled();
    }

    public void info(String msg) {
	logger.info(msg);
    }

    public void info(String format, Object arg) {
	logger.info(format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
	logger.info(format, arg1, arg2);
    }

    public void info(String format, Object... arguments) {
	logger.info(format, arguments);
    }

    public void info(String msg, Throwable t) {
	logger.info(msg, t);
    }

    public boolean isWarnEnabled() {
	return logger.isWarnEnabled();
    }

    public void warn(String msg) {
	logger.warn(msg);
    }

    public void warn(String format, Object arg) {
	logger.warn(format, arg);
    }

    public void warn(String format, Object... arguments) {
	logger.warn(format, arguments);
    }

    public void warn(String format, Object arg1, Object arg2) {
	logger.warn(format, arg1, arg2);
    }

    public void warn(String msg, Throwable t) {
	logger.warn(msg, t);
    }

    public boolean isErrorEnabled() {
	return logger.isErrorEnabled();
    }

    public void error(String msg) {
	logger.error(msg);
    }

    public void error(String format, Object arg) {
	logger.error(format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
	logger.error(format, arg1, arg2);
    }

    public void error(String format, Object... arguments) {
	logger.error(format, arguments);
    }

    public void error(String msg, Throwable t) {
	logger.error(msg, t);
    }

/*
    public void trace(String msg){

    }

    public void trace(String format, Object arg){

    }

    public void trace(String format, Object arg1, Object arg2){

    }

    public void trace(String format, Object... arguments){

    }

    public void trace(String msg, Throwable t){

    }
*/

}
