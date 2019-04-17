package com.stylefeng.guns.api.cinema;

import java.util.List;

import com.stylefeng.guns.api.cinema.vo.AreaVO;
import com.stylefeng.guns.api.cinema.vo.BrandVO;
import com.stylefeng.guns.api.cinema.vo.CinemaFieldVO;
import com.stylefeng.guns.api.cinema.vo.CinemaFilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemasVO;
import com.stylefeng.guns.api.cinema.vo.FieldVO;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.api.cinema.vo.HallTypeVO;

public interface CinemaServiceAPI {

//	获取各影院详情，分页
	CinemasVO getCinemas(int brandId, int hallType, int districtId, int pageSize, int nowPage); 
	
//	获取品牌标签
	List<BrandVO> getBrands(int brandId);
	
//	获取行政区标签
	List<AreaVO> getAreas(int areaId);
	
//	获取影厅类型标签
	List<HallTypeVO> getHallTypes(int hallType);
	
//	根据影院id获取影院详情
	CinemaInfoVO getCinemaInfo(int cinemaId);
	
//	根据影院id获取该影院的所有影片信息
	List<CinemaFilmInfoVO> getCinemaFilms(int cinemaId);
	
//	根据影院id和场次id来获取当前场次影片信息
	CinemaFilmInfoVO getCurrCinemaFilm(int cinemaId, int fieldId);
	
//	根据影院id和影片id获取所有场次信息
	List<CinemaFieldVO> getCinemaFields(int cinemaId, int filmId);
	
//	根据影院id和场次id来获取当前场次影厅信息
	HallInfoVO getCurrHallInfoVO(int cinemaId, int fieldId);
	
//	根据场次id获取场次信息
	FieldVO getFiledVO(int fieldId); 
	
}
