package com.stylefeng.guns.rest.common;

public class CurrentUser {
	/**
	 *	InheritableThreadLocal与ThreadLocal区别：
	 * 	即使启动了线程隔离，InheritableThreadLocal仍能获取值，而ThreadLocal不能
	 */
	private static final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<String>();
	
//	ThreadLocal按线程区分，为了减小内存占用，存储userId，而不是直接存储userInfo
	public static void saveCurrentUserId(String userId) {
		threadLocal.set(userId);
	}
	
	public static String getCurrentUserId() {
		return threadLocal.get();
	}

}
