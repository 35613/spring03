package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

public class UserServiceTx implements UserService {

	UserService userService;
	PlatformTransactionManager platformTransactionManager;

	@Override
	public void upgradeLevel() {
		// 트랜잭션 시작
		TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			userService.upgradeLevel();
			// 트랜잭션 커밋
			platformTransactionManager.commit(status);

		} catch (

		RuntimeException e) {
			// 트랜잭션 롤백
			platformTransactionManager.rollback(status);
			throw e;
		}

	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
		this.platformTransactionManager = platformTransactionManager;
	}

	@Override
	public void add(User user) {

	}

}
