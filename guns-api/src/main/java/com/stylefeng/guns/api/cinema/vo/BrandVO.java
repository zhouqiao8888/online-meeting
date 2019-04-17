package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class BrandVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer brandId;
	
	private String brandName;
	
	private Boolean isActive; 

}
