package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.SqlUpdate;
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

    @Version
    public Timestamp lastUpdate;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderBy("updateDate DESC")
    public List<Request> requests;

    @Override
    public void save() {

        // Only keep the last ten
        List<Test> oldTests = Test.find.orderBy("updateDate desc").findList();
        while (oldTests.size() > 20) {
            oldTests.get(oldTests.size() - 1).delete();
            oldTests.remove(oldTests.size() - 1);
        }

        super.save();
    }

    public static Test getCurrentTest(String ip) {
        List<Test> tests = Test.find.where().or(Expr.eq("ip", ip), Expr.isNull("ip")).orderBy("updateDate desc").findList();

        Logger.debug("----------------");
        for (Test test : tests) {
            Logger.debug(test.ip + " " + test.updateDate);
        }
        Logger.debug("----------------");

        if (tests.size() != 0) {
            if (tests.get(0).ip == null) {
                SqlUpdate update = Ebean.createSqlUpdate("UPDATE test SET ip=:ip WHERE id=:id")
                        .setParameter("ip", ip)
                        .setParameter("id", tests.get(0).id);
                int rows = update.execute();
                return getCurrentTest(ip);
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
