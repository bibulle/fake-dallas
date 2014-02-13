package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.avaje.ebean.Expr;

import play.Logger;
import play.db.ebean.Model;

@Entity
public class Test extends Model {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3027879676222851916L;

	@Id
	public Long id;

	public String ip;
	
	public String name = "";
	
	public Date updateDate = new Date();
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("updateDate DESC")
	public List<Request> requests;

	@Override
	public void save() {
	
		// Only keep the last ten
		List<Test> oldTests = Test.find.where().eq("ip", ip).orderBy("updateDate desc").findList();
		while (oldTests.size() > 10) {
			oldTests.get(oldTests.size()-1).delete();
			oldTests.remove(oldTests.size()-1);
		}
		
		super.save();
	}
	
	public static Test getCurrentTest(String ip) {
		List<Test> tests = Test.find.where().eq("ip", ip).orderBy("updateDate desc").findList();
		
		if (tests.size() != 0) {
			return tests.get(0);
		} else {
			return null;
		}
	}
	
	public static List<Test> findAllOrderByDate() {
		return Test.find.orderBy("updateDate desc").findList();
	}
	
	/**
	 * finder
	 */
	public static Finder<Long, Test> find = new Finder<Long, Test>(Long.class, Test.class);

}
