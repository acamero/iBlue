package com.iblue.utils;

public class Log {
	public enum LogLevel {
		SILENT(0), ERROR(10), WARNING(20), INFO(30), DEBUG(40);
		private int level;

		private LogLevel(int level ) {
			this.level = level;
		}

		public boolean isLogging(LogLevel loggingLevel) {
			if (level >= loggingLevel.level) {
				return true;
			}
			return false;
		}
		
		public static LogLevel getLogLevel(String level) {			
			for(LogLevel l : LogLevel.values()) {
				if(l.toString().compareToIgnoreCase(level)==0){
					return l;
				}
			}
			// return default log level
			return Log.logLevel;
		}
	}

	private static LogLevel logLevel = LogLevel.WARNING;
	
	public static void setLogLevel(LogLevel level) {
		logLevel = level;
	}
	
	public static LogLevel getLogLevel(){
		return logLevel;
	}
	
	public static void error(String message) {
		if(logLevel.isLogging(LogLevel.ERROR)) {
			toLog(message);
		}
	}
	
	public static void warning(String message) {
		if(logLevel.isLogging(LogLevel.WARNING)) {
			toLog(message);
		}
	}
	
	public static void info(String message) {
		if(logLevel.isLogging(LogLevel.INFO)) {
			toLog(message);
		}
	}
	
	public static void debug(String message) {
		if(logLevel.isLogging(LogLevel.DEBUG)) {
			toLog(message);
		}
	}
	
	private static void toLog(String message) {
		System.out.println(message);
	}
}
