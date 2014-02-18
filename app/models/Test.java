package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.avaje.ebean.Expr;

import com.avaje.ebean.ExpressionList;
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
		List<Test> oldTests = Test.find.orderBy("updateDate desc").findList();
		while (oldTests.size() > 20) {
			oldTests.get(oldTests.size()-1).delete();
			oldTests.remove(oldTests.size()-1);
		}
		
		super.save();
	}
	
	public static Test getCurrentTest(String ip) {
		List<Test> tests = Test.find.where().or(Expr.eq("ip", ip), Expr.isNull("ip")).orderBy("updateDate desc").findList();

        Logger.debug("----------------");
        for (Test test : tests) {
            Logger.debug(test.ip+" "+test.updateDate);
        }
        Logger.debug("----------------");

		if (tests.size() != 0) {
            if (tests.get(0).ip == null) {
                tests.get(0).ip = ip;
                tests.get(0).update();
            }

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
