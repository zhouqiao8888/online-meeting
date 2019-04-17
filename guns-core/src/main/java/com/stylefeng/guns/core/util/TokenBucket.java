package com.stylefeng.guns.core.util;

public class TokenBucket {
	
//  桶的容量
	private static int capacity = 100;
	
//  桶的流入速度
	private static int rate = 1;
	
//  当前桶的令牌数
	private static long nowTokens;
	
//  创建时间
	private static long createTime = getNowTime();
	
//	获取当前时间
	private static long getNowTime() {
		return System.currentTimeMillis();
	}
	
	
//	获取当前令牌，若能获取，返回true，否则，返回false
	public static boolean getTokens() {
//		增加令牌
		long nowTime = getNowTime();
		nowTokens = nowTokens + (nowTime - createTime) * rate;
		
//		判断当前令牌是否超过桶的容量
		if(nowTokens > capacity) {
			nowTokens = capacity; 
		}
		
		System.out.println("当前令牌数量：" + nowTokens);
		
//		获取令牌
		if(nowTokens >= 1) {
			nowTokens --;
			return true;
		}
	
		return false;
	}
	
	public static void main(String[] args) throws InterruptedException {
		for(int i = 0;i < 100;i ++) {
//			暂停一下主线程时间，加快获取令牌
			if(i == 10) {
				Thread.sleep(10);
			}
			System.out.println("第" + i + "次获取token:" + TokenBucket.getTokens());
		}
	}
}
