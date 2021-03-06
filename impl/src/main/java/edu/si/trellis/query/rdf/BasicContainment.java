package edu.si.trellis.query.rdf;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import edu.si.trellis.MutableReadConsistency;

import javax.inject.Inject;

import org.apache.commons.rdf.api.IRI;

/**
 * A query to retrieve basic containment information from a materialized view or index table.
 */
public class BasicContainment extends ResourceQuery {

    @Inject
    public BasicContainment(Session session, @MutableReadConsistency ConsistencyLevel consistency) {
        super(session, "SELECT identifier AS contained FROM " + BASIC_CONTAINMENT_TABLENAME
                        + " WHERE container = :container ;", consistency);
    }

    /**
     * @param id the {@link IRI} of the container
     * @return a {@link ResultSet} of the resources contained in {@code id}
     */
    public ResultSet execute(IRI id) {
        return executeSyncRead(preparedStatement().bind().set("container", id, IRI.class));
    }
}
