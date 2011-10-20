/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.structr.rest.constraint;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.structr.core.GraphObject;
import org.structr.core.Services;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.SuperUser;
import org.structr.core.node.CreateValidatedNodeCommand;
import org.structr.core.node.NodeAttribute;
import org.structr.core.node.StructrTransaction;
import org.structr.core.node.TransactionCommand;
import org.structr.core.node.search.SearchAttribute;
import org.structr.core.node.search.SearchNodeCommand;
import org.structr.core.node.search.SearchOperator;
import org.structr.core.node.search.TextualSearchAttribute;
import org.structr.rest.VetoableGraphObjectListener;
import org.structr.rest.exception.NoResultsException;
import org.structr.rest.exception.PathException;
import org.structr.rest.wrapper.PropertySet;

/**
 * Represents a bulk type match. A TypeConstraint will always result in a
 * list of elements when it is the last element in an URI. A TypeConstraint
 * that is not the first element in an URI will try to find a pre-defined
 * relationship between preceding and the node type (defined by
 * {@see AbstractNode#getRelationshipWith}) and follow that path.
 * 
 * @author Christian Morgner
 */
public class TypeConstraint extends SortableConstraint {

	private static final Logger logger = Logger.getLogger(TypeConstraint.class.getName());

	private String type = null;
	
	@Override
	public boolean checkAndConfigure(String part, HttpServletRequest request) {

		// todo: check if type exists etc.
		this.setType(part);

		return true;
	}

	@Override
	public List<GraphObject> doGet() throws PathException {

		List<SearchAttribute> searchAttributes = new LinkedList<SearchAttribute>();
		AbstractNode topNode = null;
		boolean includeDeleted = false;
		boolean publicOnly = false;

		if(type != null) {

			searchAttributes.add(new TextualSearchAttribute("type", type, SearchOperator.OR));

			List<GraphObject> results = (List<GraphObject>)Services.command(securityContext, SearchNodeCommand.class).execute(
				topNode,
				includeDeleted,
				publicOnly,
				searchAttributes
			);

			if(!results.isEmpty()) {
				return results;
			}
			
		} else {

			logger.log(Level.WARNING, "type was null");
		}

		throw new NoResultsException();
	}

	@Override
	public void doPost(PropertySet propertySet, List<VetoableGraphObjectListener> listeners) throws Throwable {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doHead() throws Throwable {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void doOptions() throws Throwable {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		
		this.type = type.toLowerCase();

		if(this.type.endsWith("ies")) {
			logger.log(Level.INFO, "Replacing trailing 'ies' with 'y' for type {0}", type);
			this.type = this.type.substring(0, this.type.length() - 3).concat("y");
		} else
		if(this.type.endsWith("s")) {
			logger.log(Level.INFO, "Removing trailing plural 's' from type {0}", type);
			this.type = this.type.substring(0, this.type.length() - 1);
		}
	}

	public AbstractNode createNode(PropertySet propertySet) throws Throwable {

		final List<NodeAttribute> attributes = propertySet.getAttributes();
		attributes.add(new NodeAttribute(AbstractNode.Key.type.name(), StringUtils.capitalize(type)));

		// create transaction closure
		StructrTransaction transaction = new StructrTransaction() {

			@Override
			public Object execute() throws Throwable {

				return (AbstractNode)Services.command(securityContext, CreateValidatedNodeCommand.class).execute(new SuperUser(), attributes);
			}
		};

		// execute transaction: create new node
		AbstractNode newNode = (AbstractNode)Services.command(securityContext, TransactionCommand.class).execute(transaction);
		if(newNode == null) {

			// re-throw transaction exception cause
			if(transaction.getCause() != null) {
				throw transaction.getCause();
			}
		}

		return newNode;
	}

	@Override
	public ResourceConstraint tryCombineWith(ResourceConstraint next) throws PathException {

		if(next instanceof IdConstraint)	return new TypedIdConstraint((IdConstraint)next, this); else
		if(next instanceof SearchConstraint)	return new TypedSearchConstraint(this, ((SearchConstraint)next).getSearchString());

		return super.tryCombineWith(next);
	}
}
