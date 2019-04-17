package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class SourceVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sourceId;
	
	private String sourceName;
	
	private boolean isActive;
	

}
