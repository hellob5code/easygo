package com.easygo.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import com.easygo.model.beans.house.House;
import com.easygo.model.beans.order.Orders;
import com.easygo.model.beans.user.User;
import com.easygo.model.dao.house.IHouseDAO;
import com.easygo.model.dao.order.IOrderDAO;
import com.easygo.model.dao.user.IUserDAO;
import com.easygo.model.impl.house.IHouseDAOImpl;
import com.easygo.model.impl.order.IOrderDAOImpl;
import com.easygo.model.impl.user.IUserDAOImpl;
import com.google.gson.Gson;

//@WebServlet("/easygoservlet")
public class EasygoServlet extends HttpServlet {
	private static final long serialVrsionUID = 1L;
	// 用于输出数据
	private PrintWriter mPrintWriter;

	// user的相关方法
	IUserDAO userdao;
	// user的相关对象
	List<User> userList;
	int user_no;
	User user;

	// House的相关方法
	IHouseDAO housedao;
	// House相关对象
	List<House> houseList;
	House house;

	public EasygoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 初始化
		mPrintWriter = response.getWriter();
		// 获取当前页面显示的第几页
		String cur = request.getParameter("cur");
		// 第一次加载页面，让其显示第一页
		if (cur == null) {
			cur = "1";
		}
		String method = request.getParameter("methods");

		switch (method) {
		case "goAddUser":
			request.setAttribute("oneUser", user);
			request.getRequestDispatcher("jsp/user/addUser.jsp").forward(
					request, response);
			break;
		case "addUser":
			user = new User();
			try {
				BeanUtils.populate(user, request.getParameterMap());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(user.getUser_no());
			// userdao.addUser(user);
			if (userdao.addUser(user)) {
				System.out.println("成功");
			}

			break;
		case "addUserSuccess":
			break;
		case "deleteUser":
			// 得到要删除的user_no
			user_no = Integer.valueOf(request.getParameter("no"));
			userdao = new IUserDAOImpl();
			boolean delResult = userdao.delUser(user_no);
			// 属性名为oneUser
			request.setAttribute("delResult", delResult);
			if (delResult) {
				response.sendRedirect("easygoservlet?methods=getAllUser");
			}
			// request.getRequestDispatcher("jsp/user/user.jsp").forward(request,
			// response);
			break;
		case "getAllUser":
			userList = new ArrayList<User>();
			userdao = new IUserDAOImpl();
			userList = userdao.selectAllUser();
			request.setAttribute("userList", userList);
			request.getRequestDispatcher("jsp/user/user.jsp").forward(request,
					response);
			break;
		case "findoneUser":
			// 得到要查询的user_no
			user_no = Integer.valueOf(request.getParameter("no"));
			userdao = new IUserDAOImpl();
			user = userdao.findSpecUserByNo(user_no);
			// 属性名为oneUser
			request.setAttribute("oneUser", user);
			request.getRequestDispatcher("jsp/user/selectOneUser.jsp").forward(
					request, response);
			break;
		case "updateUser":
			// 得到要查询的user_no
			user_no = Integer.valueOf(request.getParameter("no"));
			userdao = new IUserDAOImpl();
			user = userdao.findSpecUserByNo(user_no);
			// 属性名为oneUser
			request.setAttribute("oneUser", user);
			request.getRequestDispatcher("jsp/user/selectOneUser.jsp").forward(
					request, response);
			break;

		case "addOrders":
			IOrderDAO order = new IOrderDAOImpl();
			// 获取到订单信息的每个字段
			// BeanUtils.populate(order, request.getParameterMap());
			int house_id = Integer.valueOf(request.getParameter("house_id"));
			int user_id = Integer.valueOf(request.getParameter("user_id"));
			int checknum = Integer.valueOf(request.getParameter("checknum"));
			String checktime = request.getParameter("checktime");
			String leavetime = request.getParameter("leavetime");
			double total = Double.valueOf(request.getParameter("total"));
			String tel = request.getParameter("tel");
			String order_state = request.getParameter("order_state");
			String order_time = request.getParameter("order_time");
			// 添加订单信息
			// 用网页信息初始化订单对象
			Orders orders = new Orders(house_id, user_id, checknum, checktime,
					leavetime, total, tel, order_state, order_time);
			// 向数据库中添加信息
			order.addOrders(orders);

			request.getRequestDispatcher("jsp/order/order.jsp").forward(
					request, response);
			break;
		// 得到全部订单
		case "getAllorder":
			IOrderDAO order1 = new IOrderDAOImpl();
			List<Orders> orderlist = new ArrayList<Orders>();
			orderlist = order1.selectAllOrders(Integer.parseInt(cur));
			// 总共被分成了几页
			int totalPage = order1.getTotalPage();
			request.setAttribute("orderlist", orderlist);
			request.setAttribute("cur", cur);
			request.setAttribute("totalPage", totalPage);
			request.getRequestDispatcher("jsp/order/order.jsp").forward(
					request, response);
			break;
		case "delOrders":
			IOrderDAO order2 = new IOrderDAOImpl();
			int order_id = Integer.parseInt(request.getParameter("order_id"));
			if (order2.delOrders(order_id)) {
				request.getRequestDispatcher(
						"easygoservlet?methods=getAllorder").forward(request,
						response);
			}
			break;
		// 根据订单号得到订单
		case "getorderbyorderid":
			IOrderDAO order3 = new IOrderDAOImpl();
			int order_id3 = Integer.parseInt(request.getParameter("order_id"));
			Orders orders3 = order3.findOrdersByorderid(order_id3);
			request.setAttribute("orders", orders3);
			request.getRequestDispatcher("jsp/order/updateOrder.jsp").forward(
					request, response);
			break;
		// 修改订单
		case "updateorder":
			Orders orders4 = new Orders();
			IOrderDAO order4 = new IOrderDAOImpl();
			try {
				BeanUtils.populate(orders4, request.getParameterMap());
				int order_id4 = Integer.valueOf(request
						.getParameter("order_id"));
				order4.updateOrders(order_id4, orders4);
				request.getRequestDispatcher(
						"easygoservlet?methods=getAllorder").forward(request,
						response);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "selectsomeOrders":
			IOrderDAO order5 = new IOrderDAOImpl();
			List<Orders> orderlist5 = new ArrayList<Orders>();
			String orderserch = request.getParameter("orderserch");
			orderlist5 = order5.selectsomeOrders(orderserch,
					Integer.parseInt(cur));
			// 总共被分成了几页
			int totalPage5 = order5.getTotalPage();
			request.setAttribute("orderlist", orderlist5);
			request.setAttribute("cur", cur);
			request.setAttribute("totalPage", totalPage5);
			request.getRequestDispatcher("jsp/order/serchorder.jsp").forward(
					request, response);
			break;
		case "getAllHouse":
			houseList = new ArrayList<House>();
			housedao = new IHouseDAOImpl();
			houseList = housedao.selectAllHouse();
			Gson gson = new Gson();
			String result = gson.toJson(houseList);
			mPrintWriter.write(result);
			mPrintWriter.close();
			break;
		case "getAllOrderByUserId":
			IOrderDAO order6 = new IOrderDAOImpl();
			List<Orders> orderlist6 = new ArrayList<Orders>();
			String userid = request.getParameter("userid");
			orderlist6 = order6.selectsomeOrders(userid, 1);
			Gson gson6 = new Gson();
			String result6 = gson6.toJson(orderlist6);
			mPrintWriter.write(result6);
			mPrintWriter.close();
			break;
		default:
			break;
		}
	}
}
