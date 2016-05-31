package com.easygo.model.dao.user;

import com.easygo.model.beans.house.HouseCollect;

public interface IHouseCollectDAO {
	/**
	 * 对收藏进行增删
	 */
	// 添加收藏
	public abstract boolean addHouseCollect(HouseCollect houseCollect);

	// 删除收藏
	public abstract boolean delHouseCollect(int house_collect_id);

	// 查找某个用户的收藏列表
	public abstract HouseCollect findHouseCollectByUserId(int user_id);

}
