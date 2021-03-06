package com.beproject.QAmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beproject.QAmanagement.models.*;
import com.beproject.QAmanagement.models.Notification.notificationtype;

@Repository
public interface NotificationRepository extends CrudRepository<Notification,Long>
{
	List<Notification> findByuserid(long uid);
	
	@Query("select n from Notification n where n.userid=:uid and n.attributeid=:aid and n.type=:t")
	Notification findunique(@Param("uid") long uid, @Param("aid") long aid, @Param("t") notificationtype t);
}
