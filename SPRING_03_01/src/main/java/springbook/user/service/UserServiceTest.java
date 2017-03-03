package springbook.user.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;

	@Autowired
	UserServiceimpl userServiceimpl;

	@Autowired
	UserDao userDao;

	@Autowired
	PlatformTransactionManager platformTransactionManager;

	List<User> users;

	public void setUserDao(UserDao userDao) {

		this.userDao = userDao;

	}

	@Before
	public void setUp() {

		users = Arrays.asList(

				new User("git_hub01", "이인재", "p1", Level.BASIC, 49, 0, "356131@gmail.com"),

				new User("git_hub02", "김영재", "p2", Level.BASIC, 50, 0, "356131@gmail.com"), // basic
				// ->silver
				new User("git_hub03", "전창건", "p3", Level.SILVER, 60, 29, "356131@gmail.com"),

				new User("git_hub04", "황인배", "p4", Level.SILVER, 60, 30, "356131@gmail.com"), // silver
				// ->gold
				new User("git_hub05", "김태영", "p5", Level.GOLD, 100, 30, "356131@gmail.com")

		);

	}

	@Test
	public void upgradeLevels() throws Exception {

		userDao.deleteAll();

		for (User user : users) {

			userDao.add(user);
		}

		userService.upgradeLevel();

		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);

	}

	@Test
	public void bean() {

		assertThat(this.userService, is(notNullValue()));

	}

	private void checkLevel(User user, boolean upgraded) {

		User userUpdate = userDao.get(user.getId());

		if (upgraded == true) {

			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));

		} else {

			assertThat(userUpdate.getLevel(), is(user.getLevel()));

		}

	}

	private void checkLevel(User user, Level expectedLevel) {

		User userUpdate = userDao.get(user.getId());

		assertThat(userUpdate.getLevel(), is(expectedLevel));

	}

	static class TestUserService extends UserServiceimpl {

		private String id;

		public TestUserService(String id) {

			this.id = id;

		}

		public void upgradeLevel(User user) {

			if (user.getId().equals(this.id))

				throw new TestUserServiceException();

			super.upgradeLevel(user);

		}

	}

	static class TestUserServiceException extends RuntimeException {

		public TestUserServiceException() {

			super("널 위해 준비했어 익셉션");

		}

	}

	@Test(expected = TestUserServiceException.class)
	public void allOrNothing() throws Exception {

		UserServiceimpl testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);

		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setPlatformTransactionManager(platformTransactionManager);
		txUserService.setUserService(txUserService);

		userDao.deleteAll();

		for (User user : users) {
			userDao.add(user);
		}
		try {
			txUserService.upgradeLevel();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
			throw e;
		}

	}

}
