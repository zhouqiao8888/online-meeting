package com.stylefeng.guns.rest.modular.film.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.film.FilmAsyncServiceAPI;
import com.stylefeng.guns.api.film.vo.ActorInfoVO;
import com.stylefeng.guns.api.film.vo.ActorsVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.api.film.vo.ImageVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmTMapper;

@Component
@Service(interfaceClass=FilmAsyncServiceAPI.class)
public class DefaultFilmAsyncServiceImpl implements FilmAsyncServiceAPI {

	@Autowired
	private MoocFilmTMapper moocFilmTMapper;
	

	@Override
	public FilmDetailVO getFilmDetail(String searchParam, int searchType) {
		FilmDetailVO filmDetailVO = moocFilmTMapper.getFilmDetailByIdOrName(searchParam, searchType);
		
		return filmDetailVO;
	}

	@Override
	public FilmDescVO getFilmDesc(String searchParam, int searchType) {
		FilmDescVO filmDescVO = moocFilmTMapper.getFilmDescByIdOrName(searchParam, searchType);

//		组装导演、演员信息
		ActorsVO actorsVO = new ActorsVO();
		ActorInfoVO director = this.getDirectorInfoByIdOrName(searchParam, searchType);
		List<ActorInfoVO> actors = this.getActorsInfoByIdOrName(searchParam, searchType);
		actorsVO.setDirector(director);
		actorsVO.setActors(actors);
		
//		组装影片图像信息
		ImageVO imageVO = this.getFilmImgsByIdOrName(searchParam, searchType);
		
		filmDescVO.setImgs(imageVO);
		filmDescVO.setActors(actorsVO);
		return filmDescVO;
	}

	@Override
	public ActorInfoVO getDirectorInfoByIdOrName(String searchParam, int searchType) {
		return moocFilmTMapper.getDirectorInfoByIdOrName(searchParam, searchType);
	}

	@Override
	public List<ActorInfoVO> getActorsInfoByIdOrName(String searchParam, int searchType) {
		return moocFilmTMapper.getActorsInfoByIdOrName(searchParam, searchType);
	}
	
	@Override
	public ImageVO getFilmImgsByIdOrName(String searchParam, int searchType) {
		ImageVO imageVO = new ImageVO();
		String[] images = moocFilmTMapper.getFilmImgsByIdOrName(searchParam, searchType).split(",");
		imageVO.setMainImg(images[0]);
		imageVO.setImg01(images[1]);
		imageVO.setImg02(images[2]);
		imageVO.setImg03(images[3]);
		imageVO.setImg04(images[4]);
		
		return imageVO;
	}
	


}
