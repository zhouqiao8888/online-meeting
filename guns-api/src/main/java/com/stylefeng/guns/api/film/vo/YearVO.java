package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class YearVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String yearId;
	
	private String yearName;
	
	private boolean isActive;


}
