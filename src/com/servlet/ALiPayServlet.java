package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.service.Service;
import com.util.SignUtils;

public class ALiPayServlet extends HttpServlet {
	// 商户PID
	public static final String PARTNER = "2088421881650043";

	// 收款账号
	public static final String SELLER = "lycckj2016@163.com";

	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAObfsQS/Aj9Lbn6HYeB5f4ASJx4RzKGTg6Zk7DqSNBeIuTl8CV70BpGfH/TDrnbPVdBZzU+LCPUWKkcnfDOOcXbfi6FEmBQBe4gvQ06bpf31D2kwOVdlPPi2+2xJnwLEfgMgsmD5GujgJm40z58YY2/RtVUy9ZeUrgugl7AAUsgfAgMBAAECgYA/IrkD9UAmEvi6fUdU3aTyXwR3gqKVJQQBguPe+JBqRydwAu2FG/KX7A4PH1wcfYKYIZa1w+3FKDSTlMawSqnYNR9AvPMyOuPY8Y6EYsaC7IyXPeo11e63Sbiv9A6wDcY6p68lpz8BefEJXV2OmqnoQap8aPrCGvUERTq5gA0AAQJBAP2yfOXFu8QGACchKZIHTQTxfHpXRI4JaG32qMX5h3hqC1q5pyH10sZyqaTZMoL//zEegXp+xJ+BftSnn2yHSB8CQQDo+CtPQL58DQ9IRtsOUiHwUuSlorZ6O4RHww+5QoUISBH4vXwgnOt2iqsEQDASpyIvXNUZdxggH+fBH+9IxIABAkAGK6MyC38no2W5jeru7FKJPj5i02CNe9kYY5lzR0BuQ1YHXOCdyNbZkMxjgAzkdsmELlPD4PVzU7j7/x+3igrNAkEAhKr2tu4uy6QARO+cxX3+N8Zs0OplMhx8iwVJCf4Ylgu8uELn0gaqjFoRY51OAwFMlV4vOI46neitaKxP7seAAQJAEJvgXv9NuwQ5KPCKrhPkkCq0A+sslbWHnUh7NTFUwt6muAFcNuICHPe3cZOD/lBtCDSDXGZKugEZuCznJ5/4og==";
	public static final String BUS_NAME = "智慧诚才";
	public static final String NOTIFY_URL = "http://121.199.22.246:9891/ALiPayWeb/NotifyServlet";
	public String out_trade_no;

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String model_name = request.getParameter("model_name");
		String model_price = request.getParameter("model_price");
		String model_times = request.getParameter("model_times");
		String card_code = request.getParameter("card_code");
		String orderInfo = getOrderInfo(BUS_NAME, model_name, model_price);
		String sign = sign(orderInfo);
		Service serv = new Service();
		String info = serv.makeOder(card_code, model_name, model_price,
				model_times, out_trade_no);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("orderInfo", orderInfo);
		jsonObject.put("sign", sign);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(jsonObject.toString());
		out.flush();
		out.close();

	}

	private String sign(String content) {

		return SignUtils.sign(content, RSA_PRIVATE);

	}

	private String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + NOTIFY_URL + "\"";

		// 服务接口名称， 固定值ֵ
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值ֵ
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";


		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);
		Random r = new Random();
		key = key + r.nextInt();
		out_trade_no = key.substring(0, 15);
		return out_trade_no;
	}

}
