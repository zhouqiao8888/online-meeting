package com.stylefeng.guns.rest.modular.film.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.ActorInfoVO;
import com.stylefeng.guns.api.film.vo.ActorsVO;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.CateVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.api.film.vo.ImageVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocBannerTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocCatDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocSourceDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocYearDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocBannerT;
import com.stylefeng.guns.rest.common.persistence.model.MoocCatDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmT;
import com.stylefeng.guns.rest.common.persistence.model.MoocSourceDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocYearDictT;

@Component
@Service(interfaceClass=FilmServiceAPI.class)
public class DefaultFilmServiceImpl implements FilmServiceAPI {

	@Autowired
	private MoocBannerTMapper moocBannerTMapper;
	
	@Autowired
	private MoocFilmTMapper moocFilmTMapper;
	
	@Autowired
	private MoocCatDictTMapper moocCatDictTMapper;
	
	@Autowired
	private MoocSourceDictTMapper moocSourceDictTMapper;
	
	@Autowired
	private MoocYearDictTMapper MoocYearDictTMapper;
	
	@Override
	public List<BannerVO> getBanners() {
		List<MoocBannerT> moocBannerTs = moocBannerTMapper.selectList(null);
		List<BannerVO> bannerVOs = new ArrayList<BannerVO>();
		for(MoocBannerT moocBannerT : moocBannerTs) {
			BannerVO bannerVO = this.transferToBannerVO(moocBannerT);
			bannerVOs.add(bannerVO);
		}
		return bannerVOs;
	}

	@Override
	public FilmVO getHotFilms(boolean isLimit, int num) {
		FilmVO filmVO = new FilmVO();
		EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<MoocFilmT>();
//		1表示正在热映
		wrapper.eq("film_status", 1);
//		查询第一页，num条
		Page<MoocFilmT> page = new Page<MoocFilmT>(1, num);
		
//		判断是否需要限制条数，若需要，则为首页，并且限制为热映影片	
		List<FilmInfo> filmInfos = new ArrayList<FilmInfo>();
		if(isLimit) {
			List<MoocFilmT> moocFilmTs = moocFilmTMapper.selectPage(page, wrapper);
			for(MoocFilmT moocFilmT : moocFilmTs) {
				filmInfos.add(this.transferToFilmInfo(moocFilmT));
			}
			filmVO.setFilmInfoList(filmInfos);
			filmVO.setFilmNum(moocFilmTs.size());
		}		
		else {
//			若不需要，则为列表页，同样限制为热映影片

		}
		return filmVO;
	}

	@Override
	public FilmVO getSoonFilms(boolean isLimit, int num) {
		FilmVO filmVO = new FilmVO();
		EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<MoocFilmT>();
//		2表示即将上映
		wrapper.eq("film_status", 2);
//		查询第一页，num条
		Page<MoocFilmT> page = new Page<MoocFilmT>(1, num);
		
//		判断是否需要限制条数，若需要，则为首页，并且限制为即将上映影片	
		List<FilmInfo> filmInfos = new ArrayList<FilmInfo>();
		if(isLimit) {
			List<MoocFilmT> moocFilmTs = moocFilmTMapper.selectPage(page, wrapper);
			for(MoocFilmT moocFilmT : moocFilmTs) {
				filmInfos.add(this.transferToFilmInfo(moocFilmT));
			}
			filmVO.setFilmInfoList(filmInfos);
			filmVO.setFilmNum(moocFilmTs.size());
		}		
		else {
//			若不需要，则为列表页，同样限制为即将上映影片

		}
		return filmVO;
	}

	@Override
	public List<FilmInfo> getBoxRanking() {
//		条件：正在热映，票房前10
		EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<MoocFilmT>();
//		1表示正在热映
		wrapper.eq("film_status", 1);
//		查询第一页，num条，按票房排序
		Page<MoocFilmT> page = new Page<MoocFilmT>(1, 10, "film_box_office");
		
		List<FilmInfo> resFilmInfos = new ArrayList<FilmInfo>();
		List<MoocFilmT> moocFilmTs = moocFilmTMapper.selectPage(page, wrapper);
		for(MoocFilmT moocFilmT : moocFilmTs) {
			resFilmInfos.add(this.transferToFilmInfo(moocFilmT));
		}		
		return resFilmInfos;
	}

	@Override
	public List<FilmInfo> getExpectRanking() {
//		条件：即将上映，预售前10
		EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<MoocFilmT>();
//		2表示即将上映
		wrapper.eq("film_status", 2);
//		查询第一页，num条，按预售排序
		Page<MoocFilmT> page = new Page<MoocFilmT>(1, 10, "film_preSaleNum");
		
		List<FilmInfo> resFilmInfos = new ArrayList<FilmInfo>();
		List<MoocFilmT> moocFilmTs = moocFilmTMapper.selectPage(page, wrapper);
		for(MoocFilmT moocFilmT : moocFilmTs) {
			resFilmInfos.add(this.transferToFilmInfo(moocFilmT));
		}		
		return resFilmInfos;
	}

	@Override
	public List<FilmInfo> getTop() {
//		条件：经典影片，评分前10
		EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<MoocFilmT>();
//		3表示经典影片
		wrapper.eq("film_status", 3);
//		查询第一页，num条，按预售排序
		Page<MoocFilmT> page = new Page<MoocFilmT>(1, 10, "film_score");
		
		List<FilmInfo> resFilmInfos = new ArrayList<FilmInfo>();
		List<MoocFilmT> moocFilmTs = moocFilmTMapper.selectPage(page, wrapper);
		for(MoocFilmT moocFilmT : moocFilmTs) {
			resFilmInfos.add(this.transferToFilmInfo(moocFilmT));
		}		
		return resFilmInfos;
	}
	
	@Override
	public List<CateVO> getCateInfo(String cateId) {
//		获取所有类型标签
		List<MoocCatDictT> moocCatDictTs = moocCatDictTMapper.selectList(null);

//		转为前端VO
		List<CateVO> cateVOs = new ArrayList<CateVO>();
		boolean flag = false;
		for(MoocCatDictT moocCatDictT : moocCatDictTs) {
			CateVO cateVO = this.transferToCateVO(moocCatDictT);
//			若查找到相应标签id, 则active
			if(StringUtils.equals(cateId, moocCatDictT.getUuid() + "")) {
				cateVO.setActive(true);
				flag = true;
			}
			else {
				cateVO.setActive(false);
			}
			cateVOs.add(cateVO);
		}
		
//		若相应标签id一个都没查找到，则active全都置为true
		if(!flag) {
			for(CateVO cateVO : cateVOs) {
				cateVO.setActive(true);
			}
		}
		
		return cateVOs;
	}

	@Override
	public List<SourceVO> getSourceInfo(String sourceId) {
//		获取所有区域标签
		List<MoocSourceDictT> moocSourceDictTs = moocSourceDictTMapper.selectList(null);

//		转为前端VO
		List<SourceVO> sourceVOs = new ArrayList<SourceVO>();
		boolean flag = false;
		for(MoocSourceDictT moocSourceDictT : moocSourceDictTs) {
			SourceVO sourceVO = this.transferToSourceVO(moocSourceDictT);
//			若查找到相应标签id, 则active
			if(StringUtils.equals(sourceId, moocSourceDictT.getUuid() + "")) {
				sourceVO.setActive(true);
				flag = true;
			}
			else {
				sourceVO.setActive(false);
			}
			sourceVOs.add(sourceVO);
		}

//		若相应标签id一个都没查找到，则active全都置为true
		if(!flag) {
			for(SourceVO sourceVO : sourceVOs) {
				sourceVO.setActive(true);
			}
		}
		
		return sourceVOs;
	}

	@Override
	public List<YearVO> getYearInfo(String yearId) {
//		获取所有年代标签
		List<MoocYearDictT> moocYearDictTs = MoocYearDictTMapper.selectList(null);

//		转为前端VO
		List<YearVO> yearVOs = new ArrayList<YearVO>();
		boolean flag = false;
		for(MoocYearDictT moocYearDictT : moocYearDictTs) {
			YearVO yearVO = this.transferToYearVO(moocYearDictT);
//			若查找到相应标签id, 则active
			if(StringUtils.equals(yearId, moocYearDictT.getUuid() + "")) {
				yearVO.setActive(true);
				flag = true;
			}
			else {
				yearVO.setActive(false);
			}
			yearVOs.add(yearVO);
		}
		
//		若相应标签id一个都没查找到，则active全都置为true
		if(!flag) {
			for(YearVO yearVO : yearVOs) {
				yearVO.setActive(true);
			}
		}
		
		return yearVOs;
	}

//	MoocBannerT -> BannerVO
	private BannerVO transferToBannerVO(MoocBannerT moocBannerT) {
		BannerVO bannerVO = new BannerVO();
		bannerVO.setBannerAddress(moocBannerT.getBannerAddress());
		bannerVO.setBannerId(moocBannerT.getUuid() + "");
		bannerVO.setBannerUrl(moocBannerT.getBannerUrl());
		
		return bannerVO;
	}
	
//	MoocFilmT -> FilmInfo
	private FilmInfo transferToFilmInfo(MoocFilmT moocFilmT) {
		FilmInfo filmInfo = new FilmInfo();
		filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
		filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
		filmInfo.setFilmId(moocFilmT.getUuid() + "");
		filmInfo.setFilmName(moocFilmT.getFilmName());
		filmInfo.setFilmScore(moocFilmT.getFilmScore());
		filmInfo.setFilmType(moocFilmT.getFilmType());
		filmInfo.setImgAddress(moocFilmT.getImgAddress());
		filmInfo.setScore(moocFilmT.getFilmScore());
		filmInfo.setShowTime(DateUtil.getTime(moocFilmT.getFilmTime()));
		
		return filmInfo;
	}

//	MoocCatDictT -> CateVO
	private CateVO transferToCateVO(MoocCatDictT moocCatDictT) {
		CateVO cateVO = new CateVO();
		cateVO.setCateId(moocCatDictT.getUuid() + "");
		cateVO.setCateName(moocCatDictT.getShowName());
		return cateVO;
	}
	
//	MoocCatDictT -> CateVO
	private SourceVO transferToSourceVO(MoocSourceDictT moocSourceDictT) {
		SourceVO sourceVO = new SourceVO();
		sourceVO.setSourceId(moocSourceDictT.getUuid() + "");
		sourceVO.setSourceName(moocSourceDictT.getShowName());
		return sourceVO;
	}
	
//	MoocCatDictT -> CateVO
	private YearVO transferToYearVO(MoocYearDictT moocCatDictT) {
		YearVO yearVO = new YearVO();
		yearVO.setYearId(moocCatDictT.getUuid() + "");
		yearVO.setYearName(moocCatDictT.getShowName());
		return yearVO;
	}

	@Override
	public FilmVO getFilms(int showType, int sortId, int cateId, int sourceId, int yearId, int nowPage, int pageSize) {
		EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<MoocFilmT>();
		
//		查询条件：判断标签id是否等于99（全部），一般不会按全部查询，所以99忽略
//		限制为热映 /即将上映 /经典影片
		wrapper.eq("film_status", showType);

//		分类条件：注意分类是字符串 -> #2#23#13#
		if(cateId != 99) {
			String reStr = "#" + cateId + "#";
			wrapper.like("film_cats", reStr);
		}
//		区域条件
		if(sourceId != 99) {
			wrapper.eq("film_source", sourceId);
		}
//		年代条件
		if(yearId != 99) {
			wrapper.eq("film_date", yearId);
		}		

//		排序条件：判读sortId, 1-按热门搜索（票房排序）[默认排序方式]，2-按时间搜索（时间排序），3-按评价搜索（评分排序）
		String sortMethod = "";
		
		if(sortId == 2) {
			sortMethod = "film_time";
		}
		else if(sortId == 3) {
//			即将上映影片没有评分，用预售数量代替
			if(showType == 2) {
				sortMethod = "film_preSaleNum";
			}
			else {
				sortMethod = "film_score";
			}
		}
		else {
//			即将上映影片没有票房，用预售数量代替
			if(showType == 2) {
				sortMethod = "film_preSaleNum";
			}
			else {
				sortMethod = "film_box_office";	
			}
		}
		
//		显示页，显示条数
		Page<MoocFilmT> page = new Page<MoocFilmT>(nowPage, pageSize, sortMethod);
		
//		计算总页数
		int totalCount = moocFilmTMapper.selectCount(wrapper);
		int totalPage = 0;
		if(totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		}
		else {
			totalPage = totalCount / pageSize + 1;
		}

//		条件查询，并重组前端VO对象
		List<MoocFilmT> moocFilmTs = moocFilmTMapper.selectPage(page, wrapper);
		
		List<FilmInfo> filmInfos = new ArrayList<FilmInfo>();
		FilmVO filmVO = new FilmVO();
		for(MoocFilmT moocFilmT : moocFilmTs) {
			filmInfos.add(this.transferToFilmInfo(moocFilmT));
		}
		filmVO.setFilmNum(filmInfos.size());
		filmVO.setFilmInfoList(filmInfos);
		filmVO.setTotalPage(totalPage);

		return filmVO;
	}

	@Override
	public FilmDetailVO getFilmDetail(String searchParam, int searchType) {
		FilmDetailVO filmDetailVO = moocFilmTMapper.getFilmDetailByIdOrName(searchParam, searchType);
		if(filmDetailVO == null) {
			return filmDetailVO;
		}
		
//		获取第四部分的信息描述
		FilmDescVO filmDescVO = this.getFilmDesc(searchParam, searchType);
		filmDetailVO.setInfo04(filmDescVO);
		
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

	@Override
	public List<ActorInfoVO> getActorsInfoByIdOrName(String searchParam, int searchType) {
		return moocFilmTMapper.getActorsInfoByIdOrName(searchParam, searchType);
	}

	


}
