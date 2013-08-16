/**
 * 
 */
package org.urlMonitor.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.mail.EmailException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.urlMonitor.model.FailedStates;
import org.urlMonitor.model.Monitor;
import org.urlMonitor.model.type.StatusType;
import org.urlMonitor.service.events.MonitorUpdateEvent;
import org.urlMonitor.service.quartz.CronTrigger;

/**
 * @author Alex Eng(aeng)  loones1595@gmail.com
 *
 */
@Service
@Scope("singleton")
@Slf4j
public class UrlMonitorService implements ApplicationListener<MonitorUpdateEvent>
{
   @Value("${monitor.files.path}")
   private String dirPath;

   @Value("${retry.count}")
   private int retryCount;

   @Autowired
   private EmailService emailService;

   private final static String REGEX_PROPERTIES = "*.properties";

   private CronTrigger cronTrigger;
   private Map<JobKey, Monitor> monitorMap = new HashMap<JobKey, Monitor>();
   private Map<JobKey, FailedStates> monitorFailedMap = new HashMap<JobKey, FailedStates>();

   @PostConstruct
   public void init() throws SchedulerException, FileNotFoundException, IOException
   {
      log.info("==================================================");
      log.info("================= URL Monitor ====================");
      log.info("==================================================");

      initJobs();
   }

   private void initJobs() throws SchedulerException, FileNotFoundException, IOException
   {
      log.info("Initialising jobs...");

      cronTrigger = new CronTrigger();

      for (Monitor monitor : loadMonitorFiles())
      {
         JobKey jobKey = cronTrigger.scheduleMonitor(monitor);
         if (jobKey != null)
         {
            monitorMap.put(jobKey, monitor);
         }
      }
   }

   private List<Monitor> loadMonitorFiles() throws FileNotFoundException, IOException
   {
      List<Monitor> result = new ArrayList<Monitor>();

      File dir = new File(dirPath);
      if (dir.exists())
      {
         FileFilter fileFilter = new WildcardFileFilter(REGEX_PROPERTIES);
         File[] files = dir.listFiles(fileFilter);
         if (!ArrayUtils.isEmpty(files))
         {
            for (File file : files)
            {
               if (!file.isDirectory())
               {
                  Properties prop = new Properties();
                  prop.load(new FileInputStream(file));
                  result.add(new Monitor(prop));
               }
            }
         }
      }
      else
      {
         log.warn("Monitor files not found {0}", dirPath);
      }
      return result;
   }

   public List<Monitor> getMonitorList()
   {
      return new ArrayList<Monitor>(monitorMap.values());
   }

   public void onApplicationEvent(MonitorUpdateEvent event)
   {
      if (monitorMap.containsKey(event.getKey()))
      {
         Monitor monitor = monitorMap.get(event.getKey());
         monitor.update(event.getStatus());

         try
         {
            updateStates(monitor);
         }
         catch (EmailException e)
         {
            log.error("Unable to send notification email-" + e);
            e.printStackTrace();
         }
      }
   }

   /**
    * retry MAX_RETRY_COUNT times if failed or unknown, then send email
    * @param monitor
    * @throws EmailException 
    */
   private void updateStates(Monitor monitor) throws EmailException
   {
      JobKey key = monitor.getKey();
      if (monitor.getStatus() == StatusType.Pass)
      {
         if (monitorFailedMap.remove(key) != null)
         {
            emailService.sendSuccessEmail(monitor, monitor.getLastCheck());
         }
      }
      else if (monitor.getStatus() == StatusType.Unknown || monitor.getStatus() == StatusType.Failed)
      {
         if (monitorFailedMap.containsKey(key))
         {
            FailedStates failedStates = monitorFailedMap.get(key);
            failedStates.addCount();
            if (failedStates.getCount() == retryCount)
            {
               emailService.sendFailedEmail(monitor, monitor.getLastCheck());
            }
         }
         else
         {
            monitorFailedMap.put(key, new FailedStates());
         }
      }
   }
}
