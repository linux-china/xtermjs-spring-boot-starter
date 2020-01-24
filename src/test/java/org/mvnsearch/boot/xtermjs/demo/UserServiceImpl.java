package org.mvnsearch.boot.xtermjs.demo;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * user service implementation
 *
 * @author linux_china
 */
@Component("userService")
public class UserServiceImpl implements UserService {

	@Override
	public User findById(Integer id) {
		User user = new User();
		user.setId(id);
		user.setName("linux_china");
		user.setBirth(new Date());
		return user;
	}

	@Override
	public String findRealName(Integer id) {
		return "linux_china";
	}

	@Override
	public List<String> findVips(List<Integer> ids) {
		return Arrays.asList("first", "second");
	}

	@Override
	public void save(String name, Integer age) {

	}

}
