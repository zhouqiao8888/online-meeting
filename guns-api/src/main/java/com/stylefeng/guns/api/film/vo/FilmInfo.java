package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class FilmInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String filmId;
	
	private Integer filmType;
	
	private String imgAddress;
	
	private String filmName;
	
	private String filmScore;
	
	private Integer expectNum;
	
	private String showTime;
	
	private Integer boxNum;
	
	private String score;
	
}
