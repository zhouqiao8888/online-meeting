package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class FieldVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int cinemaId;
	
	private int fieldId;
	
	private int filmId;
	
	private int filmPrice;

}
