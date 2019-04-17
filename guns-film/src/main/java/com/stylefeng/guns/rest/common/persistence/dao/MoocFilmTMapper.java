package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.ActorInfoVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmT;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2019-02-26
 */
public interface MoocFilmTMapper extends BaseMapper<MoocFilmT> {

	FilmDetailVO getFilmDetailByIdOrName(@Param(value="searchParam") String searchParam, @Param(value="searchType") int searchType);
	
	FilmDescVO getFilmDescByIdOrName(@Param(value="searchParam") String searchParam, @Param(value="searchType") int searchType);

	ActorInfoVO getDirectorInfoByIdOrName(@Param(value="searchParam") String searchParam, @Param(value="searchType") int searchType);
	
	List<ActorInfoVO> getActorsInfoByIdOrName(@Param(value="searchParam") String searchParam, @Param(value="searchType") int searchType);
	
	String getFilmImgsByIdOrName(@Param(value="searchParam") String searchParam, @Param(value="searchType") int searchType);


}
