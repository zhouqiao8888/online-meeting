package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    
    @Reference(interfaceClass=UserAPI.class, check=false)
   	private UserAPI userAPI;

    @RequestMapping(value = "${jwt.auth-path}")
    public ResponseVO<?> createAuthenticationToken(AuthRequest authRequest) {
    	
    	boolean validate = true;
//    	重写验证逻辑
    	String username = authRequest.getUserName();
    	String password = authRequest.getPassword();
    	int userId = userAPI.login(username, password);
    	if(userId == 0) {
    		validate = false;
    	}

        if (validate) {
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken("" + userId, randomKey);
            return ResponseVO.success(new AuthResponse(token, randomKey));
//            return ResponseEntity.ok(new AuthResponse(token, randomKey));
        } else {
        	return ResponseVO.serviceFail("用户名或者密码错误");
//            throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
        }
    }
}
