package com.stylefeng.guns.rest.modular.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FieldVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoListVO;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2017TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.modular.order.util.FTPUtil;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Component
@Service(interfaceClass=OrderServiceAPI.class, group="order_2017")
public class OrderServiceImpl2017 implements OrderServiceAPI {
	
	@Autowired
	private MoocOrder2017TMapper moocOrder2017TMapper;
	
	@Reference(interfaceClass=CinemaServiceAPI.class, check=false)
	private CinemaServiceAPI cinemaServiceAPI;
	
	@Autowired
	private FTPUtil ftpUtil;

	@Override
	public boolean isValidSeats(int fieldId, String soldSeats) {
//		获取座位表文件名称
		String seatAddressName = moocOrder2017TMapper.getSeatAddress(fieldId);
		int index = seatAddressName.indexOf("/");
		seatAddressName = seatAddressName.substring(index + 1);
		
//		并读取出座位表文件内容
		String seatAddressJSON = ftpUtil.readFileByFileName(seatAddressName);
		
		JSONObject jsonObject = JSONObject.fromObject(seatAddressJSON);
		String ids = jsonObject.optString("ids");	

//		判断选定座位是否在有效座位表中：有一个不在就返回false
		String[] soldSeatsArr = soldSeats.split(",");
		String[] idsArr = ids.split(",");
		
		for(String soldSeatTemp : soldSeatsArr) {
			boolean flag = false;
			for(String id : idsArr) {
				if(soldSeatTemp.equals(id)) {
					flag = true;
					break;
				}
			}
//			若一次内部循环没找到，说明该座位无效，直接返回false
			if(!flag) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSoldSeats(int fieldId, String soldSeats) {
		String allSoldSeats = moocOrder2017TMapper.getAllSoldSeats(fieldId);
		
//		判断选定座位是否在已占座位中：有一个已经被占就返回false
		String[] soldSeatsArr = soldSeats.split(",");
		String[] allSoldSeatsArr = allSoldSeats.split(",");
		
		for(String soldSeatTemp : soldSeatsArr) {
			boolean flag = false;
			for(String allSoldSeatsTemp : allSoldSeatsArr) {
				if(soldSeatTemp.equals(allSoldSeatsTemp)) {
					flag = true;
					break;
				}
			}
//			若一次内部循环找到了一次，说明该座位已经被占，直接返回false
			if(flag) {
				return false;
			}
		}
		return true;
	}

	@Override
	public OrderInfoVO getOrderInfoVOByOrderId(String uuid) {
		if(StringUtils.isEmpty(uuid)) {
			log.error("请输入订单id");
			return null;
		}
		return moocOrder2017TMapper.getOrderInfoVOByOrderId(uuid);
	}

	@Override
	public OrderInfoListVO getOrderInfoVOs(int userId, int nowPage, int pageSize) {
					
		if(userId == 0) {
			log.error("请输入用户id");
			return null;
		}
		
		List<OrderInfoVO> orderInfoVOs = moocOrder2017TMapper.getOrderInfoVOs(userId);
		if(orderInfoVOs == null || orderInfoVOs.size() == 0) {
			return null;
		}
		
//		分页
		OrderInfoListVO orderInfoListVO = new OrderInfoListVO();
		List<OrderInfoVO> orderInfoVOs2 = new ArrayList<OrderInfoVO>();
		
		for(int i = (nowPage - 1) * pageSize;i < nowPage * pageSize && i < orderInfoVOs.size();i ++) {
			OrderInfoVO orderInfoVO = orderInfoVOs.get(i);
			
//			更新状态信息
			if(orderInfoVO.getOrderStatusCode() == 0) {
				orderInfoVO.setOrderStatus("待支付");
			}
			else if(orderInfoVO.getOrderStatusCode() == 1) {
				orderInfoVO.setOrderStatus("已支付");
			}
			else {
				orderInfoVO.setOrderStatus("已关闭");
			}
			
			orderInfoVOs2.add(orderInfoVO);
		}
		
		orderInfoListVO.setOrderInfoVOs(orderInfoVOs2);
		
//		计算页数
		int totalPage = 0;
		if(orderInfoVOs.size() % pageSize != 0) {
			totalPage = orderInfoVOs.size() / pageSize + 1;
		}
		else {
			totalPage = orderInfoVOs.size() / pageSize;
		}
		
		orderInfoListVO.setTotalPage(totalPage);
		
		return orderInfoListVO;
	}
	
	@Override
	public String saveOrderInfo(int userId, int fieldId, String soldSeats, String seatsName) {
//		构建订单信息
		MoocOrder2017T moocOrderT = new MoocOrder2017T();
		
		moocOrderT.setUuid(UUID.randomUUID().toString());
		moocOrderT.setOrderUser(userId);
		moocOrderT.setSeatsIds(soldSeats);
		moocOrderT.setSeatsName(seatsName);
		
//		填充影院相关信息
		FieldVO fieldVO = cinemaServiceAPI.getFiledVO(fieldId);
		moocOrderT.setFieldId(fieldVO.getFieldId());
		moocOrderT.setFilmId(fieldVO.getFilmId());
		moocOrderT.setCinemaId(fieldVO.getCinemaId());
		moocOrderT.setFilmPrice(Double.valueOf(fieldVO.getFilmPrice()));
		
//		计算总价
		double totalPrice = this.multiply(moocOrderT.getFilmPrice(), Double.valueOf(soldSeats.split(",").length));
		moocOrderT.setOrderPrice(totalPrice);
		
//		插入订单信息，若成功，返回订单id
		int res = moocOrder2017TMapper.insert(moocOrderT);
		if(res > 0) {
			return moocOrderT.getUuid();
		}
		return null;
	}

	@Override
	public OrderInfoVO buyTickets(int userId, int fieldId, String soldSeats, String seatsName) {
		if(TokenBucket.getTokens()) {
	//		判断是否为有效座位
			boolean flag = this.isValidSeats(fieldId, soldSeats);
			if(!flag) {
				log.error("所选座位不是有效座位");
				return null;
			}
			
	//		判断是否为已售座位
			flag = this.isSoldSeats(fieldId, soldSeats);
			if(!flag) {
				log.error("所选座位已售");
				return null;
			}
			
			
	//		插入订单信息，若成功，返回订单信息
			String orderId = this.saveOrderInfo(userId, fieldId, soldSeats, seatsName);
			if(StringUtils.isEmpty(orderId)) {
				log.error("订单信息插入失败");
				return null;
			}
			
			return this.getOrderInfoVOByOrderId(orderId);
		}
		else {
			log.error("订单信息插入失败");
			return null;
		}
	}
	
	private double multiply(double d1, double d2) {
		
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		
		return b1.multiply(b2).doubleValue();
	}

	@Override
	public boolean paySuccess(String orderId) {
		MoocOrder2017T moocOrderT = new MoocOrder2017T();
		moocOrderT.setUuid(orderId);
		moocOrderT.setOrderStatus(1);
		
		int res = moocOrder2017TMapper.updateById(moocOrderT);
		if(res > 0) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean payFail(String orderId) {
		MoocOrder2017T moocOrderT = new MoocOrder2017T();
		moocOrderT.setUuid(orderId);
		moocOrderT.setOrderStatus(2);
		
		int res = moocOrder2017TMapper.updateById(moocOrderT);
		if(res > 0) {
			return true;
		}
		
		return false;
	}
	

	
	

	

}
