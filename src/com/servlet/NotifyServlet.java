package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.service.Service;

public class NotifyServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String out_trade_no=req.getParameter("out_trade_no");//商品订单号
		String gmt_payment=req.getParameter("gmt_payment");//订单付款时间
		String trade_no=req.getParameter("trade_no");//支付宝交易号
		Service serv = new Service();
		String info=serv.changeOrder(out_trade_no, trade_no, gmt_payment);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		out.print("success");
		out.flush();
		out.close();
		
	}

}
