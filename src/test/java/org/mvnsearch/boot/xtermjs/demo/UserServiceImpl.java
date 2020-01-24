package org.mvnsearch.boot.xtermjs.demo;

import org.springframework.stereotype.Component;

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
		return null;
	}

	@Override
	public List<String> findVips(List<Integer> ids) {
		return null;
	}

	public void save(String name, Integer age) {

	}

}
