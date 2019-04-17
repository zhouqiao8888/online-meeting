package com.stylefeng.guns.rest.modular.cinema.vo;

import com.stylefeng.guns.api.cinema.vo.CinemaFilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaInfoVO;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;

import lombok.Data;

@Data
public class CinemaFieldDetailResVO {
	
	private CinemaInfoVO cinemaInfo;
	
	private CinemaFilmInfoVO filmInfo;
	
	private HallInfoVO hallInfo;

}
