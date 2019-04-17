package com.stylefeng.guns.rest.modular.alipay.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.stylefeng.guns.api.alipay.AlipayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.PayQrcodeInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayStatusInfoVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.modular.alipay.common.Const;
import com.stylefeng.guns.rest.modular.alipay.util.FTPUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Service(interfaceClass=AlipayServiceAPI.class, mock="com.stylefeng.guns.api.alipay.AlipayServiceMock")
public class AlipayServiceImpl implements AlipayServiceAPI {
	
//	如果原有的OrderService已经分组，则引用它们的时候需要指明分组名称
	@Reference(interfaceClass=OrderServiceAPI.class, group="order_2019", check=false)
	private OrderServiceAPI orderServiceAPI;
	
	// 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;
    
    @Autowired
	private FTPUtil ftpUtil;
    
    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
            .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
            .setFormat("json").build();
    }


	@Override
	public PayQrcodeInfoVO getPayQrcodeInfo(String orderId) {
		String filePath = this.test_trade_precreate(orderId);
		
		if(!StringUtils.isEmpty(filePath)) {
			PayQrcodeInfoVO payQrcodeInfoVO = new PayQrcodeInfoVO();
			
			payQrcodeInfoVO.setOrderId(orderId);
			payQrcodeInfoVO.setQRCodeAddress(filePath);
			
			return payQrcodeInfoVO;
		}
		
		return null;
	}

	@Override
	public PayStatusInfoVO getPayStatusInfo(String orderId) {
//		服务降级测试：延迟4s得不到服务，会返回mock类里的处理结果，注意只能捕获RpcException
//		int i = 1 / 0;
//		try {
//			Thread.sleep(4000);
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		}
		
		String userId = RpcContext.getContext().getAttachment("userId");
		log.info("userId: " + userId);
		
		boolean flag = this.test_trade_query(orderId);
		
		if(flag) {
//			更新数据库订单状态为已支付
			boolean status = orderServiceAPI.paySuccess(orderId);
			if(status) {
				log.info("订单支付状态更新为已支付");
			}
			else {
				log.info("订单支付状态更新失败");
				return null;
			}
			
			PayStatusInfoVO payStatusInfoVO = new PayStatusInfoVO();
			
			payStatusInfoVO.setOrderId(orderId);
			payStatusInfoVO.setOrderMsg("支付成功");
			payStatusInfoVO.setOrderStatus(1);
			
			return payStatusInfoVO;
		}
		else {
//			更新数据库订单状态为已关闭
			boolean status = orderServiceAPI.payFail(orderId);
			if(status) {
				log.info("订单支付状态更新为已关闭");
			}
			else {
				log.info("订单支付状态更新失败");
			}
			return null;
		}
		
	}
	
	
	 // 测试当面付2.0生成支付二维码
    private String test_trade_precreate(String orderId) {
    	
    	OrderInfoVO orderInfoVO = orderServiceAPI.getOrderInfoVOByOrderId(orderId);
    	String filePath = "";
    	
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderId;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "Meeting院线当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = orderInfoVO.getOrderPrice();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "Meeting院线当面付扫码消费共" + orderInfoVO.getOrderPrice() + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
//        // 创建好一个商品后添加至商品明细列表
//        goodsDetailList.add(goods1);
//
//        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
//            本项目主动查询订单支付状态，没有用到支付宝回调
            //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String fileName = String.format("qr-%s.png", response.getOutTradeNo());
                filePath = String.format("F:/eclipse_workspace/qr-photos/%s", fileName);
                log.info("filePath:" + filePath);
                
                File qrCodeImagefile = ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                
//               将二维码文件上传到远程ftp服务器
                List<File> fileList = new ArrayList<File>();
                fileList.add(qrCodeImagefile);
                
				try {
					boolean flag = ftpUtil.uploadFile(fileList);
					if(!flag) {
						return "";
					}
//					返回ftp服务器路径
					filePath = Const.FTP_URL + fileName;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
//				删除本地二维码文件
				boolean flag = qrCodeImagefile.delete();
				if(flag) {
					log.info("本地二维码文件删除成功");
				}
				else {
					log.info("本地二维码文件删除失败");
				}
			
                break;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        
        return filePath;
    }
    
    // 测试当面付2.0查询订单
    private boolean test_trade_query(String orderId) {
    	
    	boolean flag = false;
    	
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
    	String outTradeNo = orderId;

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
            .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");
                flag = true;

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                break;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        
        return flag;
    }
    
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

}
