package com.stylefeng.guns.rest.modular.cinema;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.CinemaFilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaInfoVO;
import com.stylefeng.guns.api.cinema.vo.CinemaVO;
import com.stylefeng.guns.api.cinema.vo.CinemasVO;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldDetailResVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaRequestVO;
import com.stylefeng.guns.rest.modular.common.Const;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cinema")
public class CinemaController {
	
	@Reference(interfaceClass=CinemaServiceAPI.class, check=false, cache="lru", connections=10)
	private CinemaServiceAPI cinemaAPI;
	
	/**
	 * 查询影院列表
	 * @param cinemaRequestVO
	 * @return
	 */
	@RequestMapping(value="/getCinemas", method=RequestMethod.POST)
	public ResponseVO<List<CinemaVO>> getCinemas(CinemaRequestVO cinemaRequestVO) {
		try {
			CinemasVO cinemasVO = cinemaAPI.getCinemas(cinemaRequestVO.getBrandId(), cinemaRequestVO.getHallType(), cinemaRequestVO.getDistrictId(), 
					cinemaRequestVO.getPageSize(), cinemaRequestVO.getNowPage());
			
			if(cinemasVO != null) {
				return ResponseVO.success(cinemasVO.getCinemas(), Const.IMG_PRE, cinemaRequestVO.getNowPage(), cinemasVO.getTotalPage());
			}
			
			return ResponseVO.serviceFail("抱歉，未查找到相应影院信息");
			
		} catch(Exception e) {
			log.error("获取影院列表异常");
			return ResponseVO.sysFail("获取影院列表异常");
		}
		
	}
	
	/**
	 * 查询影院条件列表
	 * @param brandId
	 * @param hallType
	 * @param areaId
	 * @return
	 */
	@RequestMapping(value="/getCondition", method=RequestMethod.POST)
	public ResponseVO<CinemaConditionVO> getCondition(@RequestParam(value="brandId", defaultValue="99") int brandId,
								   @RequestParam(value="hallType", defaultValue="99") int hallType,
								   @RequestParam(value="areaId", defaultValue="99") int areaId) {
		
		CinemaConditionVO cinemaConditionVO = new CinemaConditionVO();
		
		try {
			cinemaConditionVO.setBrandList(cinemaAPI.getBrands(brandId));
			cinemaConditionVO.setAreaList(cinemaAPI.getAreas(areaId));
			cinemaConditionVO.setHalltypeList(cinemaAPI.getHallTypes(hallType));
			
			return ResponseVO.success(cinemaConditionVO);

		} catch (Exception e) {
			log.error("影院列表条件查询异常");
			return ResponseVO.serviceFail("影院列表条件查询异常");
		}
		
	}
	
	/**
	 * 查询影院场次信息
	 * @param cinemaId
	 * @return
	 */
	@RequestMapping(value="/getFields", method=RequestMethod.POST)
	public ResponseVO<CinemaFieldResVO> getFields(int cinemaId) {
		CinemaFieldResVO cinemaFieldResVO = new CinemaFieldResVO();
		
		try {
			if(cinemaAPI.getCinemaInfo(cinemaId) == null) {
				return ResponseVO.serviceFail("抱歉，未查找到相应影院信息");
			}
			
			cinemaFieldResVO.setCinemaInfo(cinemaAPI.getCinemaInfo(cinemaId));
			cinemaFieldResVO.setFilmList(cinemaAPI.getCinemaFilms(cinemaId));
			
			return ResponseVO.success(cinemaFieldResVO);
		} catch (Exception e) {
			log.error("获取播放场次异常");
			return ResponseVO.serviceFail("获取播放场次异常");
		}	
	}
	
	/**
	 * 查询影院当前场次的详细信息
	 * @param cinemaId
	 * @param fieldId
	 * @return
	 */
	@RequestMapping(value="/getFieldInfo", method=RequestMethod.POST)
	public ResponseVO<CinemaFieldDetailResVO> getFieldInfo(int cinemaId, int fieldId) {
		CinemaFieldDetailResVO cinemaFieldDetailResVO = new CinemaFieldDetailResVO();
		
		try {
	//		组装返回VO
			CinemaInfoVO cinemaInfoVO = cinemaAPI.getCinemaInfo(cinemaId);
			if(cinemaInfoVO == null) {
				return ResponseVO.serviceFail("抱歉，未查找到相应影院信息");
			}
			
			CinemaFilmInfoVO cinemaFilmInfoVO = cinemaAPI.getCurrCinemaFilm(cinemaId, fieldId);
			
			HallInfoVO hallInfoVO = cinemaAPI.getCurrHallInfoVO(cinemaId, fieldId);
//			造几个选座假数据，后续会对接订单模块
			hallInfoVO.setSoldSeats("1,2,3");
			
			cinemaFieldDetailResVO.setCinemaInfo(cinemaInfoVO);
			cinemaFieldDetailResVO.setFilmInfo(cinemaFilmInfoVO);
			cinemaFieldDetailResVO.setHallInfo(hallInfoVO);
			
			return ResponseVO.success(cinemaFieldDetailResVO, Const.IMG_PRE);
		} catch (Exception e) {
			log.error("获取场次信息异常");
			return ResponseVO.serviceFail("获取场次信息异常");
		}
	}
	
	
	

}
