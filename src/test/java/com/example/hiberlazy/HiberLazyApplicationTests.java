package com.example.hiberlazy;

import org.junit.Assert;
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

        Parent parent = parentRepository.findByName("John");

        /* RESULT - 1st query gets the parent, 2nd children. it's not 1 query with join!!!

         * Hibernate: select parent0_.id as id1_1_, parent0_.parent_name as parent_n2_1_ from parent parent0_ where parent0_.parent_name=?
         * Hibernate: select children0_.parent_id as parent_i3_0_0_, children0_.id as id1_0_0_, children0_.id as id1_0_1_, children0_.child_name as child_na2_0_1_, children0_.parent_id as parent_i3_0_1_ from child children0_ where children0_.parent_id=?
         *
         * */
    }

    @Test
    @Transactional
    public void loadParentWithEagerChildrenFetchJoin() {
        /// make sure com.example.hiberlazy.Parent.children has fetch=EAGER

        Parent parent = parentRepository.findByNameFetchJoin("John");

        Assert.assertNotNull(parent);

        /* RESULT - One query with join!!! Awesome)

         *Hibernate:
            select
                parent0_.id as id1_1_0_,
                children1_.id as id1_0_1_,
                parent0_.name as name2_1_0_,
                children1_.child_name as child_na2_0_1_,
                children1_.parent_id as parent_i3_0_1_,
                children1_.parent_id as parent_i3_0_0__,
                children1_.id as id1_0_0__
            from
                parent parent0_
            inner join
                child children1_
                    on parent0_.id=children1_.parent_id
            where
                parent0_.name=?
         * */
    }

    @Test
    @Transactional
    public void LoadAllParentsWithEagerChildren() {
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

    @Test
    @Transactional
    public void LoadAllParentsWithEagerChildrenFetchJoin() {
        /// make sure com.example.hiberlazy.Parent.children has fetch=EAGER

        List<Parent> parents = parentRepository.findAllFetchJoin();

        Assert.assertTrue(parents != null && !parents.isEmpty());

		/* RESULT - Single query with join!
            Hibernate:
                select
                    parent0_.id as id1_1_0_,
                    children1_.id as id1_0_1_,
                    parent0_.name as name2_1_0_,
                    children1_.child_name as child_na2_0_1_,
                    children1_.parent_id as parent_i3_0_1_,
                    children1_.parent_id as parent_i3_0_0__,
                    children1_.id as id1_0_0__
                from
                    parent parent0_
                inner join
                    child children1_
                        on parent0_.id=children1_.parent_id

		* */
    }

    @Test
    @Transactional
    public void LoadParentByIdWithEagerChildren() {
        /// make sure com.example.hiberlazy.Parent.children has fetch=EAGER

        for (long i = 1; i <= 100; i++) {
            Parent parent = parentRepository.findById(i).orElse(null);

            if (parent != null)
                break;

        }

        /* RESULT
        * Hibernate:
            select
                parent0_.id as id1_1_0_,
                parent0_.parent_name as parent_n2_1_0_,
                children1_.parent_id as parent_i3_0_1_,
                children1_.id as id1_0_1_,
                children1_.id as id1_0_2_,
                children1_.child_name as child_na2_0_2_,
                children1_.parent_id as parent_i3_0_2_
            from
                parent parent0_
            left outer join
                child children1_
                    on parent0_.id=children1_.parent_id
            where
                parent0_.id=?
        * */

		/*
		* В твоем же примере на GitHub, мы видим Repo.findByName(name).
            Такой подход будет всегда приводить к двум запросам:
            1. select parent by name (вытянет Parent объект непрофетченный, без джойнов).
            2. select childrens by parent ID (вытянет всех children-ов этого parent, по ID полученному в первом select).
            Если воспользоваться Repo.findOne(ID) - то запрос будет один, с джойном.

            А для случая Repo.findAll() - да, жадный фетчинг будет работать как ты и описал:
            1. Сначала всех parent (непрофетченных, без джойнов).
            2. По одному запросу на каждый парент c джойном children-ов, по списку ID полученных в первом запросе.

            Для случая жадного фетчинга из коробки - это типичное поведение.
            Для того, чтобы получить один запрос, есть такие варианты:
            1. Как и писал Сергей - join fetch, в HQL (в твоем примере это легко проверить, через @Query).
            2. Criteria API (лично я не проверял, так-как не люблю этот метод).

            О проблеме n+1 можно почитать в книге "Hibernate in Action", там этому вопросу посвящена отдельная глава.
		* */
    }

}
