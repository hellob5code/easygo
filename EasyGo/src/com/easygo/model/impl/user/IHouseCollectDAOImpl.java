package com.easygo.model.impl.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.easygo.model.beans.house.HouseCollect;
import com.easygo.model.dao.user.IHouseCollectDAO;
import com.easygo.utils.C3P0Utils;
import com.easygo.utils.Paging;

public class IHouseCollectDAOImpl implements IHouseCollectDAO {

	private Connection connection = null;
	private PreparedStatement statement = null;
	private ResultSet resultSet = null;
	
	Paging paging = new Paging();

	// 将数据处理，计算出需要分成几页
	// 改动的话只需改动查询语句即可
	public int getTotalPage() {
		// 查询语句，查出数据总的记录数
		String table = "house";
		int count = 0;
		count = paging.getTotalPage(table);
		return count;
	}

	@Override
	public boolean addHouseCollect(HouseCollect houseCollect) {
		// TODO Auto-generated method stub
		boolean result = false;
		connection = C3P0Utils.getConnection();
		String sql = "INSERT INTO house_collect(user_id,house_id) VALUES(?,?);";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, houseCollect.getUser_id());
			statement.setInt(2, houseCollect.getHouse_id());
			statement.executeUpdate();
			result = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return result;
	}

	@Override
	public boolean delHouseCollect(int house_collect_id) {
		// TODO Auto-generated method stub
		boolean result = false;
		connection = C3P0Utils.getConnection();
		String sql = "DELETE FROM house_collect WHERE house_collect_id=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, house_collect_id);

			statement.executeUpdate();
			result = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return result;
	}

	@Override
	public List<HouseCollect> findHouseCollectByUserId(int user_id) {
		// TODO Auto-generated method stub
		List<HouseCollect> houseCollectList = new ArrayList<>();
		connection = C3P0Utils.getConnection();
		String sql = "select * from house_collect where user_id =?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int house_collect_id = resultSet.getInt(1);
				int user_id2 = resultSet.getInt(2);
				int house_id = resultSet.getInt(3);
				HouseCollect houseCollect = new HouseCollect(house_collect_id,
						user_id2, house_id);

				houseCollectList.add(houseCollect);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return houseCollectList;
	}

	// 判断某个用户是否收藏了某个房源
	@Override
	public boolean findHouseCollectById(int user_id, int house_id) {
		// TODO Auto-generated method stub
		boolean result = false;
		connection = C3P0Utils.getConnection();
		String sql = "select * from house_collect where user_id =? AND house_id=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.setInt(2, house_id);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = true;
				return result;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return result;
	}

	@Override
	public boolean deleteHouseCollectById(int user_id, int house_id) {
		// TODO Auto-generated method stub
		boolean result = false;
		connection = C3P0Utils.getConnection();
		String sql = "DELETE FROM house_collect WHERE user_id=? AND house_id = ?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.setInt(2, house_id);
			statement.executeUpdate();
			result = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return result;
	}
	@Override
	public List<Integer> findHouseCollectByUserIdCur(int cur, int user_id) {
		List<Integer> housecollectList = new ArrayList<Integer>();
		connection = C3P0Utils.getConnection();
		String sql = "select * from house_collect where user_id =? limit ?,?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);

			// 分页处理
			statement.setInt(2, (cur - 1) * paging.getPageSize());
			statement.setInt(3, paging.getPageSize());

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				//int house_collect_id = resultSet.getInt(1);
				//int user_id1 = resultSet.getInt(2);
				int house_id = resultSet.getInt(3);
				housecollectList.add(house_id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return housecollectList;
	}


}
