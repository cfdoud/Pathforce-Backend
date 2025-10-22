package com.pathdx.repository.reposervice;

import com.pathdx.constant.AuditLogSort;
import com.pathdx.dto.responseDto.AuditLogRespDto;
import com.pathdx.model.*;
import com.pathdx.repository.AuditLogsRepository;
import com.pathdx.repository.BaseRepoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AuditLogRepoService extends BaseRepoService<AuditLogModel,Long> {

    @Autowired
    AuditLogsRepository auditLogsRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    protected JpaRepository<AuditLogModel, Long> getRepository() {
        return auditLogsRepository;
    }

    @Override
    protected Class<AuditLogModel> getEntityClass() {
        return AuditLogModel.class;
    }

    public Page<AuditLogRespDto> getAuditLogByFilter(String labId, List<Long> userIds, Date fromDate, Date toDate, Pageable pageable) throws ParseException {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AuditLogRespDto> criteriaBuilderQuery = criteriaBuilder.createQuery(AuditLogRespDto.class);
        Root<AuditLogModel> root = criteriaBuilderQuery.from(AuditLogModel.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<AuditLogModel, UserModel> userJoin = root.join(AuditLogModel_.userModel);
        Join<AuditLogModel, ActionModel> actionJoin = root.join(AuditLogModel_.actionModel);
        Join<AuditLogModel, CaseDetails> caseJoin = null;
        Join<CaseDetails, OrderMessages> orderMessagesJoin = null;
        if(root.get("caseDetails").get("caseId")!=null) {
            caseJoin = root.join(AuditLogModel_.caseDetails,JoinType.LEFT);
            orderMessagesJoin = caseJoin.join(CaseDetails_.orderMessages, JoinType.LEFT);
        }
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(sdf.parse(sdf.format(toDate)));
            cal.add(Calendar.DATE, 1);
            Date incrementday = cal.getTime();
            predicates.add((criteriaBuilder.between(root.get("dateCreated"), fromDate,incrementday)));
        }
        Predicate labIdPredicates = criteriaBuilder.equal(root.get("labDetail").get("labid"), labId);
        Predicate nullLabIdPredicates = criteriaBuilder.isNull(root.get("labDetail").get("labid"));
        predicates.add(criteriaBuilder.or(labIdPredicates, nullLabIdPredicates));

        CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(userJoin.get("id"));
        for (Long userId : userIds) {
            inClause.value(userId);
        }
        predicates.add(inClause);
        //predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));

        CompoundSelection<AuditLogRespDto> selection = null;
        selection = criteriaBuilder.construct(AuditLogRespDto.class,
                root.get(AuditLogModel_.id),root.get(AuditLogModel_.accessionId),
                userJoin.get(UserModel_.firstName),
                userJoin.get(UserModel_.lastName),
                userJoin.get(UserModel_.email),
                orderMessagesJoin.get(OrderMessages_.caseStatus),
                actionJoin.get(ActionModel_.actionType),
                root.get(AuditLogModel_.description),
                root.get(AuditLogModel_.dateCreated));
        criteriaBuilderQuery.select(selection)
                .where(predicates.toArray(new Predicate[]{}));

        criteriaBuilderQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        List<AuditLogRespDto> results = entityManager.createQuery(criteriaBuilderQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        Long count =  getCount(labId, userIds, fromDate, toDate);
        return new PageImpl<>(results, pageable, count);
    }
    public List<AuditLogRespDto>  getAuditLogs(String labId, List<Long> userIds, Date fromDate, Date toDate) throws ParseException {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AuditLogRespDto> criteriaBuilderQuery = criteriaBuilder.createQuery(AuditLogRespDto.class);
        Root<AuditLogModel> root = criteriaBuilderQuery.from(AuditLogModel.class);
        List<Predicate> predicates = new ArrayList<>();
        Join<AuditLogModel, UserModel> userJoin = root.join(AuditLogModel_.userModel);
        Join<AuditLogModel, ActionModel> actionJoin = root.join(AuditLogModel_.actionModel);
        Join<AuditLogModel, CaseDetails> caseJoin = null;
        Join<CaseDetails, OrderMessages> orderMessagesJoin = null;
        if(root.get("caseDetails").get("caseId")!=null) {
            caseJoin = root.join(AuditLogModel_.caseDetails,JoinType.LEFT);
            orderMessagesJoin = caseJoin.join(CaseDetails_.orderMessages, JoinType.LEFT);
        }
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(sdf.parse(sdf.format(toDate)));
            cal.add(Calendar.DATE, 1);
            Date incrementday = cal.getTime();
            predicates.add((criteriaBuilder.between(root.get("dateCreated"), fromDate,incrementday)));
        }
        Predicate labIdPredicates = criteriaBuilder.equal(root.get("labDetail").get("labid"), labId);
        Predicate nullLabIdPredicates = criteriaBuilder.isNull(root.get("labDetail").get("labid"));
        predicates.add(criteriaBuilder.or(labIdPredicates, nullLabIdPredicates));

        CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(userJoin.get("id"));
        for (Long userId : userIds) {
            inClause.value(userId);
        }
        predicates.add(inClause);
//        predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
        CompoundSelection<AuditLogRespDto> selection = null;
        selection = criteriaBuilder.construct(AuditLogRespDto.class,
                root.get(AuditLogModel_.id),root.get(AuditLogModel_.accessionId),
                userJoin.get(UserModel_.firstName),
                userJoin.get(UserModel_.lastName),
                userJoin.get(UserModel_.email),
                orderMessagesJoin.get(OrderMessages_.caseStatus),
                actionJoin.get(ActionModel_.actionType),
                root.get(AuditLogModel_.description),
                root.get(AuditLogModel_.dateCreated));

        criteriaBuilderQuery.select(selection)
                .where(predicates.toArray(new Predicate[]{}));
        criteriaBuilderQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        List<AuditLogRespDto> results = entityManager.createQuery(criteriaBuilderQuery)
                .getResultList();
        return results;
    }


    public Page<AuditLogRespDto> getAuditLogByFilter(String labId, List<Long> userIds, Date fromDate, Date toDate, AuditLogSort sort, String order, Map<String, String> parameters, Pageable pageable, Optional<String> date) throws ParseException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AuditLogRespDto> criteriaBuilderQuery = criteriaBuilder.createQuery(AuditLogRespDto.class);
        Root<AuditLogModel> root = criteriaBuilderQuery.from(AuditLogModel.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<AuditLogModel, UserModel> userJoin = root.join(AuditLogModel_.userModel);
        Join<AuditLogModel, ActionModel> actionJoin = root.join(AuditLogModel_.actionModel);
        Join<AuditLogModel, CaseDetails> caseJoin = null;
        Join<CaseDetails, OrderMessages> orderMessagesJoin = null;
        if(root.get("caseDetails").get("caseId")!=null) {
            caseJoin = root.join(AuditLogModel_.caseDetails,JoinType.LEFT);
            orderMessagesJoin = caseJoin.join(CaseDetails_.orderMessages, JoinType.LEFT);
        }
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(sdf.parse(sdf.format(toDate)));
            cal.add(Calendar.DATE, 1);
            Date incrementday = cal.getTime();
            predicates.add((criteriaBuilder.between(root.get("dateCreated"), fromDate,incrementday)));
        }
        Predicate labIdPredicates = criteriaBuilder.equal(root.get("labDetail").get("labid"), labId);
        Predicate nullLabIdPredicates = criteriaBuilder.isNull(root.get("labDetail").get("labid"));
        predicates.add(criteriaBuilder.or(labIdPredicates, nullLabIdPredicates));

        CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(userJoin.get("id"));
        for (Long userId : userIds) {
            inClause.value(userId);
        }
        //predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));

        predicates.add(inClause);

        CompoundSelection<AuditLogRespDto> selection = null;
        selection = criteriaBuilder.construct(AuditLogRespDto.class,
                root.get(AuditLogModel_.id),root.get(AuditLogModel_.accessionId),
                userJoin.get(UserModel_.firstName),
                userJoin.get(UserModel_.lastName),
                userJoin.get(UserModel_.email),
                orderMessagesJoin.get(OrderMessages_.caseStatus),
                actionJoin.get(ActionModel_.actionType),
                root.get(AuditLogModel_.description),
                root.get(AuditLogModel_.dateCreated));
        filter(parameters, criteriaBuilder, root, userJoin, actionJoin, orderMessagesJoin, predicates, date);
        criteriaBuilderQuery.select(selection)
                .where(predicates.toArray(new Predicate[]{}));

        if(sort.equals(AuditLogSort.NAME) ){
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    userJoin.get(UserModel_.firstName)));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    userJoin.get(UserModel_.firstName)));
        } else if (sort.equals(AuditLogSort.EMAILID)) {
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    userJoin.get(UserModel_.email)));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    userJoin.get(UserModel_.email)));
        } else if (sort.equals(AuditLogSort.CREATEDDATE)) {
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    root.get("dateCreated")));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    root.get("dateCreated")));
        }else if (sort.equals(AuditLogSort.ACCESSIONID)) {
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    root.get("accessionId")));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    root.get("accessionId")));
        }else if (sort.equals(AuditLogSort.CASESTATUS)) {
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    orderMessagesJoin.get(OrderMessages_.caseStatus)));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    orderMessagesJoin.get(OrderMessages_.caseStatus)));
        }else if (sort.equals(AuditLogSort.ACTIONTYPE)) {
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    actionJoin.get(ActionModel_.actionType)));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    actionJoin.get(ActionModel_.actionType)));
        }else if (sort.equals(AuditLogSort.DESCRIPTION)) {
            if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                    root.get("description")));
            else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                    root.get("description")));
        }

        List<AuditLogRespDto> results = entityManager.createQuery(criteriaBuilderQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        Long count =  getCount(labId, userIds,  fromDate,  toDate, parameters, date);
        return new PageImpl<>(results, pageable, count);
    }

    private Long getCount(String labId, List<Long> userIds, Date fromDate, Date toDate,  Map<String, String> filters, Optional<String> date) throws ParseException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<AuditLogModel> root = criteriaBuilderQuery.from(AuditLogModel.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<AuditLogModel, ActionModel> actionJoin = root.join(AuditLogModel_.actionModel);
        Join<AuditLogModel, UserModel> userJoin = root.join(AuditLogModel_.userModel);

        Join<AuditLogModel, CaseDetails> caseJoin = null;
        Join<CaseDetails, OrderMessages> orderMessagesJoin = null;
        if(root.get("caseDetails").get("caseId")!=null) {
            caseJoin = root.join(AuditLogModel_.caseDetails,JoinType.LEFT);
            orderMessagesJoin = caseJoin.join(CaseDetails_.orderMessages, JoinType.LEFT);
        }

        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(sdf.parse(sdf.format(toDate)));
            cal.add(Calendar.DATE, 1);
            Date incrementday = cal.getTime();
            predicates.add((criteriaBuilder.between(root.get("dateCreated"), fromDate,incrementday)));
        }
        Predicate labIdPredicates = criteriaBuilder.equal(root.get("labDetail").get("labid"), labId);
        Predicate nullLabIdPredicates = criteriaBuilder.isNull(root.get("labDetail").get("labid"));
        predicates.add(criteriaBuilder.or(labIdPredicates, nullLabIdPredicates));

        CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(userJoin.get("id"));
        for (Long userId : userIds) {
            inClause.value(userId);
        }
        predicates.add(inClause);
        //predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
        filter(filters, criteriaBuilder, root, userJoin, actionJoin, orderMessagesJoin, predicates, date);
        criteriaBuilderQuery.select(criteriaBuilder.count(root))
                .where(predicates.toArray(new Predicate[]{}));
        return entityManager.createQuery(criteriaBuilderQuery)
                .getSingleResult();

    }

    private static void filter(Map<String, String> filters,
                               CriteriaBuilder criteriaBuilder, Root<AuditLogModel> root, Join<AuditLogModel, UserModel> userJoin,
                               Join<AuditLogModel, ActionModel> actionJoin, Join<CaseDetails, OrderMessages> orderMessagesJoin, List<Predicate> predicates, Optional<String> date) throws ParseException {
        if(filters.containsKey("accessionId"))
            predicates.add(criteriaBuilder.like(root.get("accessionId"), "%"+ filters.get("accessionId")+"%"));
        if(filters.containsKey("name"))
            predicates.add(criteriaBuilder.or((criteriaBuilder.like(userJoin.get(UserModel_.firstName), "%" + filters.get("name")+ "%")),
                    (criteriaBuilder.like(userJoin.get(UserModel_.middleName), "%" + filters.get("name") + "%")),
                    (criteriaBuilder.like(userJoin.get(UserModel_.lastName), "%" + filters.get("name") + "%"))));
        if(filters.containsKey("emailid"))
            predicates.add(criteriaBuilder.like(userJoin.get(UserModel_.email), "%"+ filters.get("emailid")+"%"));
        if(filters.containsKey("actiontype"))
            predicates.add(criteriaBuilder.like(actionJoin.get(ActionModel_.actionType), "%"+ filters.get("actiontype")+"%"));
        if(filters.containsKey("casestatus"))
            predicates.add(criteriaBuilder.like(orderMessagesJoin.get(OrderMessages_.caseStatus), "%"+ filters.get("casestatus")+"%"));
        if(filters.containsKey("description"))
            predicates.add(criteriaBuilder.like(root.get("description"),"%"+ filters.get("description")+"%"));

        if(date.isPresent()){
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(sdf.parse(date.get()));
            Date currentDay = cal.getTime();
            cal.add(Calendar.DATE, 1);
            Date incrementday = cal.getTime();
            predicates.add(criteriaBuilder.between(root.get(AuditLogModel_.dateCreated), currentDay, incrementday));
        }

    }

    private Long getCount(String labId, List<Long> userIds, Date fromDate, Date toDate) throws ParseException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<AuditLogModel> root = criteriaBuilderQuery.from(AuditLogModel.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<AuditLogModel, UserModel> userJoin = root.join(AuditLogModel_.userModel);
        Join<AuditLogModel, ActionModel> actionJoin = root.join(AuditLogModel_.actionModel);
        Join<AuditLogModel, CaseDetails> caseJoin = null;
        Join<CaseDetails, OrderMessages> orderMessagesJoin = null;
        if(root.get("caseDetails").get("caseId")!=null) {
            caseJoin = root.join(AuditLogModel_.caseDetails,JoinType.LEFT);
            orderMessagesJoin = caseJoin.join(CaseDetails_.orderMessages, JoinType.LEFT);
        }
        if(Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(sdf.parse(sdf.format(toDate)));
            cal.add(Calendar.DATE, 1);
            Date incrementday = cal.getTime();
            predicates.add((criteriaBuilder.between(root.get("dateCreated"), fromDate,incrementday)));
        }
        Predicate labIdPredicates = criteriaBuilder.equal(root.get("labDetail").get("labid"), labId);
        Predicate nullLabIdPredicates = criteriaBuilder.isNull(root.get("labDetail").get("labid"));
        predicates.add(criteriaBuilder.or(labIdPredicates, nullLabIdPredicates));

        CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(userJoin.get("id"));
        for (Long userId : userIds) {
            inClause.value(userId);
        }
        predicates.add(inClause);
        //predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));

        criteriaBuilderQuery.select(criteriaBuilder.count(root))
                .where(predicates.toArray(new Predicate[]{}));
        criteriaBuilderQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        return entityManager.createQuery(criteriaBuilderQuery)
                .getSingleResult();
    }

    public Optional<LabDetail> getLabDetails(String labId) {
        LabDetail labDetail = new LabDetail();
        try {
            TypedQuery<LabDetail> tq = entityManager.createQuery("SELECT a FROM LabDetail a where a.labid=:labId", LabDetail.class);
            tq.setParameter("labId",labId);
            labDetail = tq.getSingleResult();
        }catch (Exception e){
        }
        Optional<LabDetail> labDetailOptional = Optional.ofNullable(labDetail);
        return labDetailOptional;
    }
}
