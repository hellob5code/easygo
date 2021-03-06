package com.easygo.model.dao.order;

import java.util.List;

import com.easygo.model.beans.order.Orders;

public interface IOrderDAO {

	/**
	 * 
	 * 对订单进行增删改查
	 */

	// 添加订单
	public abstract boolean addOrders(Orders orders);

	// 删除订单
	public abstract boolean delOrders(int order_id);

	// 修改订单
	public abstract boolean updateOrders(int order_id, Orders orders);

	// 查找所有订单
	public abstract List<Orders> selectAllOrders(int cur);

	// 查找某个用户的订单
	public abstract List<Orders> findSpecOrdersByUserId(int user_id);

	// 根据订单号查询订单信息，修改订单是要用到
	public abstract Orders findOrdersByorderid(int order_id);

	// 模糊查询（订单编号或者房间编号或者房客编号）第二个参数是分页用到的
	public abstract List<Orders> selectsomeOrders(String orderserch, int cur);

	public abstract int getTotalPage();
	// 查找房主的订单（根据房子id进行查询）
	public abstract List<Orders> findOwnerOrdersByHouseId(int house_id);
	public abstract boolean updateOrderBook(int order_id, String book_name,
			String book_tel);

	// 订单状态的更新
	public abstract boolean updateOrderState(int order_id, String order_state);

	// 添加订单并且返回当前订单的id
	public abstract int addOrdersRetrunId(Orders orders);

}
