package com.stylefeng.guns.api.film.vo;

import java.util.List;

import com.stylefeng.guns.api.film.vo.CateVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;

import lombok.Data;

@Data
public class FilmConditionVO {
	private List<CateVO> cateInfo;
	
	private List<SourceVO> sourceInfo;
	
	private List<YearVO> yearInfo; 
}
