/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.structr.rest.constraint;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.structr.common.SecurityContext;
import org.structr.core.GraphObject;
import org.structr.rest.VetoableGraphObjectListener;
import org.structr.rest.exception.IllegalPathException;
import org.structr.rest.exception.PathException;
import org.structr.rest.servlet.JsonRestServlet;
import org.structr.rest.wrapper.PropertySet;

/**
 *
 * @author Christian Morgner
 */
public class SortConstraint extends WrappingConstraint {

	private static final Logger logger = Logger.getLogger(SortConstraint.class.getName());

	private String sortOrder = null;
	private String sortKey = null;
	
	public SortConstraint(SecurityContext securityContext, String sortKey, String sortOrder) {
		this.securityContext = securityContext;
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	@Override
	public boolean checkAndConfigure(String part, HttpServletRequest request) {

		this.sortKey = request.getParameter(JsonRestServlet.REQUEST_PARAMETER_SORT_KEY);
		this.sortOrder = request.getParameter(JsonRestServlet.REQUEST_PARAMETER_SORT_ORDER);

		return sortKey != null;
	}
	
	@Override
	public List<GraphObject> doGet() throws PathException {

		if(wrappedConstraint != null) {
			
			List<GraphObject> results = wrappedConstraint.doGet();
			Comparator<GraphObject> comparator = null;

			try {
				if("desc".equals(sortOrder)) {

					comparator = new Comparator<GraphObject>() {
						@Override
						public int compare(GraphObject n1, GraphObject n2) {
							Comparable c1 = (Comparable)n1.getProperty(sortKey);
							Comparable c2 = (Comparable)n2.getProperty(sortKey);
							return(c2.compareTo(c1));
						}
					};

				} else {

					comparator = new Comparator<GraphObject>() {
						@Override
						public int compare(GraphObject n1, GraphObject n2) {
							Comparable c1 = (Comparable)n1.getProperty(sortKey);
							Comparable c2 = (Comparable)n2.getProperty(sortKey);
							return(c1.compareTo(c2));
						}
					};
				}

				if(comparator != null) {
					Collections.sort(results, comparator);
				}

			} catch(Throwable t) {

				logger.log(Level.WARNING, "Error while sorting result set with {0}", sortKey);
			}

			return results;
		}

		throw new IllegalPathException();
	}

	@Override
	public void doPost(PropertySet propertySet, List<VetoableGraphObjectListener> listeners) throws Throwable {
		if(wrappedConstraint != null) {
			wrappedConstraint.doPost(propertySet, listeners);
		}

		throw new IllegalPathException();
	}

	@Override
	public void doHead() throws Throwable {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doOptions() throws Throwable {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResourceConstraint tryCombineWith(ResourceConstraint next) {
		return null;
	}
}
