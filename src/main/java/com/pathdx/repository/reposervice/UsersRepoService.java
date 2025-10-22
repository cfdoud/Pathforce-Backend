package com.pathdx.repository.reposervice;

import com.pathdx.model.*;
import com.pathdx.repository.BaseRepoService;
import com.pathdx.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class UsersRepoService extends BaseRepoService<UserModel,Long> {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    EntityManager entityManager;
    @Override
    protected JpaRepository<UserModel, Long> getRepository() {
        return usersRepository;
    }

    @Override
    protected Class<UserModel> getEntityClass() {
        return UserModel.class;
    }

    public Set<String> getUserMailByRoles(String labId){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<UserModel> root = cq.from(UserModel.class);
        Join<UserModel, LabDetail> labDetailJoin = root.join(UserModel_.labDetails);
        Join<UserModel, Role> roleJoin = root.join(UserModel_.roles);

        List<Long> roles = Arrays.asList(1L, 4L);
        Path<Long> roleId = roleJoin.get(Role_.id);
        Predicate predicate = roleId.in(roles);

        predicates.add(cb.equal(labDetailJoin.get(LabDetail_.labid), labId));
        predicates.add(cb.and(predicate));

        cq.select( root.get(UserModel_.email))
                .where(predicates.toArray(new Predicate[]{}));

        List<String> userMail = entityManager.createQuery(cq).getResultList();
        return new HashSet<>(userMail);
    }

    public List<String> getUsersList(String labId){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        List<Predicate> predicates = new ArrayList<>();
        Root<UserModel> root = cq.from(UserModel.class);
        Join<UserModel, LabDetail> labDetailJoin = root.join(UserModel_.labDetails);
        predicates.add(cb.notEqual(labDetailJoin.get(LabDetail_.labid), labId));
        cq.select( root.get(UserModel_.email))
                .where(predicates.toArray(new Predicate[]{}));

        return entityManager.createQuery(cq).getResultList();

    }

    public UserModel findLabDetails(Long Id) {
        UserModel userModel = new UserModel();
        try {
            TypedQuery<UserModel> tq = entityManager.createQuery("SELECT a FROM UserModel a INNER JOIN FETCH a.labDetails  where a.id=:Id", UserModel.class);
            tq.setParameter("Id",Id);
            userModel = tq.getSingleResult();
            //orderMessagesList.stream().forEach(om-> System.out.println(om.getAccessionId()));
        }catch (Exception e){
            System.out.println(e);
        }
        return userModel;
    }

    public UserModel findRoles(Long userId) {
        UserModel userModel = new UserModel();
        try {
            TypedQuery<UserModel> tq = entityManager.createQuery("SELECT a FROM UserModel a INNER JOIN FETCH a.roles  where a.id=:Id", UserModel.class);
            tq.setParameter("Id",userId);
            userModel = tq.getSingleResult();
            //orderMessagesList.stream().forEach(om-> System.out.println(om.getAccessionId()));
        }catch (Exception e){
            System.out.println(e);
        }
        return userModel;
    }
}
