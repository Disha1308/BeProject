package com.beproject.QAmanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beproject.QAmanagement.models.*;
import com.beproject.QAmanagement.models.QuestionRating.type;

@Repository
public interface AnswerRatingRepository extends CrudRepository<AnswerRating,Long>
{

	@Query("select count(a) from AnswerRating a where a.answerid=:aid and vote= :t ")
	long getvote(@Param("aid") long aid, @Param("t") type upvote);
	
	@Query("select a from AnswerRating a where a.answerid=:aid and a.userid= :uid ")
	AnswerRating findByUseridAnswerid(@Param("aid") long aid, @Param("uid") long uid);
	


}