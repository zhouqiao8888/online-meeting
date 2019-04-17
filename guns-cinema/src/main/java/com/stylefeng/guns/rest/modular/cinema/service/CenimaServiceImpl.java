package com.stylefeng.guns.rest.modular.cinema.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.AreaVO;
import com.stylefeng.guns.api.cinema.vo.BrandVO;
import com.stylefeng.guns.api.cinema.vo.CinemaFieldVO;
import com.stylefeng.guns.api.cinema.vo.CinemaFilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaVO;
import com.stylefeng.guns.api.cinema.vo.CinemasVO;
import com.stylefeng.guns.api.cinema.vo.FieldVO;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.api.cinema.vo.HallTypeVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocAreaDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocBrandDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocHallFilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MoocFieldT;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallFilmInfoT;


@Component
@Service(interfaceClass=CinemaServiceAPI.class, executes=10)
public class CenimaServiceImpl implements CinemaServiceAPI {
	
	@Autowired
	private MoocCinemaTMapper moocCinemaTMapper;
	
	@Autowired
	private MoocBrandDictTMapper moocBrandDictTMapper;
	
	@Autowired
	private MoocAreaDictTMapper moocAreaDictTMapper;
	
	@Autowired
	private MoocHallDictTMapper moocHallDictTMapper;
	
	@Autowired
	private MoocFieldTMapper moocFieldTMapper;
	
	@Autowired
	private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;
	


	@Override
	public CinemasVO getCinemas(int brandId, int hallType, int districtId, int pageSize, int nowPage) {
		CinemasVO cinemasVO = new CinemasVO();
		EntityWrapper<MoocCinemaT> wrapper = new EntityWrapper<MoocCinemaT>();
		
//		品牌条件
		if(brandId != 99) {
			wrapper.eq("brand_id", brandId);
		}
		
//		地域条件
		if(districtId != 99) {
			wrapper.eq("area_id", districtId);
		}
		
//		影厅类型条件
		if(hallType != 99) {
			String hallTypeStr = "#" + hallType + "#";
			wrapper.like("hall_ids", hallTypeStr);
		}
		
//		按页数条件查询，并转换为前端VO
		Page<MoocCinemaT> page = new Page<MoocCinemaT>(nowPage, pageSize);
		List<MoocCinemaT> moocCinemaTs = moocCinemaTMapper.selectPage(page, wrapper);
		
		List<CinemaVO> cinemaVOs = this.transferToCinemaVOs(moocCinemaTs);
		cinemasVO.setCinemas(cinemaVOs);
		
//		计算总页数
	    int totalCount = moocCinemaTMapper.selectCount(wrapper);
	    
//		if(totalCount == 0) {
//			return null;
//		}
//			
//		int totalPage = 0;
//		if(totalCount % pageSize == 0) {
//			totalPage = totalCount / pageSize;
//		}
//		else {
//			totalPage = totalCount / pageSize + 1;
//		}
//		cinemasVO.setTotalPage(totalPage);
		
//		使用page计算总页数
		Page<CinemaVO> pageForCount = new Page<CinemaVO>();
		pageForCount.setRecords(cinemaVOs);
		pageForCount.setSize(pageSize);
		pageForCount.setTotal(totalCount);
		
		cinemasVO.setTotalPage((int)pageForCount.getPages());
		
		return cinemasVO;
	}
	
	private List<CinemaVO> transferToCinemaVOs(List<MoocCinemaT> moocCinemaTs) {
		List<CinemaVO> cinemaVOs = new ArrayList<CinemaVO>();
		
		for(MoocCinemaT moocCinemaT : moocCinemaTs) {
			CinemaVO cinemaVO = new CinemaVO();
			
			cinemaVO.setUuid(moocCinemaT.getUuid());
			cinemaVO.setAddress(moocCinemaT.getCinemaAddress());
			cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
			cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice() + "");
			
			cinemaVOs.add(cinemaVO);
		}
		
		return cinemaVOs;
	}
	
	private List<BrandVO> transferToBrandVOs(List<MoocBrandDictT> moocBrandDictTs) {
		List<BrandVO> brandVOs = new ArrayList<BrandVO>();
		for(MoocBrandDictT moocBrandDictT : moocBrandDictTs) {
			BrandVO brandVO = new BrandVO();
			
			brandVO.setBrandId(moocBrandDictT.getUuid());
			brandVO.setBrandName(moocBrandDictT.getShowName());
			
			brandVOs.add(brandVO);
		}
		
		return brandVOs;
	}
	
	private List<AreaVO> transferToAreaVOs(List<MoocAreaDictT> moocAreaDictTs) {
		List<AreaVO> areaVOs = new ArrayList<AreaVO>();
		for(MoocAreaDictT moocAreaDictT : moocAreaDictTs) {
			AreaVO areaVO = new AreaVO();
			areaVO.setAreaId(moocAreaDictT.getUuid());
			areaVO.setAreaName(moocAreaDictT.getShowName());
			areaVOs.add(areaVO);
		}
		
		return areaVOs;
	}
	
	private List<HallTypeVO> transferToHallTypeVOs(List<MoocHallDictT> moocHallDictTs) {
		List<HallTypeVO> hallTypeVOs = new ArrayList<HallTypeVO>();
		for(MoocHallDictT moocHallDictT : moocHallDictTs) {
			HallTypeVO hallTypeVO = new HallTypeVO();
			hallTypeVO.setHalltypeId(moocHallDictT.getUuid());
			hallTypeVO.setHalltypeName(moocHallDictT.getShowName());
			hallTypeVOs.add(hallTypeVO);
		}
		
		return hallTypeVOs;
	}


	@Override
	public List<BrandVO> getBrands(int brandId) {
		
		MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
		
//		先选出所有的标签
		List<MoocBrandDictT> moocBrandDictTs = moocBrandDictTMapper.selectList(null);
		
//		转化为VO
		List<BrandVO> brandVOs = this.transferToBrandVOs(moocBrandDictTs);
		
//		根据id激活相应的标签
		for(BrandVO brandVO : brandVOs) {
//			判断这个标签是否存在于数据库，若不存在，默认置为全部（99）
			if(moocBrandDictT == null && brandVO.getBrandId() == 99) {
				brandVO.setIsActive(true);
			}
			else {
				if(brandVO.getBrandId() == brandId) {
					brandVO.setIsActive(true);
				}
				else {
					brandVO.setIsActive(false);
				}
			}
		}
		
		return brandVOs;
	}

	@Override
	public List<AreaVO> getAreas(int areaId) {
		
		MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
		
//		先选出所有的标签
		List<MoocAreaDictT> moocAreaDictTs = moocAreaDictTMapper.selectList(null);
		
//		转化为VO
		List<AreaVO> areaVOs = this.transferToAreaVOs(moocAreaDictTs);
		
//		根据id激活相应的标签
		for(AreaVO areaVO : areaVOs) {
//			判断这个标签是否存在于数据库，若不存在，默认置为全部（99）
			if(moocAreaDictT == null && areaVO.getAreaId() == 99) {
				areaVO.setIsActive(true);
			}
			else {
				if(areaVO.getAreaId() == areaId) {
					areaVO.setIsActive(true);
				}
				else {
					areaVO.setIsActive(false);
				}
			}
		}
		
		return areaVOs;
	}

	@Override
	public List<HallTypeVO> getHallTypes(int hallType) {
		
		MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
		
//		先选出所有的标签
		List<MoocHallDictT> moocHallDictTs = moocHallDictTMapper.selectList(null); 
		
//		转化为VO
		List<HallTypeVO> hallTypeVOs = this.transferToHallTypeVOs(moocHallDictTs);
		
//		根据id激活相应的标签
		for(HallTypeVO hallTypeVO : hallTypeVOs) {
//			判断这个标签是否存在于数据库，若不存在，默认置为全部（99）
			if(moocHallDictT == null && hallTypeVO.getHalltypeId() == 99) {
				hallTypeVO.setIsActive(true);
			}
			else {
				if(hallTypeVO.getHalltypeId() == hallType) {
					hallTypeVO.setIsActive(true);
				}
				else {
					hallTypeVO.setIsActive(false);
				}
			}
		}
		
		return hallTypeVOs;
	}

	@Override
	public CinemaInfoVO getCinemaInfo(int uuid) {
		MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(uuid);
		
		if(moocCinemaT == null) {
			return null;
		}
		
		CinemaInfoVO cinemaInfoVO = this.transferToCinemaInfoVO(moocCinemaT);
		
		return cinemaInfoVO;
	}
	
	private CinemaInfoVO transferToCinemaInfoVO(MoocCinemaT moocCinemaT) {
		CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
		cinemaInfoVO.setCinemaId(moocCinemaT.getUuid());
		cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
		cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
		cinemaInfoVO.setCinemaAdress(moocCinemaT.getCinemaAddress());
		cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
		
		return cinemaInfoVO;
	}

	@Override
	public List<CinemaFilmInfoVO> getCinemaFilms(int cinemaId) {
		EntityWrapper<MoocFieldT> wrapper = new EntityWrapper<MoocFieldT>();
//		约束条件
		wrapper.eq("cinema_id", cinemaId);
		
//		通过cinemaId -> filmIds
		List<Integer> filmIds = moocFieldTMapper.getFilmIdsByCinemaId(cinemaId);
		
		List<CinemaFilmInfoVO> cinemaFilmInfoVOs = new ArrayList<CinemaFilmInfoVO>();		
		for(Integer fillmId : filmIds) {			
			MoocHallFilmInfoT moocHallFilmInfoT = new MoocHallFilmInfoT();
			moocHallFilmInfoT.setFilmId(fillmId);
			
//			通过filmId -> filmInfo
			moocHallFilmInfoT = moocHallFilmInfoTMapper.selectOne(moocHallFilmInfoT);
			
//			组装前端VO
			CinemaFilmInfoVO cinemaFilmInfoVO = this.transferToCinemaFilmInfo(moocHallFilmInfoT);
			
//			对每个影片，找出其所有场次
			List<CinemaFieldVO> cinemaFieldVOs = this.getCinemaFields(cinemaId, moocHallFilmInfoT.getFilmId());
			
//			重置每个场次的language
			for(CinemaFieldVO cinemaFieldVO : cinemaFieldVOs) {
				cinemaFieldVO.setLanguage(cinemaFilmInfoVO.getFilmType());
			}
			
			cinemaFilmInfoVO.setFilmFields(cinemaFieldVOs);			
			cinemaFilmInfoVOs.add(cinemaFilmInfoVO);
			
		}
		
		return cinemaFilmInfoVOs;
	}
	
	
	
	private CinemaFilmInfoVO transferToCinemaFilmInfo(MoocHallFilmInfoT moocHallFilmInfoT) {
		CinemaFilmInfoVO cinemaFilmInfoVO = new CinemaFilmInfoVO();
		
		cinemaFilmInfoVO.setActors(moocHallFilmInfoT.getActors());
		cinemaFilmInfoVO.setFilmCats(moocHallFilmInfoT.getFilmCats());
		cinemaFilmInfoVO.setFilmId(moocHallFilmInfoT.getFilmId() + "");
		cinemaFilmInfoVO.setFilmLength(moocHallFilmInfoT.getFilmLength());
		cinemaFilmInfoVO.setFilmName(moocHallFilmInfoT.getFilmName());
		cinemaFilmInfoVO.setFilmType(moocHallFilmInfoT.getFilmLanguage());
		cinemaFilmInfoVO.setImgAddress(moocHallFilmInfoT.getImgAddress());
		
		return cinemaFilmInfoVO;
	}

	@Override
	public List<CinemaFieldVO> getCinemaFields(int cinemaId, int filmId) {
		EntityWrapper<MoocFieldT> wrapper = new EntityWrapper<MoocFieldT>();
//		约束条件
		wrapper.eq("cinema_id", cinemaId);
		wrapper.eq("film_id", filmId);
		
//		找出该影院当前影片的所有影厅，转为前端VO
		List<MoocFieldT> moocFieldTs = moocFieldTMapper.selectList(wrapper);
		List<CinemaFieldVO> cinemaFieldVOs = this.transferToCinemaFieldVOs(moocFieldTs);
		
		return cinemaFieldVOs;
	}
	
	private List<CinemaFieldVO> transferToCinemaFieldVOs(List<MoocFieldT> moocFieldTs) {
		List<CinemaFieldVO> cinemaFieldVOs = new ArrayList<CinemaFieldVO>();
		for(MoocFieldT moocFieldT : moocFieldTs) {
			CinemaFieldVO cinemaFieldVO = new CinemaFieldVO();
			cinemaFieldVO.setFieldId(moocFieldT.getHallId());
			cinemaFieldVO.setBeginTime(moocFieldT.getBeginTime());
			cinemaFieldVO.setEndTime(moocFieldT.getEndTime());
			cinemaFieldVO.setHallName(moocFieldT.getHallName());
			cinemaFieldVO.setPrice(moocFieldT.getPrice() + "");
			cinemaFieldVOs.add(cinemaFieldVO);
		}
		return cinemaFieldVOs;
	}

	@Override
	public CinemaFilmInfoVO getCurrCinemaFilm(int cinemaId, int fieldId) {
//		通过cinemaId和fildId -> filmId	
		MoocFieldT moocFieldT = this.getCurrFieldInfo(cinemaId, fieldId);
		
		if(moocFieldT == null) {
			return null;
		}
		
//		通过filmId -> filmInfo
		MoocHallFilmInfoT moocHallFilmInfoT = new MoocHallFilmInfoT();
		moocHallFilmInfoT.setFilmId(moocFieldT.getFilmId());
		
		moocHallFilmInfoT = moocHallFilmInfoTMapper.selectOne(moocHallFilmInfoT);
		
//		组装前端VO
		CinemaFilmInfoVO cinemaFilmInfoVO = this.transferToCinemaFilmInfo(moocHallFilmInfoT);
		
		return cinemaFilmInfoVO;
	}

	@Override
	public HallInfoVO getCurrHallInfoVO(int cinemaId, int fieldId) {
//		通过cinemaId、fildId -> moocFieldT
		MoocFieldT moocFieldT = this.getCurrFieldInfo(cinemaId, fieldId);
				
		if(moocFieldT == null) {
			return null;
		}
		
//		组装前端VO
		HallInfoVO hallInfoVO = this.transferToHallInfoVO(moocFieldT);
		
		return hallInfoVO;
	}
	
//	根据影院id和场次id来获取当前场次信息
	private MoocFieldT getCurrFieldInfo(int cinemaId, int fieldId) {
		MoocFieldT moocFieldT = new MoocFieldT();
		moocFieldT.setCinemaId(cinemaId);
		moocFieldT.setUuid(fieldId);
		
		moocFieldT = moocFieldTMapper.selectOne(moocFieldT);
		
		return moocFieldT;
	}
	
	private HallInfoVO transferToHallInfoVO(MoocFieldT moocFieldT) {
		HallInfoVO hallInfoVO = new HallInfoVO();
		
		hallInfoVO.setHallFieldId(moocFieldT.getUuid() + "");
		hallInfoVO.setHallName(moocFieldT.getHallName());
		hallInfoVO.setPrice(moocFieldT.getPrice());
		
//		通过hall_id -> seatFile
		MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(moocFieldT.getHallId());
		if(moocHallDictT == null) {
			return null;
		}
		
		hallInfoVO.setSeatFile(moocHallDictT.getSeatAddress());
		
		return hallInfoVO;
	}

	@Override
	public FieldVO getFiledVO(int fieldId) {
		return moocFieldTMapper.getFieldVO(fieldId);
	}
	


}
