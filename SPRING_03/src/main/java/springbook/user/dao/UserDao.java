package springbook.user.dao;

import java.sql.SQLException;
import java.util.List;

import springbook.user.domain.User;

public interface UserDao {

	void add(User user);

	User get(String id);

	List<User> getAll();

	void deleteAll();

	int getCount();

	void addAllTx(List<User> users) throws SQLException;

	int update(User user);

}
