package com.stylefeng.guns;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.stylefeng.guns.rest.OrderApplication;
import com.stylefeng.guns.rest.modular.order.util.FTPUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=OrderApplication.class)
public class GunsRestApplicationTests {
	
	@Autowired
	private FTPUtil ftpUtil;

	@Test
	public void contextLoads() {
		String str = ftpUtil.readFileByFileName("123214.json");
		System.out.println(str);
	}

}
