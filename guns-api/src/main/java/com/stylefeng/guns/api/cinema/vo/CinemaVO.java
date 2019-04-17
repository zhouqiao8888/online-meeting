package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;
@Data
public class CinemaVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer uuid;
	
	private String cinemaName;
	
	private String address;
	
	private String minimumPrice;

}
