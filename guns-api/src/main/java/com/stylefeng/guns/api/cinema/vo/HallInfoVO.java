package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class HallInfoVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String hallFieldId;
	
	private String hallName;
	
	private Integer price;
	
	private String seatFile;
	
//	已售座位信息必须关联订单才能查询
	private String soldSeats;

}
