package springbook.user.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class) // 스프링 테스트 컨텍스트 JUnit 확장 기능 지원
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {

	@Autowired
	UserDao dao;

	@Autowired
	DataSource dataSource;

	private User user1;
	private User user2;
	private User user3;

	private List<User> users;

	@Before
	public void setUp() {

		user1 = new User("james", "다현아빠", "1234", Level.BASIC, 1, 0, "35613@naver.com");
		user2 = new User("james01", "엄마아빠", "1234", Level.SILVER, 56, 10, "35613@naver.com");
		user3 = new User("james02", "아빠엄빠", "1234", Level.GOLD, 100, 40, "35613@naver.com");

		users = new ArrayList<User>();

		users.add(user1);
		users.add(user2);
		users.add(user3);

	}

	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {

		dao.deleteAll();

		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		User vsUser1 = dao.get(user1.getId());
		checkSameCode(user1, vsUser1);

		dao.add(user2);
		assertThat(dao.getCount(), is(2));

		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}

	public void checkSameCode(User user1, User user2) throws SQLException, ClassNotFoundException {

		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));

	}

	@Test
	public void tranTest() throws SQLException {

		dao.deleteAll();

		dao.addAllTx(users);

	}

	@Test
	public void update() throws ClassNotFoundException, SQLException {

		dao.deleteAll();

		dao.add(user1);

		user1.setName("김영재");
		user1.setPassword("1234");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		user1.setEmail("35613@naver.com");
		dao.update(user1);

		User updateUser = dao.get(user1.getId());

		checkSameCode(updateUser, user1);
	}

}
