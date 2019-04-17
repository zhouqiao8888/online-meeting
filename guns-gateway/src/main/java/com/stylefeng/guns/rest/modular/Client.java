package com.stylefeng.guns.rest.modular;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;

@Component
public class Client {
	
	@Reference(interfaceClass=UserAPI.class)
	private UserAPI userAPI;
	
	public void run() {
		userAPI.login("username", "password");
	}
}
