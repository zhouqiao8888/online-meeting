package com.stylefeng.guns.api.film;

import java.util.List;

import com.stylefeng.guns.api.film.vo.ActorInfoVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.api.film.vo.ImageVO;

public interface FilmAsyncServiceAPI {
	
//	根据查找类型、查找参数来获取影片详情
	FilmDetailVO getFilmDetail(String searchParam, int searchType);

//	根据查找类型、查找参数来获取影片描述
	FilmDescVO getFilmDesc(String searchParam, int searchType);

//	根据查找类型、查找参数来获取导演信息
	ActorInfoVO getDirectorInfoByIdOrName(String searchParam, int searchType);
	
//	根据查找类型、查找参数来获取演员信息
	List<ActorInfoVO> getActorsInfoByIdOrName(String searchParam, int searchType);

//	根据查找类型、查找参数来获取影片信息
	ImageVO getFilmImgsByIdOrName(String searchParam, int searchType);
}
