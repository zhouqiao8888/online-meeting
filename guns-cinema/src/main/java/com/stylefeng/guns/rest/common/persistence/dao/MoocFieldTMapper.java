package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.cinema.vo.FieldVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFieldT;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2019-02-28
 */
public interface MoocFieldTMapper extends BaseMapper<MoocFieldT> {
	
	List<Integer> getFilmIdsByCinemaId(int cinemaId);
	
	FieldVO getFieldVO(int fieldId);

}
