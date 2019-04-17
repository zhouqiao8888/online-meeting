package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CinemaFilmInfoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String filmId;
	
	private String filmName;
	
	private String filmLength;
	
	private String filmType;
	
	private String filmCats;
	
	private String actors;
	
	private String imgAddress;
	
	private List<CinemaFieldVO> filmFields;
}
