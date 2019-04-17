package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FilmVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer filmNum;
	
	private Integer totalPage;
		
	private List<FilmInfo> filmInfoList;
}
