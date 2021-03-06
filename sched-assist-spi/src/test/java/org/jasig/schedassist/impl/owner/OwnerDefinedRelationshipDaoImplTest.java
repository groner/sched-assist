/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.schedassist.impl.owner;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleVisitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Nicholas Blair
 * @version $Id: OwnerDefinedRelationshipDaoImplTest.java $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:database-test.xml"})
public class OwnerDefinedRelationshipDaoImplTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private OwnerDefinedRelationshipDaoImpl relationshipDao;
	@Autowired
	private OwnerDao ownerDao;
	@Autowired
	private MockCalendarAccountDao calendarAccountDao;
	private IScheduleOwner[] sampleOwners = new IScheduleOwner[2];
	
	/**
	 * 
	 */
	@Test
	public void testGetIdentifyingAttributeControl() {
		MockCalendarAccount account = new MockCalendarAccount();
		account.setAttributeValue(relationshipDao.getIdentifyingAttributeName(), "someone@somewhere.com");
		String id = relationshipDao.getIdentifyingAttribute(account);
		Assert.assertEquals("someone@somewhere.com", id);
	}
	/**
	 * 
	 */
	@Test
	public void testGetIdentifyingAttributeNotSet() {
		MockCalendarAccount account = new MockCalendarAccount();
		try {
			relationshipDao.getIdentifyingAttribute(account);
			Assert.fail("expected IllegalStateException for account missing identifying attribute");
		} catch (IllegalStateException e) {
			// expected
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testRetrieveRelationshipEmpty() {
		// make a visitor out of a different owner, so we can be sure the mock calendar account dao can return it
		MockScheduleVisitor visitor = new MockScheduleVisitor(sampleOwners[1].getCalendarAccount());
		Assert.assertEquals(0, relationshipDao.forVisitor(visitor).size());
		Assert.assertEquals(0, relationshipDao.forOwner(sampleOwners[1]).size());
	}
	/**
	 * 
	 */
	@Test
	public void testCreateRelationship() {
		// make a visitor out of a different owner, so we can be sure the mock calendar account dao can return it
		MockScheduleVisitor visitor = new MockScheduleVisitor(sampleOwners[1].getCalendarAccount());
		
		relationshipDao.createRelationship(sampleOwners[0], visitor, "testCreateRelationship");
		
		List<Relationship> forVisitor = relationshipDao.forVisitor(visitor);
		Assert.assertEquals(1, forVisitor.size());
		Assert.assertEquals("email0@domain.com", forVisitor.get(0).getOwner().getCalendarAccount().getEmailAddress());
		
		List<Relationship> forOwner = relationshipDao.forOwner(sampleOwners[0]);
		Assert.assertEquals(1, forOwner.size());
		Assert.assertEquals("email1@domain.com", forOwner.get(0).getVisitor().getCalendarAccount().getEmailAddress());
	}
	
	/**
	 * 
	 */
	@Test
	public void testUpdateRelationship() {
		// make a visitor out of a different owner, so we can be sure the mock calendar account dao can return it
		MockScheduleVisitor visitor = new MockScheduleVisitor(sampleOwners[1].getCalendarAccount());
		
		relationshipDao.createRelationship(sampleOwners[0], visitor, "testUpdateRelationship");
		
		List<Relationship> forVisitor = relationshipDao.forVisitor(visitor);
		Assert.assertEquals(1, forVisitor.size());
		Assert.assertEquals("email0@domain.com", forVisitor.get(0).getOwner().getCalendarAccount().getEmailAddress());
		
		List<Relationship> forOwner = relationshipDao.forOwner(sampleOwners[0]);
		Assert.assertEquals(1, forOwner.size());
		Assert.assertEquals("email1@domain.com", forOwner.get(0).getVisitor().getCalendarAccount().getEmailAddress());
		
		relationshipDao.createRelationship(sampleOwners[0], visitor, "testUpdateRelationship-updated");
		forVisitor = relationshipDao.forVisitor(visitor);
		Assert.assertEquals(1, forVisitor.size());
		Assert.assertEquals("testUpdateRelationship-updated", forVisitor.get(0).getDescription());
		Assert.assertEquals("email0@domain.com", forVisitor.get(0).getOwner().getCalendarAccount().getEmailAddress());
		
		forOwner = relationshipDao.forOwner(sampleOwners[0]);
		Assert.assertEquals(1, forOwner.size());
		Assert.assertEquals("testUpdateRelationship-updated", forOwner.get(0).getDescription());
		Assert.assertEquals("email1@domain.com", forOwner.get(0).getVisitor().getCalendarAccount().getEmailAddress());
	}
	
	/**
	 * 
	 */
	@Test
	public void testDeleteRelationship() {
		// make a visitor out of a different owner, so we can be sure the mock calendar account dao can return it
		MockScheduleVisitor visitor = new MockScheduleVisitor(sampleOwners[1].getCalendarAccount());
		
		relationshipDao.createRelationship(sampleOwners[0], visitor, "testCreateRelationship");
		
		List<Relationship> forVisitor = relationshipDao.forVisitor(visitor);
		Assert.assertEquals(1, forVisitor.size());
		Assert.assertEquals("email0@domain.com", forVisitor.get(0).getOwner().getCalendarAccount().getEmailAddress());
		
		List<Relationship> forOwner = relationshipDao.forOwner(sampleOwners[0]);
		Assert.assertEquals(1, forOwner.size());
		Assert.assertEquals("email1@domain.com", forOwner.get(0).getVisitor().getCalendarAccount().getEmailAddress());
	
		relationshipDao.destroyRelationship(sampleOwners[0], visitor);
		Assert.assertEquals(0, relationshipDao.forVisitor(visitor).size());
		Assert.assertEquals(0, relationshipDao.forOwner(sampleOwners[0]).size());
	}
	
	/**
	 * Creates the database.
	 * Also pulls the {@link OwnerDao} from the configuration and registers a few 
	 * sample {@link IScheduleOwner}s.
	 * 
	 * @throws Exception
	 */
	@Before
	public void createDatabase() throws Exception {
		Resource createDdl = (Resource) this.applicationContext.getBean("createDdl");
		
		String sql = IOUtils.toString(createDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
		
		for(int i = 0; i < sampleOwners.length; i++) {
			ICalendarAccount calendarAccount = this.calendarAccountDao.getCalendarAccount("user"+i);
			sampleOwners[i] = ownerDao.register(calendarAccount);
		}
	}
	
	/**
	 * Destroy the database.
	 * 
	 * @throws Exception
	 */
	@After
	public void destroyDatabase() throws Exception {
		Resource destroyDdl = (Resource) this.applicationContext.getBean("destroyDdl");
		
		String sql = IOUtils.toString(destroyDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
	}
}
