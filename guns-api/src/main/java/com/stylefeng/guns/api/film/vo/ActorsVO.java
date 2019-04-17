package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;
import java.util.List;

import com.stylefeng.guns.api.film.vo.ActorInfoVO;

import lombok.Data;

@Data
public class ActorsVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ActorInfoVO director;
	
	private List<ActorInfoVO> actors;
	
}
