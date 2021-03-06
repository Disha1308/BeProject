package com.beproject.QAmanagement.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.beproject.QAmanagement.models.*;
import com.beproject.QAmanagement.models.Notification.notificationstatus;
import com.beproject.QAmanagement.repository.*;


@Component
public class NotificationDTOService 
{
	@Autowired
	NotificationRepository notifyRepo;
	
	@Autowired
	AnswerRepository aRepo;
	
	@Autowired
	QuestionRepository qRepo;
	
	@Autowired
	NegotiationMessageRepository negoRepo;
	
	public List<NotificationDTO> getnotifications(long uid,int pageno) 
	{
		List<Notification> nlist = notifyRepo.findByuserid(uid);
		List<NotificationDTO> ndtolist = new ArrayList<NotificationDTO>();
		if(nlist==null)		
				return null;
		int i = 0;
		while(i<nlist.size())
		{
			NotificationDTO ndto = new NotificationDTO();
			Notification n = nlist.get(i++);
			switch(n.getType())
			{
			case answer: ndto = getType1Dto(n);
				break;
			case discussion: ndto = getType2Dto(n);
				break;
			case requeststatus: ndto = getType3Dto(n);
				break;
			case seekerrequest:ndto = getType4Dto(n);
				break;
			case rejection:ndto = getType5Dto(n);
				break;
			default:
				break;		
			}
			ndto.setNotificationid(n.getNotificationid());
			ndto.setNtype(n.getType());
			ndto.setState(n.getState());
			ndto.setTimestamp(n.getTimestamp());
			
			ndtolist.add(ndto);
		}	
		ndtolist.sort(Comparator.comparing(NotificationDTO::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
		int start = (pageno - 1)*10;
		if(start >= ndtolist.size())
			return null;
		List<NotificationDTO> page = new ArrayList<NotificationDTO>();
		for(int s = start; s <(start+10)  && s < ndtolist.size();s++)
			{page.add(ndtolist.get(s));}
		return page;
		
	}

	private NotificationDTO getType1Dto(Notification n) {		
		NotificationDTO ndto = new NotificationDTO();
		Answers a = aRepo.findOne(n.getAttributeid());
		if(a!=null){
		ndto.setQuestionid(a.getQuestionid());
		ndto.setUserid(a.getUserid());
		ndto.setUsername(getusername(a.getUserid()));
		ndto.setQuestiontitle(qRepo.findOne(a.getQuestionid()).getTitle());
		ndto.setPrefferedtimestamp(a.getTimestamp());}
		return ndto;
	}
	
	private NotificationDTO getType2Dto(Notification n) {
		NotificationDTO ndto = new NotificationDTO();
		NegotiationMessage nmsg = negoRepo.findOne(n.getAttributeid());
		Question q = qRepo.findOne(nmsg.getQuestionid());
		if(q!=null){
		ndto.setQuestionid(nmsg.getQuestionid());
		ndto.setQuestiontitle(q.getTitle());
		ndto.setPrefferedtimestamp(q.getPreferredTime());}
		if(n.getUserid() == nmsg.getSeekerid())
		{	ndto.setUserid(nmsg.getExpertid());
			ndto.setUsername(getusername(nmsg.getExpertid()));}
		else
		{	ndto.setUserid(nmsg.getSeekerid());
			ndto.setUsername(getusername(nmsg.getSeekerid()));}
		return ndto;
	}	
	
	private NotificationDTO getType3Dto(Notification n) {
		NotificationDTO ndto = new NotificationDTO();
		NegotiationMessage nmsg = negoRepo.findOne(n.getAttributeid());
		Question q = qRepo.findOne(nmsg.getQuestionid());
		
		ndto.setUserid(nmsg.getExpertid());
		ndto.setUsername(getusername(nmsg.getExpertid()));
		ndto.setQuestionid(nmsg.getQuestionid());
		if(q!=null){
		ndto.setQuestiontitle(q.getTitle());
		ndto.setPrefferedtimestamp(q.getPreferredTime());}
		ndto.setNegotiationStatus(nmsg.getMessagestatus().toString());
		return ndto;
	}
	
	private NotificationDTO getType4Dto(Notification n) {
		NotificationDTO ndto = new NotificationDTO();
		NegotiationMessage nmsg = negoRepo.findOne(n.getAttributeid());
		Question q = qRepo.findOne(nmsg.getQuestionid());
		ndto.setUserid(nmsg.getSeekerid());
		ndto.setUsername(getusername(nmsg.getSeekerid()));
		ndto.setQuestionid(nmsg.getQuestionid());
		if(q!= null){
		ndto.setQuestiontitle(q.getTitle());
		ndto.setPrefferedtimestamp(q.getPreferredTime());
		}
		else
		{
			//todo handle question deletion case
		}
		return ndto;
	}

	private NotificationDTO getType5Dto(Notification n)
	{
		NotificationDTO ndto = new NotificationDTO();
		Question q = qRepo.findOne(n.getAttributeid());
	
		if(q!=null){
		ndto.setUserid(q.getUserid());
		ndto.setUsername(getusername(q.getUserid()));
		ndto.setQuestionid(q.getQuestionid());
		ndto.setQuestiontitle(q.getTitle());
		}
		return ndto;
	}
	
	public String getusername(long userid) {
		try{
		RestTemplate restT = new RestTemplate();
		ResponseEntity<String> response = restT.exchange("http://localhost:8080/v1.0/username/"+userid,
			    HttpMethod.GET, null, String.class);
		String s =response.getBody();
		return s;
		}
		catch(Exception e)
		{
			System.out.println("usermanagement not available");
			return null;
		}
	}

	public boolean changenotificationstatus(long nid) 
	{
		Notification n = notifyRepo.findOne(nid);
		if(n!= null){
		n.setState(notificationstatus.read);
		notifyRepo.save(n);
		return true;
		}
		return false;
	}

	public String getquestiontitle(long questionid) {
		return qRepo.findOne(questionid).getTitle();
	}
	
}
