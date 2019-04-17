package com.stylefeng.guns.rest.modular.film;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceAPI;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.CateVO;
import com.stylefeng.guns.api.film.vo.FilmConditionVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.api.film.vo.FilmIndexVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.common.Const;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;

@RestController
@RequestMapping("/film")
public class FilmController {
	
	@Reference(interfaceClass=FilmServiceAPI.class, check=false)
	private FilmServiceAPI filmServiceAPI;
	
//	异步的film服务接口
	@Reference(interfaceClass=FilmAsyncServiceAPI.class, async=true, check=false)
	private FilmAsyncServiceAPI filmAsyncServiceAPI;
	
	
	/**
	 * 	首页：服务聚合多个请求接口
	 * @return
	 */
	@RequestMapping(value="/getIndex", method=RequestMethod.GET)
	public ResponseVO<FilmIndexVO> getIndex() {
		FilmIndexVO filmIndexVO = new FilmIndexVO();
		boolean isLimit = true;
		int num = 8;
		
//		标题栏
		List<BannerVO> bannerVOs = filmServiceAPI.getBanners();
//		热映影片
		FilmVO hotFilms = filmServiceAPI.getHotFilms(isLimit, num);
//		即将上映影片
		FilmVO soonFilms = filmServiceAPI.getSoonFilms(isLimit, num);
//		院线影片排名
		List<FilmInfo> boxFilmInfos = filmServiceAPI.getBoxRanking();
//		期待影片排名
		List<FilmInfo> expectFilmInfos = filmServiceAPI.getExpectRanking();
//		top100
		List<FilmInfo> topFilmInfos = filmServiceAPI.getTop();
		
		filmIndexVO.setBanners(bannerVOs);
		filmIndexVO.setHotFilms(hotFilms);
		filmIndexVO.setSoonFilms(soonFilms);
		filmIndexVO.setBoxRanking(boxFilmInfos);
		filmIndexVO.setExpectRanking(expectFilmInfos);
		filmIndexVO.setTop100(topFilmInfos);
		return ResponseVO.success(filmIndexVO, Const.IMG_PRE);
	}
	
	/**
	 * 	条件列表查询接口
	 * @param cateId
	 * @param sourceId
	 * @param yearId
	 * @return
	 */
	@RequestMapping(value="/getConditionList", method=RequestMethod.POST)
	public ResponseVO<FilmConditionVO> getConditionList(@RequestParam(value="catId", defaultValue="99") String cateId,
									   @RequestParam(value="sourceId", defaultValue="99") String sourceId,
									   @RequestParam(value="yearId", defaultValue="99") String yearId) {
		
		FilmConditionVO filmConditionVO = new FilmConditionVO();
		
//		通过service层获取目录，区域，年代的所有标签
		List<CateVO> cateVOs = filmServiceAPI.getCateInfo(cateId);
		List<SourceVO> sourceVOs = filmServiceAPI.getSourceInfo(sourceId);
		List<YearVO> yearVOs = filmServiceAPI.getYearInfo(yearId);
		
//		组装返回前端的VO
		filmConditionVO.setCateInfo(cateVOs);
		filmConditionVO.setSourceInfo(sourceVOs);
		filmConditionVO.setYearInfo(yearVOs);
		
		return ResponseVO.success(filmConditionVO);
	}
	
	/**
	 * 	影片查询接口
	 * @param requestVO
	 * @return
	 */
	@RequestMapping(value="/getFilms", method=RequestMethod.POST) 
	public ResponseVO<List<FilmInfo>> getFilms(FilmRequestVO requestVO) {
		
		FilmVO filmVO = filmServiceAPI.getFilms(requestVO.getShowType(), requestVO.getSortId(), requestVO.getCateId(), 
				requestVO.getSourceId(), requestVO.getYearId(), requestVO.getNowPage(), requestVO.getPageSize());
						
		return ResponseVO.success(filmVO.getFilmInfoList(), Const.IMG_PRE, requestVO.getNowPage(), filmVO.getTotalPage());
	}
	
	/**
	 * 影片详情查询接口
	 * @param searchParam
	 * @param searchType
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@RequestMapping(value="/getFilmsDetail/{searchParam}", method=RequestMethod.POST)
	public ResponseVO<FilmDetailVO> getFilmsDetail(@PathVariable(value="searchParam") String searchParam, int searchType) throws InterruptedException, ExecutionException {
//		异步获取影片主体信息
//		FilmDetailVO filmDetailVO = filmAsyncServiceAPI.getFilmDetail(searchParam, searchType);	
		filmAsyncServiceAPI.getFilmDetail(searchParam, searchType);	
		Future<FilmDetailVO> filmDetailFuture = RpcContext.getContext().getFuture();
		
//		异步获取第四部分的信息描述
//		FilmDescVO filmDescVO = filmAsyncServiceAPI.getFilmDesc(searchParam, searchType);
		filmAsyncServiceAPI.getFilmDesc(searchParam, searchType);
		Future<FilmDescVO> filmDescFuture = RpcContext.getContext().getFuture();
		
		FilmDetailVO filmDetailVO = filmDetailFuture.get();
		if(filmDetailVO == null) {
			return ResponseVO.serviceFail("抱歉，未找到该影片");
		}
		filmDetailVO.setInfo04(filmDescFuture.get());		
		
		return ResponseVO.success(filmDetailVO, Const.IMG_PRE);
	}


}
