package com.stylefeng.guns.api.film.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ActorInfoVO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String imgAddress;
	
	private String directorName;
	
	private String actorName;
	
	private String roleName;

}
