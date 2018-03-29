package com.example.hiberlazy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HiberLazyApplicationTests {

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private ChildRepository childRepository;

	@Test
	@Transactional
	public void loadParentWithEagerChildren() {
		/// make sure com.example.hiberlazy.Parent.children has fetch=EAGER

		Parent parent = parentRepository.findByParentName("John");

		/* RESULT - 1st query gets the parent, 2nd children. it's not 1 query with join!!!

		* Hibernate: select parent0_.id as id1_1_, parent0_.parent_name as parent_n2_1_ from parent parent0_ where parent0_.parent_name=?
		* Hibernate: select children0_.parent_id as parent_i3_0_0_, children0_.id as id1_0_0_, children0_.id as id1_0_1_, children0_.child_name as child_na2_0_1_, children0_.parent_id as parent_i3_0_1_ from child children0_ where children0_.parent_id=?
		*
		* */
	}

	@Test
	@Transactional
	public void LoadParentSWithEagerChildren() {
		/// make sure com.example.hiberlazy.Parent.children has fetch=EAGER

		List<Parent> parents = parentRepository.findAll();

		/* RESULT
		I have 2 parents, so 1st query retrieves ALL parents and then one query is issued for each parent to
		get their children. if i have 100 parents, there's going to be 100 + 1 queries. isn't it N+1?
		it's not join strategy, it's select, see https://www.mkyong.com/hibernate/hibernate-fetching-strategies-examples/
		adding     @Fetch(FetchMode.JOIN) as described in the article has no effect

		* Hibernate: select parent0_.id as id1_1_, parent0_.parent_name as parent_n2_1_ from parent parent0_
		* Hibernate: select children0_.parent_id as parent_i3_0_0_, children0_.id as id1_0_0_, children0_.id as id1_0_1_, children0_.child_name as child_na2_0_1_, children0_.parent_id as parent_i3_0_1_ from child children0_ where children0_.parent_id=?
		* Hibernate: select children0_.parent_id as parent_i3_0_0_, children0_.id as id1_0_0_, children0_.id as id1_0_1_, children0_.child_name as child_na2_0_1_, children0_.parent_id as parent_i3_0_1_ from child children0_ where children0_.parent_id=?
		* */
	}

}
