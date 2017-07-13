package org.piaohao.act.jfinal.db;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import org.osgl.exception.UnexpectedException;
import org.osgl.mvc.annotation.After;
import org.osgl.mvc.annotation.Before;
import org.osgl.mvc.annotation.Catch;
import org.osgl.mvc.annotation.Finally;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * An injector support Transaction
 */
@Singleton
public class JFinalTransactional {

    protected int getTransactionLevel(Config config) {
        return config.getTransactionLevel();
    }

    @Before
    public void start() {
        Config config = DbKit.getConfig();
        Connection conn = null;
        try {
            conn = config.getConnection();
            config.setThreadLocalConnection(conn);
            conn.setTransactionIsolation(getTransactionLevel(config));    // conn.setTransactionIsolation(transactionLevel);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            //ignore
        }
    }

    @After
    public void commit() {
        Config config = DbKit.getConfig();
        Connection conn = null;
        Boolean autoCommit = null;
        try {
            conn = config.getConnection();
            autoCommit = conn.getAutoCommit();
            config.setThreadLocalConnection(conn);
            conn.setTransactionIsolation(getTransactionLevel(config));    // conn.setTransactionIsolation(transactionLevel);
            conn.setAutoCommit(false);
            conn.commit();
        } catch (SQLException e) {
            rollback();
        } finally {
            try {
                if (conn != null) {
                    if (autoCommit != null)
                        conn.setAutoCommit(autoCommit);
                    conn.close();
                }
            } catch (Throwable t) {
                LogKit.error(t.getMessage(), t);    // can not throw exception here, otherwise the more important exception in previous catch block can not be thrown
            } finally {
                config.removeThreadLocalConnection();    // prevent memory leak
            }
        }
    }

    @Catch(Exception.class)
    public void rollback() {
        try {
            Config config = DbKit.getConfig();
            Connection conn = config.getConnection();
            conn.rollback();
        } catch (SQLException e) {
            throw new UnexpectedException(e);
        }
    }

    @Finally
    public void clear() {
    }

}
