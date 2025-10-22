package com.pathdx.repository.reposervice;

import com.pathdx.dto.responseDto.AllocationAssignedCasesDto;
import com.pathdx.dto.responseDto.AllocationUnassignedDto;
import com.pathdx.dto.responseDto.RepoDto;
import com.pathdx.dto.responseDto.YearWiseCaseCount;
import com.pathdx.model.*;
import com.pathdx.repository.BaseRepoService;
import com.pathdx.repository.OrderMessagesRepository;
import com.pathdx.utils.CaseListingStatus;
import com.pathdx.utils.CaseStatus;
import com.pathdx.utils.DashboardSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pathdx.constant.CommonConstants.*;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Service
@Slf4j
public class OrderMessageRepoService extends BaseRepoService<OrderMessages,Long> {

    @Autowired
    OrderMessagesRepository orderMessagesRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    protected JpaRepository<OrderMessages, Long> getRepository() {
        return orderMessagesRepository;
    }

    @Override
    protected Class<OrderMessages> getEntityClass() {
        return OrderMessages.class;
    }

    public List myActivity(String labId, Optional<Long> userId, String pathdxStatus){
        String hqlQuery = null;
        if(StringUtils.equalsIgnoreCase(pathdxStatus, NEWCASES) || StringUtils.equalsIgnoreCase(pathdxStatus, CLOSED)) {
            hqlQuery = "select month(DateReported) as month, year(DateReported) as year, count(distinct accessionId) as count from "
                    + "PF_OrderMessages o";
        } else if (StringUtils.equalsIgnoreCase(pathdxStatus, INPROCESS) || StringUtils.equalsIgnoreCase(pathdxStatus, PENDING)) {
            hqlQuery = "select month(assignedDate) as month, year(assignedDate) as year, count(distinct accessionId) as count from "
                    + "PF_OrderMessages o";
        }
        String fStatus = "F";
        String status = "closed";

        if(!StringUtils.equalsIgnoreCase(pathdxStatus, NEWCASES)){
            hqlQuery = StringUtils.joinWith(SPACE, hqlQuery, " inner join user_order_messages u on o.Id=u.order_message_id");
        }
        if(userId.isEmpty() && StringUtils.equalsIgnoreCase(pathdxStatus, NEWCASES) ){
            hqlQuery = StringUtils.joinWith(SPACE, hqlQuery, " left join user_order_messages u on o.Id=u.order_message_id");
        }
        if(StringUtils.equalsIgnoreCase(pathdxStatus, CLOSED)) {
            hqlQuery = StringUtils.joinWith(SPACE, hqlQuery, "where CaseStatus in  (:status, :fstatus) and LabId = :labId");
        }else{
            hqlQuery = StringUtils.joinWith(SPACE, hqlQuery, "where CaseStatus not in  (:status, :fstatus) and LabId = :labId");
        }
        if(userId.isPresent()){
            hqlQuery = StringUtils.joinWith(SPACE, hqlQuery, "and u.user_id= :userId ");
        }
        if(StringUtils.equalsIgnoreCase(pathdxStatus, NEWCASES)){
            hqlQuery = StringUtils.joinWith(SPACE, hqlQuery, "and u.user_id is null ");
        }
        hqlQuery = StringUtils.equalsIgnoreCase(pathdxStatus, INPROCESS) ? StringUtils.joinWith(
                SPACE, hqlQuery, "and datediff(CURDATE(), o.AssignedDate)<=7 ") : hqlQuery;

        hqlQuery = StringUtils.equalsIgnoreCase(pathdxStatus, PENDING) ? StringUtils.joinWith(
                SPACE, hqlQuery,"and datediff(CURDATE(), o.AssignedDate) > 7 group by  month, year"):
                StringUtils.joinWith(SPACE,hqlQuery,"group by  month,year");

        NativeQuery nativeQuery = getSession().createSQLQuery(hqlQuery);
        nativeQuery.addScalar("count", new IntegerType());
        nativeQuery.addScalar("month", new IntegerType());
        nativeQuery.addScalar("year", new IntegerType());
        nativeQuery.setParameter("status",status);
        nativeQuery.setParameter("fstatus", fStatus);
        nativeQuery.setParameter("labId",labId);
        userId.ifPresent(aLong -> nativeQuery.setParameter("userId", aLong));
        return nativeQuery.setResultTransformer(Transformers.aliasToBean(YearWiseCaseCount.class)).list();
    }
    public Long getCaseCountByStatus(String labId, Optional<Long> userId, String caseStatus) throws ParseException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<OrderMessages> root = criteriaBuilderQuery.from(OrderMessages.class);
        Join<OrderMessages, LabDetail> labDetail = root.join(OrderMessages_.labDetail);
        Join<OrderMessages, UserModel> userModelJoinLeft =null;
        Join<OrderMessages, UserModel> userModelJoin = null;

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar c = Calendar.getInstance(timeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(timeZone);
        c.setTime(sdf.parse(sdf.format(c.getTime())));
        c.add(Calendar.DAY_OF_YEAR, -7);
        Date agingDate = c.getTime();

        List<String> status = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
        Path<String> casestatus = root.get(OrderMessages_.caseStatus);
        Predicate predicate = casestatus.in(status);
        if(!StringUtils.equalsIgnoreCase(caseStatus, NEWCASES)){
             userModelJoin = root.join(OrderMessages_.userModels,JoinType.INNER);
        }else{

        }


        if(userId.isPresent()) {
           predicates.add(criteriaBuilder.equal(userModelJoin.get("id"), userId.get()));
        }

        if(StringUtils.equalsIgnoreCase(caseStatus, NEWCASES) && userId.isEmpty()){
            userModelJoinLeft  = root.join(OrderMessages_.userModels,JoinType.LEFT);
            predicates.add(criteriaBuilder.isNull(userModelJoinLeft));
        } else if (!StringUtils.equalsIgnoreCase(caseStatus, NEWCASES)  && userId.isEmpty()) {
            predicates.add(criteriaBuilder.isNotNull(userModelJoin));
        }
        if(StringUtils.equalsIgnoreCase(caseStatus, NEWCASES) && userId.isEmpty()){
            predicates.add(criteriaBuilder.not(predicate));
        } else if (StringUtils.equalsIgnoreCase(caseStatus, INPROCESS)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderMessages_.assignedDate), agingDate));
            predicates.add(criteriaBuilder.not(predicate));

        } else if (StringUtils.equalsIgnoreCase(caseStatus, PENDING)) {
            predicates.add(criteriaBuilder.lessThan(root.get(OrderMessages_.assignedDate), agingDate));
            predicates.add(criteriaBuilder.not(predicate));

        }else if (StringUtils.equalsIgnoreCase(caseStatus, CLOSED)){
            predicates.add(criteriaBuilder.and(predicate));

        }
        predicates.add(criteriaBuilder.equal(labDetail.get(LabDetail_.labid), labId));

        criteriaBuilderQuery.select(criteriaBuilder.countDistinct(root.get(OrderMessages_.accessionId)))
                .where(predicates.toArray(new Predicate[]{}));


        return entityManager.createQuery(criteriaBuilderQuery).getSingleResult();

        }


   public Page<RepoDto> getCaseByStatus(CaseStatus status, String labId, Long userId, String role, Pageable pageable,
                                        DashboardSort sort, String order, Map<String, String> filters,
                                        Optional<String> date, Optional<Integer> age) throws ParseException {
       CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
       CriteriaQuery<RepoDto> criteriaBuilderQuery = criteriaBuilder.createQuery(RepoDto.class);
       Root<OrderMessages> root = criteriaBuilderQuery.from(OrderMessages.class);
       List<Predicate> predicates = new ArrayList<>();

       Join<OrderMessages, LabDetail> labDetail = root.join(OrderMessages_.labDetail);
       Join<OrderMessages, Patients> patientsJoin = root.join(OrderMessages_.patients);
       Join<OrderMessages, Physicians> physiciansJoin = root.join(OrderMessages_.physicians);
       Join<OrderMessages, UserModel> userModelJoinLeft =null;
       Join<OrderMessages, UserModel> userModelJoin = null;
       if(!status.equals(CaseStatus.NEWCASES)){
           userModelJoin = root.join(OrderMessages_.userModels,JoinType.INNER);
       }else{
           userModelJoinLeft = root.join(OrderMessages_.userModels,JoinType.LEFT);
       }
       if(StringUtils.equalsIgnoreCase(role, LAB_REVIEWER)){
            predicates.add(criteriaBuilder.equal(userModelJoin.get("id"), userId));}

       if(!StringUtils.equalsIgnoreCase(role, LAB_REVIEWER) && !status.equals(CaseStatus.NEWCASES)){
           predicates.add(criteriaBuilder.isNotNull(userModelJoin));
       }

       List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
       Path<String> casestatus = root.get(OrderMessages_.caseStatus);
       Predicate predicate = casestatus.in(closedStatus);

       TimeZone timeZone = TimeZone.getTimeZone("UTC");
       Calendar c = Calendar.getInstance(timeZone);
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       sdf.setTimeZone(timeZone);
       c.setTime(sdf.parse(sdf.format(c.getTime())));

       c.add(Calendar.DAY_OF_YEAR, -7);
       Date agingDate = c.getTime();
       switch(status){
           case NEWCASES:
               if(StringUtils.equalsIgnoreCase(role, LAB_ADMIN) || StringUtils.equalsIgnoreCase(role, SUPER_ADMIN)){
                   predicates.add(criteriaBuilder.isNull(userModelJoinLeft));
                   predicates.add(criteriaBuilder.not(predicate));
               } else if (StringUtils.equalsIgnoreCase(role, LAB_REVIEWER)) {
                   return null;
               }
               break;
           case INPROCESSCASES:
               if(date.isEmpty())
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get(OrderMessages_.assignedDate), agingDate));
               predicates.add(criteriaBuilder.not(predicate));
                break;
           case PENDINGCASES:
               if(date.isEmpty())
               predicates.add(criteriaBuilder.lessThan(root.<Date>get(OrderMessages_.assignedDate), agingDate));
               predicates.add(criteriaBuilder.not(predicate));
               break;
           case CLOSEDCASES:
               predicates.add(criteriaBuilder.and(predicate));
       }

       predicates.add(criteriaBuilder.equal(labDetail.get(LabDetail_.labid), labId));
       CompoundSelection<RepoDto> selection = null;
        if(status.equals(CaseStatus.NEWCASES) || status.equals(CaseStatus.CLOSEDCASES)){
        selection = criteriaBuilder.construct(RepoDto.class,
               root.get(OrderMessages_.id),root.get(OrderMessages_.accessionId),
               root.get(OrderMessages_.hospital), patientsJoin.get(Patients_.firstName),
               patientsJoin.get(Patients_.middleName), patientsJoin.get(Patients_.lastName),
               physiciansJoin.get(Physicians_.FirstName), physiciansJoin.get(Physicians_.middleName),
               physiciansJoin.get(Physicians_.lastName),root.get(OrderMessages_.assignedDate),
               root.get(OrderMessages_.dateReported));
            }else{
        selection = criteriaBuilder.construct(RepoDto.class,
                    root.get(OrderMessages_.id),root.get(OrderMessages_.accessionId),
                    root.get(OrderMessages_.hospital), patientsJoin.get(Patients_.firstName),
                    patientsJoin.get(Patients_.middleName), patientsJoin.get(Patients_.lastName),
                    physiciansJoin.get(Physicians_.FirstName), physiciansJoin.get(Physicians_.middleName),
                    physiciansJoin.get(Physicians_.lastName),root.get(OrderMessages_.assignedDate),
                    root.get(OrderMessages_.assignedDate));
        }

       filter(labId, filters, date, age, criteriaBuilder, root, predicates, labDetail, patientsJoin, physiciansJoin, status);

       criteriaBuilderQuery.select(selection)
                            .where(predicates.toArray(new Predicate[]{}));
       criteriaBuilderQuery.distinct(true);

       if(sort.equals(DashboardSort.PATIENTNAME) ){
           if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                   patientsJoin.get(Patients_.firstName)));
           else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                   patientsJoin.get(Patients_.firstName)));
       } else if (sort.equals(DashboardSort.PHYSICIANNAME)) {
           if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                   physiciansJoin.get(Physicians_.FirstName)));
           else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                   physiciansJoin.get(Physicians_.FirstName)));
       } else if (sort.equals(DashboardSort.CREATEDDATE)) {
           if(status.equals(CaseStatus.NEWCASES) || status.equals(CaseStatus.CLOSEDCASES)){
               if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                       root.get(OrderMessages_.dateReported)));
               else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                       root.get(OrderMessages_.dateReported)));}
           else{
               if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                       root.get(OrderMessages_.assignedDate)));
               else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                       root.get(OrderMessages_.assignedDate)));
           }
       }else if (sort.equals(DashboardSort.ACCESSIONID)) {
           if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                   root.get(OrderMessages_.accessionId)));
           else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                   root.get(OrderMessages_.accessionId)));
       }else if (sort.equals(DashboardSort.HOSPITAL)) {
           if (StringUtils.equalsIgnoreCase(order, "desc")) criteriaBuilderQuery.orderBy(criteriaBuilder.desc(
                   root.get(OrderMessages_.hospital)));
           else criteriaBuilderQuery.orderBy(criteriaBuilder.asc(
                   root.get(OrderMessages_.hospital)));
       }

       List<RepoDto> results = entityManager.createQuery(criteriaBuilderQuery)
               .setFirstResult((int) pageable.getOffset())
               .setMaxResults(pageable.getPageSize())
               .getResultList();
       Long count =  getCount( status,  labId,  userId,  role, filters, date,  age);
       return new PageImpl<>(results, pageable, count);
           }

    private static void filter(String labId, Map<String, String> filters, Optional<String> date, Optional<Integer> age,
                               CriteriaBuilder criteriaBuilder, Root<OrderMessages> root, List<Predicate> predicates,
                               Join<OrderMessages, LabDetail> labDetail, Join<OrderMessages, Patients> patientsJoin,
                               Join<OrderMessages, Physicians> physiciansJoin, CaseStatus status) throws ParseException {
        if(filters.containsKey("accessionid"))
            predicates.add(criteriaBuilder.like(root.get(OrderMessages_.accessionId), "%"+ filters.get("accessionid")+"%"));
        if(filters.containsKey("hospital"))
            predicates.add(criteriaBuilder.like(root.get(OrderMessages_.hospital), "%"+ filters.get("hospital")+"%"));
        predicates.add(criteriaBuilder.equal(labDetail.get(LabDetail_.labid), labId));
        if(filters.containsKey("patientname")) {
            predicates.add(criteriaBuilder.or((criteriaBuilder.like(patientsJoin.get(Patients_.firstName), "%" + filters.get("patientname")+ "%")),
                    (criteriaBuilder.like(patientsJoin.get(Patients_.middleName), "%" + filters.get("patientname") + "%")),
                    (criteriaBuilder.like(patientsJoin.get(Patients_.lastName), "%" + filters.get("patientname") + "%"))));
        }
        if(filters.containsKey("physicianname")) {
            predicates.add(criteriaBuilder.or((criteriaBuilder.like(physiciansJoin.get(Physicians_.FirstName), "%" + filters.get("physicianname")+ "%")),
                    (criteriaBuilder.like(physiciansJoin.get(Physicians_.middleName), "%" + filters.get("physicianname") + "%")),
                    (criteriaBuilder.like(physiciansJoin.get(Physicians_.lastName), "%" + filters.get("physicianname") + "%"))));

        }
        if(age.isPresent()){
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            cal.setTime(sdf.parse(sdf.format(cal.getTime())));
            cal.add(Calendar.DAY_OF_YEAR, -age.get());
            Date ag = cal.getTime();
            cal.add(Calendar.DATE, 1);
            Date ageplus = cal.getTime();
            predicates.add(criteriaBuilder.between(root.<Date>get(OrderMessages_.assignedDate), ag, ageplus));
        }

      if(date.isPresent()){
          TimeZone timeZone = TimeZone.getTimeZone("UTC");
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          sdf.setTimeZone(timeZone);
          Calendar cal = Calendar.getInstance(timeZone);
          cal.setTime(sdf.parse(date.get()));
          Date currentDay = cal.getTime();
          cal.add(Calendar.DATE, 1);
          Date incrementday = cal.getTime();
          if(status.equals(CaseStatus.NEWCASES) || status.equals(CaseStatus.CLOSEDCASES))
          predicates.add(criteriaBuilder.between(root.get(OrderMessages_.dateReported), currentDay, incrementday));
          else
              predicates.add(criteriaBuilder.between(root.get(OrderMessages_.assignedDate), currentDay, incrementday));

      }

    }

    private Long getCount(CaseStatus status, String labId, Long userId, String role,Map<String, String> filters,
             Optional<String> date, Optional<Integer> age) throws ParseException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<OrderMessages> root = criteriaBuilderQuery.from(OrderMessages.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<OrderMessages, LabDetail> labDetail = root.join(OrderMessages_.labDetail);
        Join<OrderMessages, Patients> patientsJoin = root.join(OrderMessages_.patients);
        Join<OrderMessages, Physicians> physiciansJoin = root.join(OrderMessages_.physicians);
        Join<OrderMessages, UserModel> userModelJoinLeft =null;
        Join<OrderMessages, UserModel> userModelJoin = null;

        if(!status.equals(CaseStatus.NEWCASES)){
            userModelJoin = root.join(OrderMessages_.userModels,JoinType.INNER);
        }else{
            userModelJoinLeft = root.join(OrderMessages_.userModels,JoinType.LEFT);
        }
        if(StringUtils.equalsIgnoreCase(role, LAB_REVIEWER)){
            predicates.add(criteriaBuilder.equal(userModelJoin.get("id"), userId));}

        if(!StringUtils.equalsIgnoreCase(role, LAB_REVIEWER) && !status.equals(CaseStatus.NEWCASES)){
            predicates.add(criteriaBuilder.isNotNull(userModelJoin));
        }

        List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
        Path<String> casestatus = root.get(OrderMessages_.caseStatus);
        Predicate predicate = casestatus.in(closedStatus);


        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar c = Calendar.getInstance(timeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(timeZone);
        c.setTime(sdf.parse(sdf.format(c.getTime())));

        c.add(Calendar.DAY_OF_YEAR, -7);
        Date agingDate = c.getTime();
        switch(status){
            case NEWCASES:
                if(StringUtils.equalsIgnoreCase(role, LAB_ADMIN) || StringUtils.equalsIgnoreCase(role, SUPER_ADMIN)){
                    predicates.add(criteriaBuilder.isNull(userModelJoinLeft));
                    predicates.add(criteriaBuilder.not(predicate));
                } else if (StringUtils.equalsIgnoreCase(role, LAB_REVIEWER)) {
                    return null;
                }
                break;
            case INPROCESSCASES:
                if(date.isEmpty())
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get(OrderMessages_.assignedDate), agingDate));
                predicates.add(criteriaBuilder.not(predicate));
                break;
            case PENDINGCASES:
                if(date.isEmpty())
                    predicates.add(criteriaBuilder.lessThan(root.<Date>get(OrderMessages_.assignedDate), agingDate));
                predicates.add(criteriaBuilder.not(predicate));
                break;
            case CLOSEDCASES:
                predicates.add(criteriaBuilder.and(predicate));
        }

        predicates.add(criteriaBuilder.equal(labDetail.get(LabDetail_.labid), labId));
        filter(labId, filters, date, age, criteriaBuilder, root, predicates, labDetail, patientsJoin, physiciansJoin, status);
        criteriaBuilderQuery.select(criteriaBuilder.countDistinct(root.get(OrderMessages_.accessionId)))
                .where(predicates.toArray(new Predicate[]{}));
       return entityManager.createQuery(criteriaBuilderQuery)
                .getSingleResult();

    }
   public  List<String> getAccessionIdForUserMail(String userMail, String labId){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = cb.createQuery(String.class);
        Root<OrderMessages> root = criteriaQuery.from(OrderMessages.class);
        Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels);
        Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(userModelJoin.get(UserModel_.email), userMail));
            predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));
        criteriaQuery.select(root.get(OrderMessages_.accessionId))
                .where(cb.equal(userModelJoin.get(UserModel_.email), userMail));
       return entityManager.createQuery(criteriaQuery).getResultList();
     }


   public List<String> getOpenAccessoinIds(String labId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = cb.createQuery(String.class);
        Root<OrderMessages> root = criteriaQuery.from(OrderMessages.class);
        Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels);
        Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isNull(userModelJoin));
        predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));

        criteriaQuery.select(root.get(OrderMessages_.accessionId)).where(predicates.toArray(new Predicate[]{}));
       return entityManager.createQuery(criteriaQuery).getResultList();

   }

   public Long findCaseCount(String labId, Optional<String> usermail, String cases){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<OrderMessages> root = criteriaQuery.from(OrderMessages.class);
        Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail);

        List<Predicate> predicates = new ArrayList<>();
        List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
        Path<String> casestatus = root.get(OrderMessages_.caseStatus);
        Predicate predicate = casestatus.in(closedStatus);
        predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));
        if(usermail.isPresent()){
            Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels);
            predicates.add(cb.equal(userModelJoin.get(UserModel_.email), usermail.get()));
        }
        if(StringUtils.equalsIgnoreCase(cases, "closed")){
            predicates.add(cb.and(predicate));
        }else{
            predicates.add(cb.not(predicate));
        }
        criteriaQuery.select(cb.count(root))
                .where(predicates.toArray(new Predicate[]{}));
       return entityManager.createQuery(criteriaQuery).getSingleResult();

   }
   public Page<OrderMessages> findByAccessionId(String accessionId, String labId, CaseListingStatus status, int pageNo, int pageSize){
       CriteriaBuilder cb = entityManager.getCriteriaBuilder();
       CriteriaQuery<OrderMessages> cq = cb.createQuery(OrderMessages.class);
       Root<OrderMessages> root = cq.from(OrderMessages.class);

       Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail);
       List<Predicate> predicates = new ArrayList<>();
       List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
       Path<String> casestatus = root.get(OrderMessages_.caseStatus);
       Predicate predicate = casestatus.in(closedStatus);

       predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));
       predicates.add(cb.like(root.get(OrderMessages_.accessionId), "%" + accessionId + "%"));
       if(status.equals(CaseListingStatus.CLOSEDCASES)){
           predicates.add(cb.and(predicate));
       }else{
           predicates.add(cb.not(predicate));
       }

       cq.select(root)
               .where(predicates.toArray(new Predicate[]{}));
      List<OrderMessages> orderMessages =  entityManager.createQuery(cq)
              .setFirstResult(pageNo)
              .setMaxResults(pageSize)
               .getResultList();
       Long count = count(labId, accessionId, status);
       return new PageImpl<>(orderMessages, PageRequest.of(pageNo, pageSize), count);


   }

    private Long count(String labId, String accessionId, CaseListingStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<OrderMessages> root = cq.from(OrderMessages.class);

        Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail);
        List<Predicate> predicates = new ArrayList<>();
        List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
        Path<String> casestatus = root.get(OrderMessages_.caseStatus);
        Predicate predicate = casestatus.in(closedStatus);

        predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));
        predicates.add(cb.like(root.get(OrderMessages_.accessionId), "%" + accessionId + "%"));
        if(status.equals(CaseListingStatus.CLOSEDCASES)){
            predicates.add(cb.and(predicate));
        }else{
            predicates.add(cb.not(predicate));
        }

        cq.select(cb.count(root))
                .where(predicates.toArray(new Predicate[]{}));
       return entityManager.createQuery(cq)
                .getSingleResult();

    }

    public  Long deactivateUserOpenIssues(String labId, Long userid){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<OrderMessages> root = cq.from(OrderMessages.class);

        Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail);
        Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels, JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();
        List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
        Path<String> casestatus = root.get(OrderMessages_.caseStatus);
        Predicate predicate = casestatus.in(closedStatus);

        predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));
        predicates.add(cb.not(predicate));
        predicates.add(cb.equal(userModelJoin.get(UserModel_.id), userid));
        cq.select(cb.count(root))
                .where(predicates.toArray(new Predicate[]{}));
        return entityManager.createQuery(cq)
                .getSingleResult();

    }

    public List<AllocationAssignedCasesDto> getAssignedCasesCount(String lab) {
        List<AllocationAssignedCasesDto> alList = new ArrayList<>();
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            List<Predicate> predicates = new ArrayList<>();
            Root<OrderMessages> root = cq.from(OrderMessages.class);
            Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels, JoinType.INNER);
            Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail, JoinType.LEFT);
            predicates.add(cb.isTrue(userModelJoin.get(UserModel_.isActive)));

            predicates.add(cb.equal(labDetailJoin.get("labid"),lab));

            List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
            Path<String> casestatus = root.get(OrderMessages_.caseStatus);
            Predicate predicate = casestatus.in(closedStatus);
            predicates.add(cb.not(predicate));
            cq.multiselect(userModelJoin.get(UserModel_.ID), cb.count(root))
                    .where(predicates.toArray(new Predicate[]{}))
                    .groupBy(userModelJoin.get(UserModel_.ID));

            List<Object[]> list = entityManager.createQuery(cq).getResultList();
            //AllocationAssignedCasesOpenDto
            for (Object[] object : list) {
                AllocationAssignedCasesDto all = new AllocationAssignedCasesDto();
                all.setUserId(Long.parseLong(object[0].toString()));
                all.setAssingedCasesCount(Integer.parseInt(object[1].toString()));
                alList.add(all);
            }
        }catch (Exception e){
            log.info("Exception occured in getAssignedCasesCount::{}",e);
        }
        return alList;
    }

    public List<AllocationUnassignedDto> findUnAssignedCasesAllocation(String lab) {
        List<AllocationUnassignedDto> results = new ArrayList<>();
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<AllocationUnassignedDto> cq = cb.createQuery(AllocationUnassignedDto.class);
            Root<OrderMessages> root = cq.from(OrderMessages.class);
            Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels, JoinType.LEFT);
            Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail, JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(labDetailJoin.get("labid"),lab));
            predicates.add(cb.isNull(userModelJoin));
            CompoundSelection<AllocationUnassignedDto> selection
                    = cb.construct(AllocationUnassignedDto.class,
                    root.get(OrderMessages_.ID), root.get(OrderMessages_.accessionId));
            cq.select(selection).where(predicates.toArray(new Predicate[]{}));

            results = entityManager.createQuery(cq).getResultList();
        }catch (Exception e){
            System.out.println(e);
            log.info("Exception occured in findUnAssignedCasesAllocation::{}",e);
        }
        return results;
    }

    public List<OrderMessages> findOrderMessageByLabDetailAndCaseStatus(String labId, String assingedCases) {
        List<OrderMessages> orderMessagesList = new ArrayList<>();
        try {
            TypedQuery<OrderMessages> tq = entityManager.createQuery("SELECT a FROM OrderMessages a LEFT JOIN FETCH a.userModels au  where a.labDetail.labid=:labId and a.caseStatus=:status", OrderMessages.class);
            tq.setParameter("labId",labId);
            tq.setParameter("status",assingedCases);
            orderMessagesList = tq.getResultList();
            //orderMessagesList.stream().forEach(om-> System.out.println(om.getAccessionId()));
        }catch (Exception e){
            log.info("Exception occured in findOrderMessageByLabDetailAndCaseStatus::{}",e);
        }
        return orderMessagesList;
    }

    public Long allCasesCount(String labId, String assingedCases) {
        Long l = 1L;
        try {
            TypedQuery<Long> tq = entityManager.createQuery("SELECT count(a.id) FROM OrderMessages a LEFT JOIN a.userModels au  where a.labDetail.labid=:labId and a.caseStatus=:status", Long.class);
            tq.setParameter("labId",labId);
            tq.setParameter("status",assingedCases);
            l = tq.getSingleResult();
            //orderMessagesList.stream().forEach(om-> System.out.println(om.getAccessionId()));
        }catch (Exception e){
            log.info("Exception occured in findOrderMessageByLabDetailAndCaseStatus::{}",e);
        }
        return l;
    }
    public Long assingedCasesCount(String labId, String assingedCases) {
        Long l = 1L;
        try {
            TypedQuery<Long> tq = entityManager.createQuery("SELECT count(a.id) FROM OrderMessages a INNER JOIN a.userModels au  where a.labDetail.labid=:labId and a.caseStatus=:status", Long.class);
            tq.setParameter("labId",labId);
            tq.setParameter("status",assingedCases);
            l = tq.getSingleResult();
            //orderMessagesList.stream().forEach(om-> System.out.println(om.getAccessionId()));
        }catch (Exception e){
            log.info("Exception occured in findOrderMessageByLabDetailAndCaseStatus::{}",e);
        }
        return l;
    }

    public Page<OrderMessages> findNewCasesAndLabDetail(String labId, Pageable paging) throws ParseException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderMessages> cq = cb.createQuery(OrderMessages.class);
        Root<OrderMessages> root = cq.from(OrderMessages.class);
        Join<OrderMessages, UserModel> userModelJoin = root.join(OrderMessages_.userModels, JoinType.LEFT);
        Join<OrderMessages, LabDetail> labDetailJoin = root.join(OrderMessages_.labDetail, JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(labDetailJoin.get("labid"),labId));
        predicates.add(cb.isNull(userModelJoin));
        List<String> closedStatus = Arrays.asList(CASE_STATUS_CLOSED, CASE_STATUS_CLOSED_F);
        Path<String> casestatus = root.get(OrderMessages_.caseStatus);
        Predicate predicate = casestatus.in(closedStatus);
        predicates.add(cb.not(predicate));
         cq.select(root)
                 .where(predicates.toArray(new Predicate[]{}));

         List<OrderMessages> orderMessages =  entityManager.createQuery(cq)
                 .setFirstResult(paging.getPageNumber())
                 .setMaxResults(paging.getPageSize())
                 .getResultList();

        Long count = getCaseCountByStatus(labId, Optional.ofNullable(null), "newcases");
        return new PageImpl<>(orderMessages,paging, count);

    }
}
