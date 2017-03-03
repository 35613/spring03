package springbook.user.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserDaoImpl implements UserDao {
	private JdbcTemplate jdbcTemplete;
	private DataSource dataSource;
	private RowMapper<User> rowMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {

			User user = new User();
			user.setId(rs.getString("ID"));
			user.setName(rs.getString("NAME"));
			user.setPassword(rs.getString("PASSWORD"));
			user.setLevel(Level.valueOf(rs.getInt("LVL")));
			user.setLogin(rs.getInt("LOGIN"));
			user.setRecommend(rs.getInt("RECOMMEND"));
			user.setEmail(rs.getString("EMAIL"));
			return user;
		}
	};

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplete = new JdbcTemplate(dataSource);
	}

	public void add(User user) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT         \n");
		sb.append("INTO MEMBERS   \n");
		sb.append("  (            \n");
		sb.append("    ID,        \n");
		sb.append("    NAME,      \n");
		sb.append("    PASSWORD,  \n");
		sb.append("    LVL,       \n");
		sb.append("    LOGIN,     \n");
		sb.append("    RECOMMEND,  \n");
		sb.append("    EMAIL  \n");
		sb.append("  )            \n");
		sb.append("  VALUES       \n");
		sb.append("  (            \n");
		sb.append("    ?,     	  \n");
		sb.append("    ?,      	 \n");
		sb.append("    ?,       \n");
		sb.append("    ?,       \n");
		sb.append("    ?,       \n");
		sb.append("    ?,        \n");
		sb.append("    ?        \n");
		sb.append("  )           \n");

		this.jdbcTemplete.update(sb.toString(), user.getId(), user.getName(), user.getPassword(),
				user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
	}

	public User get(String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("	SELECT ID, NAME, PASSWORD, LVL, LOGIN, RECOMMEND, EMAIL  \n");
		sb.append("	FROM MEMBERS 									  \n");
		sb.append("	WHERE ID = ?								  	  \n");

		return this.jdbcTemplete.queryForObject(sb.toString(), new Object[] { id }, rowMapper);
	}

	public List<User> getAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("	SELECT ID, NAME, PASSWORD, LVL, LOGIN, RECOMMEND,EMAIL  \n");
		sb.append("	FROM MEMBERS 									  \n");

		return this.jdbcTemplete.query(sb.toString(), rowMapper);
	}

	public void deleteAll() {
		StringBuilder sb = new StringBuilder();

		sb.append("	DELETE			\n");
		sb.append("	FROM MEMBERS	\n");

		this.jdbcTemplete.update(sb.toString());
	}

	public int getCount() {
		StringBuilder sb = new StringBuilder();

		sb.append("	SELECT COUNT(*)		\n");
		sb.append("	FROM MEMBERS		\n");

		return this.jdbcTemplete.queryForInt(sb.toString());
	}

	public void addAllTx(List<User> users) throws SQLException {
		// 트렌젝션 동기화 매니저를 이용해 초기화
		TransactionSynchronizationManager.initSynchronization();
		Connection c = DataSourceUtils.getConnection(dataSource);
		c.setAutoCommit(false);
		try {
			for (User user : users) {
				add(user);
			}
			// 정상처리
			c.commit();
		} catch (SQLException e) {
			// 실패시
			c.rollback();
			throw e;
		} finally {
			// DB커넥션 종료
			DataSourceUtils.releaseConnection(c, dataSource);
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}

	}

	public int update(User user) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE MEMBERS       ");
		sb.append("   SET NAME      = ?  ");
		sb.append("     , PASSWORD  = ?  ");
		sb.append("     , LVL       = ?  ");
		sb.append("     , LOGIN     = ?  ");
		sb.append("     , RECOMMEND = ?  ");
		sb.append("     , EMAIL = 	  ?  ");
		sb.append(" WHERE ID        = ?  ");

		return this.jdbcTemplete.update(sb.toString(), user.getName(), user.getPassword(), user.getLevel().intValue(),
				user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
	}

}
