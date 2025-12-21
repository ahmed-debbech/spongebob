package com.debbech.spongebob.service;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Scheduler {

    public Scheduler(){

        JobDetail job = JobBuilder.newJob(ProcessDownloadedTracks.class)
                .withIdentity("j1", "process")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("t1" , "process")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .build();

        JobDetail job2 = JobBuilder.newJob(CheckForNewTracks.class)
                .withIdentity("j2", "download")
                .build();

        Trigger trigger2 = TriggerBuilder.newTrigger()
                .withIdentity("t2", "download" )
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(5)
                        .repeatForever())
                .build();



        org.quartz.Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);

            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }


        // Shutdown hook
        org.quartz.Scheduler finalScheduler = scheduler;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                finalScheduler.shutdown(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @DisallowConcurrentExecution
    public static class ProcessDownloadedTracks implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Processor.process();
        }
    }
    @DisallowConcurrentExecution
    public static class CheckForNewTracks implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            Processor.download();
        }
    }
}
