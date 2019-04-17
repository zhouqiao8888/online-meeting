package com.stylefeng.guns.rest.modular.vo;


import lombok.Data;

@Data
public class ResponseVO <T> {
	
	private int status;
	
	private String msg;
	
	private T data;
	

//	图片地址
	private String imgPre;
	
//	当前页
	private Integer nowPage;
	
//	总页数
	private Integer totalPage;
	

//	单例模式：构造方法私有，对外暴露公共方法
	private ResponseVO () {}
	
//	0表示成功，1表示业务异常，999表示系统异常
	public static <T> ResponseVO<T> success(T data) {
		ResponseVO<T> responseVO = new ResponseVO<T>();
		responseVO.setStatus(0);
		responseVO.setData(data);
		return responseVO;
	}
	
	public static <T> ResponseVO<T> success(T data, String imgPre, Integer nowPage, Integer totalPage) {
		ResponseVO<T> responseVO = new ResponseVO<T>();
		responseVO.setStatus(0);
		responseVO.setData(data);
		responseVO.setImgPre(imgPre);
		responseVO.setNowPage(nowPage);
		responseVO.setTotalPage(totalPage);
		return responseVO;
	}
	
	public static <T> ResponseVO<T> success(T data, String imgPre) {
		ResponseVO<T> responseVO = new ResponseVO<T>();
		responseVO.setStatus(0);
		responseVO.setData(data);
		responseVO.setImgPre(imgPre);
		return responseVO;
	}
	
	
	public static <T> ResponseVO<T> success(String msg) {
		ResponseVO<T> responseVO = new ResponseVO<T>();
		responseVO.setStatus(0);
		responseVO.setMsg(msg);
		return responseVO;
	}
	
	public static <T> ResponseVO<T> serviceFail(String msg) {
		ResponseVO<T> responseVO = new ResponseVO<T>();
		responseVO.setStatus(1);
		responseVO.setMsg(msg);
		return responseVO;
	}
	
	public static <T> ResponseVO<T> sysFail(String msg) {
		ResponseVO<T> responseVO = new ResponseVO<T>();
		responseVO.setStatus(999);
		responseVO.setMsg(msg);
		return responseVO;
	}
	
}
