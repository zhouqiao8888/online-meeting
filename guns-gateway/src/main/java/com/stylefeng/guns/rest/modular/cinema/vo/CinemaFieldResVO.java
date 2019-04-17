package com.stylefeng.guns.rest.modular.cinema.vo;

import java.util.List;

import com.stylefeng.guns.api.cinema.vo.CinemaFilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaInfoVO;

import lombok.Data;

@Data
public class CinemaFieldResVO {
	
	private CinemaInfoVO cinemaInfo;
	
	private List<CinemaFilmInfoVO> filmList;

}
