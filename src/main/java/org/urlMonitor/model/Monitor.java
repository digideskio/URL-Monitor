package org.urlMonitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.urlMonitor.model.type.StatusType;

/**
 * @author Alex Eng - loones1595@gmail.com
 *
 */
public class Monitor implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Getter
   private JobKey key;// This needs to be unique
   
   @Getter
   @NonNull
   private String name; 

   @Getter
   private String description;

   @Getter
   @NonNull
   private String url;

   private String tag;

   @Getter
   private StatusType status;
   
   @Getter
   private Date lastCheck;

   /**
    * see http://en.wikipedia.org/wiki/Cron#CRON_expression
    */
   @Getter
   private String cronExpression = "0/5 * * * * ?"; // every 5 seconds

   @Getter
   @NonNull
   private String contentRegex;

   @Setter
   private String emailToList;

   public Monitor(Properties prop)
   {
      this.name = prop.getProperty("name");
      this.description = prop.getProperty("description");
      this.url = prop.getProperty("url");
      this.tag = prop.getProperty("tag");

      if (!StringUtils.isEmpty(prop.getProperty("cronExpression")))
      {
         this.cronExpression = prop.getProperty("cronExpression");
      }

      this.contentRegex = prop.getProperty("contentRegex");
      this.emailToList = prop.getProperty("emailToList");
      
      this.key = new JobKey(this.name);
   }

   public List<String> getEmailTo()
   {
      if (emailToList != null)
      {
         return Arrays.asList(emailToList.split(";"));
      }
      return new ArrayList<String>();
   }

   public List<String> getTag()
   {
      if (tag != null)
      {
         return Arrays.asList(tag.split(";"));
      }
      return new ArrayList<String>();
   }
   
   public void update(StatusType status)
   {
      this.status = status;
      this.lastCheck = new Date();
   }

}
