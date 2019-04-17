package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import com.stylefeng.guns.api.film.vo.ImageVO;

import lombok.Data;

@Data
public class FilmDescVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String biography;
	
	private String filmId;
	
	private ActorsVO actors;
	
	private ImageVO imgs;

}
