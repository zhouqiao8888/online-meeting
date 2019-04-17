package com.stylefeng.guns.api.order;


import com.stylefeng.guns.api.order.vo.OrderInfoListVO;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;


public interface OrderServiceAPI {
	
//	判断是否为有效的座位号
	boolean isValidSeats(int fieldId, String soldSeats);
	
//	判断该座位号是否已经被占
	boolean isSoldSeats(int fieldId, String soldSeats);
	
//	插入订单信息，并返回相应的订单id
	String saveOrderInfo(int userId, int fieldId, String soldSeats, String seatsName);
	
//	根据订单id查询订单
	OrderInfoVO getOrderInfoVOByOrderId(String orderId);
	
//	根据用户id查询订单
	OrderInfoListVO getOrderInfoVOs(int userId, int nowPage, int pageSize);
	
//	购票
	OrderInfoVO buyTickets(int userId, int fieldId, String soldSeats, String seatsName);
	
//	之所以分成两个方法写，是因为外界只需知道设置成功或者失败，不用暴露状态码
//	将订单支付状态置为成功：设置状态码为1
	boolean paySuccess(String orderId);
	
//	将订单支付状态置为失败：设置状态码为2
	boolean payFail(String orderId);
}
