package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.PayQrcodeInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayStatusInfoVO;

public class AlipayServiceMock implements AlipayServiceAPI {

	@Override
	public PayQrcodeInfoVO getPayQrcodeInfo(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PayStatusInfoVO getPayStatusInfo(String orderId) {
		PayStatusInfoVO payStatusInfoVO = new PayStatusInfoVO();
		
		payStatusInfoVO.setOrderId(orderId);
		payStatusInfoVO.setOrderMsg("尚未支付成功");
		payStatusInfoVO.setOrderStatus(0);
		
		return payStatusInfoVO;
	}

}
