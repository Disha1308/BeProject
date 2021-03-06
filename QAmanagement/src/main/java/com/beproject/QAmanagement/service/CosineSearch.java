package com.beproject.QAmanagement.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beproject.QAmanagement.models.Question;
import com.beproject.QAmanagement.repository.QuestionRepository;

@Component
public class CosineSearch 
{
	@Autowired(required=true)
	QuestionRepository qrepo;
	
	 public class values
     {

          int val1;
          int val2;
          values(int v1, int v2)
          {
           this.val1=v1;
           this.val2=v2;
          }
          public void Update_VAl(int v1, int v2)
          {
           this.val1=v1;
           this.val2=v2;
          }

     }//end of class values
	 
	 
	 public double Cosine_Similarity_Score(String Text1, String Text2)
     {

          double sim_score=0.0000000;
          //1. Identify distinct words from both documents
          String [] word_seq_text1 = Text1.split(" ");
          String [] word_seq_text2 = Text2.split(" ");
          Hashtable<String, values> word_freq_vector = new Hashtable<String, CosineSearch.values>();
          LinkedList<String> Distinct_words_text_1_2 = new LinkedList<String>();
           
          //prepare word frequency vector by using Text1
          for(int i=0;i<word_seq_text1.length;i++)
          {
           String tmp_wd = word_seq_text1[i].trim();
           if(tmp_wd.length()>0)
           {
            if(word_freq_vector.containsKey(tmp_wd))
            {
             values vals1 = word_freq_vector.get(tmp_wd);
             int freq1 = vals1.val1+1;
             int freq2 = vals1.val2;
             vals1.Update_VAl(freq1, freq2);
             word_freq_vector.put(tmp_wd, vals1);
            }
            else
            {
             values vals1 = new values(1, 0);
             word_freq_vector.put(tmp_wd, vals1);
             Distinct_words_text_1_2.add(tmp_wd);
            }
           }
          }
           
          //prepare word frequency vector by using Text2
          for(int i=0;i<word_seq_text2.length;i++)
          {
           String tmp_wd = word_seq_text2[i].trim();
           if(tmp_wd.length()>0)
           {
            if(word_freq_vector.containsKey(tmp_wd))
            {
             values vals1 = word_freq_vector.get(tmp_wd);
             int freq1 = vals1.val1;
             int freq2 = vals1.val2+1;
             vals1.Update_VAl(freq1, freq2);
             word_freq_vector.put(tmp_wd, vals1);
            }
            else
            {
             values vals1 = new values(0, 1);
             word_freq_vector.put(tmp_wd, vals1);
             Distinct_words_text_1_2.add(tmp_wd);
            }
           }
          }
           
          //calculate the cosine similarity score.
          double VectAB = 0.0000000;
          double VectA_Sq = 0.0000000;
          double VectB_Sq = 0.0000000;
           
          for(int i=0;i<Distinct_words_text_1_2.size();i++)
          {
           values vals12 = word_freq_vector.get(Distinct_words_text_1_2.get(i));
           
           double freq1 = (double)vals12.val1;
           double freq2 = (double)vals12.val2;
           System.out.println(Distinct_words_text_1_2.get(i)+"#"+freq1+"#"+freq2);
            
           VectAB=VectAB+(freq1*freq2);
            
           VectA_Sq = VectA_Sq + freq1*freq1;
           VectB_Sq = VectB_Sq + freq2*freq2;
          }
          System.out.println("VectAB "+VectAB+" VectA_Sq "+VectA_Sq+" VectB_Sq "+VectB_Sq);
          sim_score = ((VectAB)/(Math.sqrt(VectA_Sq)*Math.sqrt(VectB_Sq)));
           
          return(sim_score);

     }
	 
	 public List<Question> search(String keywords)
	 {
		 List<Question> documents = (List<Question>) qrepo.findAll();
		 Map<Question, Double> documents_score = new LinkedHashMap<Question, Double>();
         List<Question> result = new ArrayList<Question>();
		 int i =0 ;
		 while(documents != null && i<documents.size())
		 {
			double csvalue =  Cosine_Similarity_Score(keywords.toLowerCase(),documents.get(i).getQuestionText().toLowerCase());
			if(csvalue > 0)
			 documents_score.put(documents.get(i), csvalue); 
			 System.out.println("cosine sim:"+documents.get(i).getQuestionText()+":"+csvalue);
			 System.out.println("Result:"+documents.get(i).getTitle()+":"+csvalue+"\n");
			 
			 i++;
		 }
		 System.out.print("Before sorting");
			
		 i =0 ;
			Iterator<Entry<Question, Double>> mapit = documents_score.entrySet().stream().iterator();
			 while(documents_score != null && i<documents_score.size()&& mapit.hasNext())
			 {
				 Entry<Question, Double> e = mapit.next();
				 System.out.println("Result:"+e.getKey().getTitle()+":"+e.getValue()+"\n");
				 
				 i++;
			 }
			 
		mapit = documents_score.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).iterator();
		System.out.print("After sorting");
		i =0 ;
		//mapit = documents_score.entrySet().stream().iterator();
		 while(documents_score != null && i<documents_score.size()&& mapit.hasNext())
		 {
			 Entry<Question, Double> e = mapit.next();
			 System.out.println("Result:"+e.getKey().getTitle()+":"+e.getValue()+"\n");
			 result.add(e.getKey());
			 i++;
		 }
		//result.stream().limit(5);
		return result;
	 }
	 
	 
}
