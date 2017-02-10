package com.sm.demo.bdb;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity 
public class Product {
	@PrimaryKey 
	private long id ;
	private String name;
	private long amt;
	
	@Override
	public String toString() {
		return "id:"+id+"name:"+name+"atm:"+amt;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getAmt() {
		return amt;
	}
	public void setAmt(long amt) {
		this.amt = amt;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	

}
