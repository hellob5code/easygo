package com.easygo.model.dao.user;

import java.util.List;

import com.easygo.model.beans.user.User;

public interface IUserDAO {
	
	// 注册用户
	public abstract boolean addUser(User user);

	// 注册用户,只提供手机号码和密码
	public abstract boolean register(User user);

	// 删除用户(通过账号删除)
	public abstract boolean delUser(int user_no);

	// 修改用户（通过账号修改）
	public abstract boolean updateUser(String user_no, User user);

	// 查找用户（通过账号查询）
	public abstract User findSpecUserByNo(int user_no);

	// 查找用户（通过ID查询）
	public abstract User findSpecUserById(int user_id);
	
	// 查找用户（通过手机号查询）
	public abstract User selectUser(String phone);

	// 所有用户
	public abstract List<User> selectAllUser();

	// 修改用户头像
	public abstract boolean updateUserPhoto(int user_id, String user_photo);

	// 用户进行登录
	public abstract String login(String user_no, String user_password);

    //根据输入的手机号进行user_id的查找
	public abstract int selectUserID(String phone);
	//根据输入的id进行手机号的查找
	public abstract String selectUserPhone(int user_id);
	//根据用户id更新用户信息
	public abstract boolean updateUserById(int user_id, User user);

	// 根据用户账号查找用户头像和用户签名在我的页面
	public abstract User selectPhotoMoodByUserId(int user_id);
	
	//实名认证成功后会把数据保存在数据库中
	public abstract boolean updateUserRealname(int user_id,String real_name,String idcard);

}
