package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CinemaFieldVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer fieldId;
	
	private String beginTime;
	
	private String endTime;
	
	private String language;
	
	private String hallName;
	
	private String price;

}
