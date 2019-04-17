package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

@Data
public class FilmRequestVO {
	
	private Integer showType = 1;
	
	private Integer sortId = 1;
	
	private Integer cateId = 99;
	
	private Integer sourceId = 99;
	
	private Integer yearId = 99;
	
	private Integer nowPage = 1;
	
	private Integer pageSize = 18;

}
