package com.stylefeng.guns.api.film.vo;

import java.util.List;

import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;

import lombok.Data;

@Data
public class FilmIndexVO {
	private List<BannerVO> banners;
	
	private FilmVO hotFilms;
	
	private FilmVO soonFilms;
	
	private List<FilmInfo> boxRanking;
	
	private List<FilmInfo> expectRanking;

	private List<FilmInfo> top100;

}
