package springbook.user.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserServiceimpl implements UserService {

	private UserDao userDao;

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMAND_FOR_GOLD = 30;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void upgradeLevel() {

		List<User> userList = userDao.getAll();
		for (User user : userList) {
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}

	public boolean canUpgradeLevel(User user) {
		Level currenLevel = user.getLevel();
		switch (currenLevel) {
		case BASIC:
			return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER:
			return (user.getRecommend() >= MIN_RECOMMAND_FOR_GOLD);
		case GOLD:
			return false;
		default:
			throw new IllegalArgumentException("Uknown Level" + currenLevel);
		}
	}

	public void upgradeLevel(User user) {
		if (user.getLevel() == Level.BASIC)
			user.setLevel(Level.SILVER);
		else if (user.getLevel() == Level.SILVER)
			user.setLevel(Level.GOLD);

		this.userDao.update(user);
		sendUpgradeEmail(user);

	}

	private void sendUpgradeEmail(User user) {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("356131@gmail.com", "gold6541");
			}
		};

		Session session = Session.getDefaultInstance(props, auth);
		MimeMessage message = new MimeMessage(session);

		try {
			// 보내는이
			message.setFrom(new InternetAddress("356131@gmail.com"));
			// 받는이
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			// 제목
			message.setSubject("git_hub 등업 알림 mail");
			// 내용
			message.setText(user.getName() + " 님은 " + user.getLevel().name() + " 로 등업되셨습니다.");

			Transport.send(message);

		} catch (AddressException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public void add(User user1) {
		if (user1.getLevel() == null)
			user1.setLevel(Level.BASIC);
		userDao.add(user1);
	}
}
