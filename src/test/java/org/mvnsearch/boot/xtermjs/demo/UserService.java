package org.mvnsearch.boot.xtermjs.demo;

import java.util.List;

/**
 * user service
 *
 * @author linux_china
 */
public interface UserService {

	public String findRealName(Integer id);

	public List<String> findVips(List<Integer> ids);

	public void save(String name, Integer age);

}
