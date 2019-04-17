package com.stylefeng.guns.api.film;

import java.util.List;

import com.stylefeng.guns.api.film.vo.ActorInfoVO;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.CateVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.api.film.vo.ImageVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;

public interface FilmServiceAPI {
	
//	获取标题栏
	List<BannerVO> getBanners();
	
//	获取热映影片
	FilmVO getHotFilms(boolean isLimit, int num);
	
//	获取即将上映影片
	FilmVO getSoonFilms(boolean isLimit, int num);
	
////	获取热映影片-----重构
//	FilmVO getHotFilms(int sortId, int cateId, int sourceId, 
//			           int yearId, int nowPage, int pageSize);
//	
////	获取即将上映影片----重构
//	FilmVO getSoonFilms(int sortId, int cateId, int sourceId, 
//			           int yearId, int nowPage, int pageSize);
	
//	获取热映/即将上映/经典影片
	FilmVO getFilms(int showType, int sortId, int cateId, int sourceId, 
	           int yearId, int nowPage, int pageSize);
	
//	获取院线排名
	List<FilmInfo> getBoxRanking();
	
//	获取期待排名
	List<FilmInfo> getExpectRanking();
	
//	获取top100
	List<FilmInfo> getTop();
	
//	获取所有类型标签
	List<CateVO> getCateInfo(String cateId);
	
//	获取所有区域标签
	List<SourceVO> getSourceInfo(String sourceId);
	
//	获取所有年代标签
	List<YearVO> getYearInfo(String yearId);
	
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
