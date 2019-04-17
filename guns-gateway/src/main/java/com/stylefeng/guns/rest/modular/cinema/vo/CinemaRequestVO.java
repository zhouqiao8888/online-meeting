package com.stylefeng.guns.rest.modular.cinema.vo;

import lombok.Data;

@Data
public class CinemaRequestVO {
	
	private Integer brandId = 99;
	
	private Integer hallType = 99;
	
	private Integer districtId = 99;
	
	private Integer pageSize = 12;
	
	private Integer nowPage = 1;
}
