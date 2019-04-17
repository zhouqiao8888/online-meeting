package com.stylefeng.guns.api.order.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderInfoVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String orderId;
	
	private String filmName;
	
	private String fieldTime;
	
	private String cinemaName;
	
	private String seatsName;
	
	private String orderPrice;
	
	private String orderTimestamp;
	
	private int orderStatusCode;
	
	private String orderStatus;
	

}
