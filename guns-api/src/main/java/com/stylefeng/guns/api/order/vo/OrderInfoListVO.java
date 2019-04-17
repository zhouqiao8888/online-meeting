package com.stylefeng.guns.api.order.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class OrderInfoListVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<OrderInfoVO> orderInfoVOs;
	
	private int totalPage;

}
