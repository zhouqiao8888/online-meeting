package com.stylefeng.guns.api.alipay.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class PayStatusInfoVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String orderId;
	
	private Integer orderStatus;
	
	private String orderMsg;

}
