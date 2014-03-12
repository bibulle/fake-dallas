package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Request extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8224208359014786679L;

	@Id
	public Long id;

	public String url;
	
	public String file = "";
	
	public String status = "";
	
	public Date updateDate = new Date();
	
	@Override
	public void save() {

		super.save();
	}
	
	/**
	 * finder
	 */
	public static Finder<Long, Request> find = new Finder<Long, Request>(Long.class, Request.class);

}
