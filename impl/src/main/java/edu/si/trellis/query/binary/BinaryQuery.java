package edu.si.trellis.query.binary;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Session;

import edu.si.trellis.query.CassandraQuery;

abstract class BinaryQuery extends CassandraQuery {

    BinaryQuery(Session session, String queryString, ConsistencyLevel consistency) {
        super(session, queryString, consistency);
    }
    
    static final String BINARY_TABLENAME = "binarydata";
}
