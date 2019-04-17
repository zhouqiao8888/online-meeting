package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class AreaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer areaId;
	
	private String areaName;
	
	private Boolean isActive;

}
