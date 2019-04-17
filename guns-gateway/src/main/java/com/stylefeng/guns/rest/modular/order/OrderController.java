package com.stylefeng.guns.rest.modular.order;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AlipayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.PayQrcodeInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayStatusInfoVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoListVO;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.common.Const;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
	
//	同一个接口的不同业务实现，设定不同的分组
	@Reference(interfaceClass=OrderServiceAPI.class, check=false, group="order_2019")
	private OrderServiceAPI orderAPI;
	
	@Reference(interfaceClass=OrderServiceAPI.class, check=false, group="order_2018")
	private OrderServiceAPI order2018API;
	
	@Reference(interfaceClass=OrderServiceAPI.class, check=false, group="order_2017")
	private OrderServiceAPI order2017API;
	
	@Reference(interfaceClass=AlipayServiceAPI.class, check=false)
	private AlipayServiceAPI alipayServiceAPI;
	
	
	public ResponseVO<OrderInfoVO> error(int fieldId, String soldSeats, String seatsName) {
		return ResponseVO.serviceFail("抱歉，当前购买人数较多，请稍后重试");
	}
	
	@HystrixCommand(fallbackMethod = "error", 
			commandProperties = {
				@HystrixProperty(name="execution.isolation.strategy", value = "THREAD"),
				@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
				@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
				@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
				}, 
			threadPoolProperties = {
				@HystrixProperty(name = "coreSize", value = "1"),
				@HystrixProperty(name = "maxQueueSize", value = "10"),
				@HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
				@HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
				@HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
				@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
				})
	@RequestMapping(value="/buyTickets", method=RequestMethod.POST)
	public ResponseVO<OrderInfoVO> buyTickets(int fieldId, String soldSeats, String seatsName) {
//		测试返回error
//		int i = 1 / 0;
		try {
			int userId = Integer.parseInt(CurrentUser.getCurrentUserId());
//			int userId = 1;
			if(userId == 0) {
				return ResponseVO.serviceFail("用户未登陆");
			}
			
			OrderInfoVO orderInfoVO = orderAPI.buyTickets(userId, fieldId, soldSeats, seatsName);
			
			if(orderInfoVO == null) {
				log.error("购票失败");
				return ResponseVO.serviceFail("购票失败");
			}
			
			return ResponseVO.success(orderInfoVO);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseVO.sysFail("系统异常");
		}
			
	}
	
	@RequestMapping(value="/buyTickets2018", method=RequestMethod.POST)
	public ResponseVO<OrderInfoVO> buyTickets2018(int fieldId, String soldSeats, String seatsName) {
		
		try {
			int userId = Integer.parseInt(CurrentUser.getCurrentUserId());
			if(userId == 0) {
				return ResponseVO.serviceFail("用户未登陆");
			}
			
			OrderInfoVO orderInfoVO = order2018API.buyTickets(userId, fieldId, soldSeats, seatsName);
			
			if(orderInfoVO == null) {
				log.error("购票失败_2018");
				return ResponseVO.serviceFail("购票失败_2018");
			}
			
			return ResponseVO.success(orderInfoVO);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseVO.sysFail("系统异常");
		}
			
	}
	
	@RequestMapping(value="/buyTickets2017", method=RequestMethod.POST)
	public ResponseVO<OrderInfoVO> buyTickets2017(int fieldId, String soldSeats, String seatsName) {
		
		try {
			int userId = Integer.parseInt(CurrentUser.getCurrentUserId());
			if(userId == 0) {
				return ResponseVO.serviceFail("用户未登陆");
			}
			
			OrderInfoVO orderInfoVO = order2017API.buyTickets(userId, fieldId, soldSeats, seatsName);
			
			if(orderInfoVO == null) {
				log.error("购票失败_2017");
				return ResponseVO.serviceFail("购票失败_2017");
			}
			
			return ResponseVO.success(orderInfoVO);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseVO.sysFail("系统异常");
		}
			
	}
	
	@RequestMapping(value="/getOrderInfo", method=RequestMethod.POST)
	public ResponseVO<List<OrderInfoVO>> getOrderInfo(@RequestParam(value="nowPage", defaultValue="1") int nowPage,
								   					  @RequestParam(value="pageSize", defaultValue="5") int pageSize) {
		try {
			int userId = Integer.parseInt(CurrentUser.getCurrentUserId());
			if(userId == 0) {
				return ResponseVO.serviceFail("用户未登陆");
			}

//			分组聚合	
			List<OrderInfoVO> orderInfoVOs = new ArrayList<OrderInfoVO>();
			int totalPage = 0;
			
			OrderInfoListVO orderInfoListVO = orderAPI.getOrderInfoVOs(userId, nowPage, pageSize);
			if(orderInfoListVO == null) {
				log.error("用户2019订单信息查找失败");
				return ResponseVO.serviceFail("用户2019订单信息查找失败");
			}
			
			OrderInfoListVO orderInfoListVO2018 = order2018API.getOrderInfoVOs(userId, nowPage, pageSize);
			if(orderInfoListVO2018 == null) {
				log.error("用户2018订单信息查找失败");
				return ResponseVO.serviceFail("用户2018订单信息查找失败");
			}
			
			OrderInfoListVO orderInfoListVO2017 = order2017API.getOrderInfoVOs(userId, nowPage, pageSize);
			if(orderInfoListVO2017 == null) {
				log.error("用户2017订单信息查找失败");
				return ResponseVO.serviceFail("用户2017订单信息查找失败");
			}
			
			orderInfoVOs.addAll(orderInfoListVO.getOrderInfoVOs());
			orderInfoVOs.addAll(orderInfoListVO2018.getOrderInfoVOs());
			orderInfoVOs.addAll(orderInfoListVO2017.getOrderInfoVOs());
			
			totalPage = totalPage + orderInfoListVO.getTotalPage() + orderInfoListVO2018.getTotalPage() + orderInfoListVO2017.getTotalPage();
			
			return ResponseVO.success(orderInfoVOs, Const.IMG_PRE, nowPage, totalPage);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseVO.sysFail("系统异常");
		}
		
	}
	
	@RequestMapping(value="/getPayInfo", method=RequestMethod.POST)
	public ResponseVO<PayQrcodeInfoVO> getPayInfo(String orderId) {
		try {
			String userId = CurrentUser.getCurrentUserId();
			if(StringUtils.isEmpty(userId)) {
				return ResponseVO.serviceFail("用户未登陆");
			}
			
			PayQrcodeInfoVO payQrcodeInfoVO = alipayServiceAPI.getPayQrcodeInfo(orderId);
			if(payQrcodeInfoVO == null) {
				log.error("订单支付失败，请稍后重试");
				return ResponseVO.serviceFail("订单支付失败，请稍后重试");
			}
			
			return ResponseVO.success(payQrcodeInfoVO, Const.IMG_PRE);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseVO.sysFail("系统出现异常，请联系管理员");
		}
	}
	
	@RequestMapping(value="/getPayResult", method=RequestMethod.POST)
	public ResponseVO<PayStatusInfoVO> getPayResult(String orderId, 
													@RequestParam(value="tryNums", defaultValue="1") int tryNums) {
		try {
			String userId = CurrentUser.getCurrentUserId();
			if(StringUtils.isEmpty(userId)) {
				return ResponseVO.serviceFail("用户未登陆");
			}
//			隐式参数传递
			RpcContext.getContext().setAttachment("userId", userId);
			
			if(tryNums > 3) {
				log.error("订单支付结果查询失败，请稍后重试");
				return ResponseVO.serviceFail("订单支付结果查询失败，请稍后重试");
			}
			
			PayStatusInfoVO payStatusInfoVO = alipayServiceAPI.getPayStatusInfo(orderId);
			if(payStatusInfoVO == null) {
				log.error("订单支付结果查询失败，请稍后重试");
				return ResponseVO.serviceFail("订单支付结果查询失败，请稍后重试");
			}			
			
			return ResponseVO.success(payStatusInfoVO, Const.IMG_PRE);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseVO.sysFail("系统出现异常，请联系管理员");
		}
	}

}
