package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class HallTypeVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer halltypeId;
	
	private String halltypeName;
	
	private Boolean isActive;

}
