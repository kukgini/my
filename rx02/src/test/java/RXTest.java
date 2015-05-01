import rx.Observable;
import rx.functions.Func1;

import java.sql.*;

/**
 * Created by soomin on 2015. 4. 7..
 */
public class RXTest {

    public static void main(String[] args) {

        Observable.just(db_file_name_prefix)
        .flatMap(new Func1<String, Observable<Connection>>() {
            @Override
            public Observable<Connection> call(String s) {
                Class.forName("org.hsqldb.jdbcDriver");
                Connection conn = DriverManager.getConnection("jdbc:hsqldb:" + s, "sa", "");
                return Observable.just(conn);
            }
        }).flatMap(new Func1<Connection, Observable<Statement>>() {
            @Override
            public Observable<Statement> call(Connection conn) {
                try {
                    return Observable.just(conn.createStatement());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).flatMap(new Func1<Statement, Observable<ResultSet>>() {
            @Override
            public Observable<ResultSet> call(Statement stmt) {
                try {
                    return Observable.just(stmt.executeQuery("SELECT * FROM tab"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        unique values for new rows- useful for row keys
            db.update(
                "CREATE TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");
        } catch (SQLException ex2) {

            //ignore
            //ex2.printStackTrace();  // second time we run program
            //  should throw execption since table
            // already there
            //
            // this will have no effect on the db
        }

        try {

            // add some rows - will create duplicates if run more then once
            // the id column is automatically generated
            db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('Ford', 100)");
            db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('Toyota', 200)");
            db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('Honda', 300)");
            db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('GM', 400)");

            // do a query
            db.query("SELECT * FROM sample_table WHERE num_col < 250");

            // at end of program
            db.shutdown();
        } catch (SQLException ex3) {
            ex3.printStackTrace();
        }
    }


}
