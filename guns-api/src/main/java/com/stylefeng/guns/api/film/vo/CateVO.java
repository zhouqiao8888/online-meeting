package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CateVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String cateId;
	
	private String cateName;
	
	private boolean isActive;
	
}
