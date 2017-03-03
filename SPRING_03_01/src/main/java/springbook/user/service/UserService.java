package springbook.user.service;

import springbook.user.domain.User;

public interface UserService {
	void upgradeLevel();

	void add(User user);
}
