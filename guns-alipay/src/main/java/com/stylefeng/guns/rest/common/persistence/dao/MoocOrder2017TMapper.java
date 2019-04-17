package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2019-03-18
 */
public interface MoocOrder2017TMapper extends BaseMapper<MoocOrder2017T> {

	String getSeatAddress(int fieldId);
	
	String getAllSoldSeats(int fieldId);
	
	OrderInfoVO getOrderInfoVOByOrderId(String uuid);
	
	List<OrderInfoVO> getOrderInfoVOs(int userId);
}
