package org.mvnsearch.boot.xtermjs.demo;

import java.util.Date;

/**
 * user class
 *
 * @author linux_china
 */
public class User {

	private Integer id;

	private String name;

	private Date birth;

	public User() {
	}

	public User(Integer id, String name, Date birth) {
		this.id = id;
		this.name = name;
		this.birth = birth;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

}
