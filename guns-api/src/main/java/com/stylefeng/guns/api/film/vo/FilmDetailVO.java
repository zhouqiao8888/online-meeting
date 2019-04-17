package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class FilmDetailVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String filmName;
	
	private String filmEnName;
	
	private String imgAddress;
	
	private String score;
	
	private String scoreNum;
	
	private String totalBox;
	
	private String info01;
	
	private String info02;
	
	private String info03;
	
	private FilmDescVO info04;
	

}
