package com.stylefeng.guns.rest.modular.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;

@Component
@Service(interfaceClass=UserAPI.class, loadbalance="roundrobin")
//设置负载均衡策略为轮询机制
public class UserServiceImpl implements UserAPI {
	
	@Autowired
	private MoocUserTMapper moocUserTMapper;

	@Override
	public int login(String username, String password) {
//		判断用户是否存在
		MoocUserT moocUserT = new MoocUserT();
		moocUserT.setUserName(username);
		MoocUserT resMoocUserT = moocUserTMapper.selectOne(moocUserT);
		
//		若存在，校验密码
		if(resMoocUserT != null) {
			String validatePwd = MD5Util.encrypt(password);
			System.out.println(validatePwd);
			if(validatePwd.equals(resMoocUserT.getUserPwd())) {
				return resMoocUserT.getUuid();
			}
		}
		return 0;
	}

	@Override
	public boolean register(UserModel userModel) {
//		判断用户名是否存在
		boolean resFlag = this.checkUsername(userModel.getUsername());
		if(resFlag) {
			return false;
		}
		
//		将注册信息实体转化为数据实体
		MoocUserT moocUserT = new MoocUserT();
		moocUserT.setUserName(userModel.getUsername());
		moocUserT.setEmail(userModel.getEmail());
		moocUserT.setAddress(userModel.getAddress());
		moocUserT.setUserPhone(userModel.getPhone());
		moocUserT.setUpdateTime(new Date());
		
//		md5混淆加密+salt值 -> Shiro加密
		String password = MD5Util.encrypt(userModel.getPassword());
		moocUserT.setUserPwd(password);
		
//		持久化数据实体
		int resCount = moocUserTMapper.insert(moocUserT);
		if(resCount > 0) {
			return true;
		}	
		return false;
	}


	@Override
	public boolean checkUsername(String username) {
//		判断用户名是否存在
		EntityWrapper<MoocUserT> wrapper = new EntityWrapper<MoocUserT>();
		wrapper.eq("user_name", username);
//		根据 Wrapper条件，查询总记录数
		int resCount = moocUserTMapper.selectCount(wrapper);
		if(resCount > 0) {
			return true;
		}
		return false;
	}

	@Override
	public UserInfoModel getUserInfo(int uuid) {
		MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
		if(moocUserT != null) {
			return this.transferToUserInfoModel(moocUserT);
		}
		return null;
	}

	@Override
	public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
//		将展示信息实体转化为数据实体
		MoocUserT moocUserT = this.transferToMoocUserT(userInfoModel);
//		更新相应的数据实体
		int resCount = moocUserTMapper.updateById(moocUserT);
		UserInfoModel resUserInfoModel = null;
		if(resCount > 0) {
			resUserInfoModel = this.getUserInfo(moocUserT.getUuid());
		}
		return resUserInfoModel;
	}
	
//	MoocUserT -> UserInfoModel
	private UserInfoModel transferToUserInfoModel(MoocUserT moocUserT) {
		UserInfoModel userInfoModel = new UserInfoModel();
		userInfoModel.setUuid(moocUserT.getUuid());
		userInfoModel.setUsername(moocUserT.getUserName());
		userInfoModel.setAddress(moocUserT.getAddress());
		userInfoModel.setBiography(moocUserT.getBiography());
		userInfoModel.setBirthday(moocUserT.getBirthday());
		userInfoModel.setEmail(moocUserT.getEmail());
		userInfoModel.setHeadAddress(moocUserT.getHeadUrl());
		userInfoModel.setLifeState(moocUserT.getLifeState());
		userInfoModel.setNickname(moocUserT.getNickName());
		userInfoModel.setPhone(moocUserT.getUserPhone());
		userInfoModel.setSex(moocUserT.getUserSex());
		userInfoModel.setCreateTime(moocUserT.getBeginTime().getTime());
		userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
		
		return userInfoModel;
	}
	
//	UserInfoModel -> MoocUserT 
	private MoocUserT transferToMoocUserT(UserInfoModel userInfoModel) {
		MoocUserT moocUserT = new MoocUserT();
		moocUserT.setUuid(userInfoModel.getUuid());
		moocUserT.setAddress(userInfoModel.getAddress());
		moocUserT.setBiography(userInfoModel.getBiography());
		moocUserT.setBirthday(userInfoModel.getBirthday());
		moocUserT.setEmail(userInfoModel.getEmail());
		moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
		moocUserT.setLifeState(userInfoModel.getLifeState());
		moocUserT.setNickName(userInfoModel.getNickname());
		moocUserT.setUserName(userInfoModel.getUsername());
		moocUserT.setUserPhone(userInfoModel.getPhone());
		moocUserT.setUserSex(userInfoModel.getSex());
		moocUserT.setUpdateTime(new Date(System.currentTimeMillis()));
		
		return moocUserT;
	}

}
