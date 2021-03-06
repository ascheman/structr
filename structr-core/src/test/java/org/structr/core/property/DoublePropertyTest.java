/**
 * Copyright (C) 2010-2014 Morgner UG (haftungsbeschränkt)
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.property;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import org.structr.common.StructrTest;
import org.structr.common.error.FrameworkException;
import org.structr.core.Result;
import org.structr.core.entity.TestFour;
import org.structr.core.entity.TestOne;
import org.structr.core.entity.OneFourOneToOne;

/**
 *
 * @author Christian Morgner
 */
public class DoublePropertyTest extends StructrTest {
	
	public void test() {
		
		try {
			final Property<Double> instance = TestFour.doubleProperty;
			final TestFour testEntity        = createTestNode(TestFour.class);
			
			assertNotNull(testEntity);

			// store double in the test entitiy
			final Double value = 3.141592653589793238;

			try {
				app.beginTx();
				instance.setProperty(securityContext, testEntity, value);
				app.commitTx();

			} finally {

				app.finishTx();
			}

			// check value from database
			assertEquals(value, instance.getProperty(securityContext, testEntity, true));
			
		} catch (FrameworkException fex) {
			
			fail("Unable to store array");
		}
	}
	
	public void testSimpleSearchOnNode() {
		
		try {
			final PropertyMap properties  = new PropertyMap();
			final PropertyKey<Double> key = TestFour.doubleProperty;
			
			properties.put(key, 3.141592653589793238);
			
			final TestFour testEntity     = createTestNode(TestFour.class, properties);
			
			assertNotNull(testEntity);

			// check value from database
			assertEquals(3.141592653589793238, testEntity.getProperty(key));
			
			Result<TestFour> result = app.nodeQuery(TestFour.class).and(key, 3.141592653589793238).getResult();
			
			assertEquals(1, result.size());
			assertEquals(testEntity, result.get(0));
		
		} catch (FrameworkException fex) {
			
			fail("Unable to store array");
		}
		
	}
	
	public void testSimpleSearchOnRelationship() {
		
		try {
			final TestOne testOne        = createTestNode(TestOne.class);
			final TestFour testFour      = createTestNode(TestFour.class);
			final Property<Double> key = OneFourOneToOne.doubleProperty;
			
			assertNotNull(testOne);
			assertNotNull(testFour);
			
			final OneFourOneToOne testEntity = createTestRelationship(testOne, testFour, OneFourOneToOne.class);
			
			assertNotNull(testEntity);

			try {
				app.beginTx();
				testEntity.setProperty(key, 3.141592653589793238);
				app.commitTx();

			} finally {

				app.finishTx();
			}
			
			// check value from database
			assertEquals(3.141592653589793238, testEntity.getProperty(key));
			
			Result<OneFourOneToOne> result = app.relationshipQuery(OneFourOneToOne.class).and(key, 3.141592653589793238).getResult();
			
			assertEquals(1, result.size());
			assertEquals(testEntity, result.get(0));
		
		} catch (FrameworkException fex) {
			
			fail("Unable to store array");
		}
	}
}
