package com.pathdx.repository.reposervice;

import com.pathdx.dto.responseDto.RepoDto;
import com.pathdx.model.*;
import com.pathdx.repository.BaseRepoService;
import com.pathdx.repository.SlideDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlideDetailRepoService extends BaseRepoService<SlideDetails, Long> {

    @Autowired
    SlideDetailRepository slideDetailRepository;

    @Override
    protected JpaRepository<SlideDetails, Long> getRepository() {
        return slideDetailRepository;
    }

    @Autowired
    EntityManager entityManager;
    @Override
    protected Class<SlideDetails> getEntityClass() {
        return SlideDetails.class;
    }

    public  Long countbyOrderMessage(Long id){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<SlideDetails> root = criteriaBuilderQuery.from(SlideDetails.class);
        Join<SlideDetails, CaseDetails> caseDetailsJoin = root.join(SlideDetails_.caseDetails);
        Join<CaseDetails, OrderMessages> join = caseDetailsJoin.join(CaseDetails_.orderMessages);
        //List<Predicate> predicates = new ArrayList<>();
        criteriaBuilderQuery.select(criteriaBuilder.count(root))
                .where(criteriaBuilder.equal(join.get(OrderMessages_.id), id));
       return entityManager.createQuery(criteriaBuilderQuery)
                .getSingleResult();



    }
}
