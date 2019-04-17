package com.stylefeng.guns.rest.modular.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;

@RequestMapping("/user")
@RestController
public class UserController {

//	check=false表示不进行启动检查，则guns-gateway和guns-user哪个先启动都行
	@Reference(interfaceClass=UserAPI.class, check=false)
	private UserAPI userAPI;
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public ResponseVO<String> register(UserModel userModel) {
		if(userModel == null) {
			return ResponseVO.serviceFail("参数不能为空");
		}
		
		if(StringUtils.isEmpty(userModel.getUsername())) {
			return ResponseVO.serviceFail("用户名不能为空");
		}
		
		if(StringUtils.isEmpty(userModel.getPassword())) {
			return ResponseVO.serviceFail("密码不能为空");
		}
		
		boolean resFlag = userAPI.register(userModel);
		if(resFlag) {
			return ResponseVO.success("注册成功");
		}
		return ResponseVO.serviceFail("注册失败");
	}
	
	@RequestMapping(value="/check", method=RequestMethod.POST)
	public ResponseVO<String> check(String username) {
		
		if(StringUtils.isEmpty(username)) {
			return ResponseVO.serviceFail("用户名不能为空");
		}
			
		boolean resFlag = userAPI.checkUsername(username);
		if(resFlag) {
			return ResponseVO.serviceFail("用户名已存在");
		}
		return ResponseVO.success("验证成功");
	}
	
	@RequestMapping(value="/logOut", method=RequestMethod.GET)
	public ResponseVO<String> logOut() {
		return ResponseVO.success("成功退出");
	}
	
	@RequestMapping(value="/getUserInfo", method=RequestMethod.GET)
	public ResponseVO<UserInfoModel> getUserInfo() {
		String uuidStr = CurrentUser.getCurrentUserId();
		if(StringUtils.isEmpty(uuidStr)) {
			return ResponseVO.serviceFail("查询失败，用户尚未登录");
		}
		
		int uuid = Integer.parseInt(uuidStr);
		UserInfoModel userInfoModel = userAPI.getUserInfo(uuid);		
		
		if(userInfoModel == null) {
			return ResponseVO.sysFail("查询失败，未找到该用户");
		}
		return ResponseVO.success(userInfoModel);
	}	
	
	@RequestMapping(value="/udpateUserInfo", method=RequestMethod.POST)
	public ResponseVO<UserInfoModel> udpateUserInfo(UserInfoModel userInfoModel) {
		String uuidStr = CurrentUser.getCurrentUserId();
		if(StringUtils.isEmpty(uuidStr)) {
			return ResponseVO.serviceFail("更新失败，用户尚未登录");
		}
		
//		校验是否是当前登录用户id(防止横向越权)
		int uuid = Integer.parseInt(uuidStr);
		if(uuid != userInfoModel.getUuid()) {
			return ResponseVO.sysFail("不是当前登录用户，非法操作");
		}
		
		UserInfoModel resUserInfoModel = userAPI.updateUserInfo(userInfoModel);	
		
		if(resUserInfoModel == null) {
			return ResponseVO.serviceFail("用户信息修改失败");
		}
		return ResponseVO.success(resUserInfoModel);
	}
}
