package com.stylefeng.guns.api.cinema.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CinemasVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<CinemaVO> cinemas;
	
	private Integer totalPage;

}
