package net.pis.repository.custom;

/**
 * Query DSL은 기본적으로 JPA를 상속하고 있다.
 * Query DSL사용시 사용할 클래스
 */
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.pis.orm.domain.Contract;
import net.pis.orm.domain.QContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class DemoCustomRepositoryImpl implements DemoCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Contract> getList(Map<String, Object> paramMap) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QContract qContract = QContract.contract;

        List<Contract> list = new ArrayList<Contract>();

        list =
            queryFactory
            .select(qContract)
            .from(qContract)
            .fetchResults()
            .getResults();

        return list;
    }
}
