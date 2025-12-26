package com.debbech.spongebob.service;

import com.debbech.spongebob.websocket.WsServer;
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

    public static String addNewJob(String job_name, String group_name){
        JobDetail job = JobBuilder.newJob(WriteStateToSocket.class)
                .withIdentity(job_name, group_name)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(job_name, group_name )
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .build();

        try {
            org.quartz.Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if(!scheduler.checkExists(job.getKey())) {
                scheduler.scheduleJob(job, trigger);
            }
            return job.getKey().toString();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

    public static void deleteNewJob(String job_name, String job_group){
        try {
            org.quartz.Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if(scheduler.checkExists(new JobKey(job_name, job_group))) {
                scheduler.deleteJob(new JobKey(job_name, job_group));
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
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

    @DisallowConcurrentExecution
    public static class WriteStateToSocket implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            WsServer.adminBroadcast(Processor.getStatus());
        }
    }
}
