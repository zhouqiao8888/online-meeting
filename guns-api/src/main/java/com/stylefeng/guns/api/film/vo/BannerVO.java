package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;


@Data
public class BannerVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String bannerId;
	
	private String bannerAddress;
	
	private String bannerUrl;

	
}
