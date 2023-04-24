package com.example.demo.updates;

import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EmailScheduler {

    @Autowired
    EmailSendJob emailSendJob;

    // Cannot get scheduler to work -- autowired components in EmailSendJob keeps coming up as null :(
    public void runScheduler() {
        System.out.println("Starting email scheduler");
        JobDetail job = JobBuilder.newJob(emailSendJob.getClass()).withIdentity("emailJob", "group").build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 23 23  ? * *")) // at 8am every day
                .build();
        Scheduler scheduler;

        {
            try {
                scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.start();
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
}
