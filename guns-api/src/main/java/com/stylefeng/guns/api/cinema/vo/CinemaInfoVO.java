package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CinemaInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer cinemaId;
	
	private String imgUrl;
	
	private String cinemaName;
	
	private String cinemaAdress;
	
	private String cinemaPhone;

}
