package org.mvnsearch.boot.xtermjs.demo;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * user service implementation
 *
 * @author linux_china
 */
@Component("userService")
public class UserServiceImpl implements UserService {

	@Override
	public String findRealName(Integer id) {
		return "linux_china";
	}

	@Override
	public List<String> findVips(List<Integer> ids) {
		return Arrays.asList("first", "second");
	}

	public void save(String name, Integer age) {

	}

}
