package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.PayQrcodeInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayStatusInfoVO;

public interface AlipayServiceAPI {
	
//	获取支付二维码
	PayQrcodeInfoVO getPayQrcodeInfo(String orderId);
	
//	获取支付结果
	PayStatusInfoVO getPayStatusInfo(String orderId);

}
