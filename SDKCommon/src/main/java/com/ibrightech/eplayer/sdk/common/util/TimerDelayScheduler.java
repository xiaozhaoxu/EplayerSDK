package com.ibrightech.eplayer.sdk.common.util;

import android.os.Message;

import java.util.Hashtable;

/**
 * 延迟定时器，可以暂停，恢复后执行剩余时间
 */
public class TimerDelayScheduler {
    public static  final  int TASK_WAT=1000;
    public static  final  int RETRYCOUNT_MAX=3;

    private Object lock = new Object();

    private TimeTaskListener listener;

    private Hashtable<String, TimeTaskData> timeTaskDataHashtable = new Hashtable();

    public void setListener(TimeTaskListener listener) {
        this.listener = listener;
    }

    public TimerDelayScheduler(TimeTaskListener listener){
         this.listener=listener;
    }

    TimerDelayHandler handler = new TimerDelayHandler(this);

    public class TimerDelayHandler extends WeakReferenceHandler<TimerDelayScheduler> {
        public TimerDelayHandler(TimerDelayScheduler timerDelayScheduler) {
            super(timerDelayScheduler);
        }

        @Override
        protected void handleMessage(Message msg, TimerDelayScheduler timerDelayScheduler) {
            if(CheckUtil.isEmpty(msg))return;
            if(CheckUtil.isEmpty(timerDelayScheduler))return;


            String taskToken = (String) msg.obj;

            TimeTaskData  timeTaskData =timerDelayScheduler.findTimeTaskData(taskToken);

            //定位器未被暂停或者无效
            if (null != timeTaskData&&!timeTaskData.pause&&!timeTaskData.invalidate) {
                timeTaskData.retryCount-=1;
                if (null != timerDelayScheduler.listener) {
                    timerDelayScheduler.listener.handlerTimeTaskFinished(timeTaskData.taskToken,timeTaskData.retryCount);
                }
                //如果重试次数为0了，移除定时任务
                if(timeTaskData.retryCount<=0){
                    invalidateWaitTask(timeTaskData.taskToken);
                }else {
                    scheduledWaitTask(timeTaskData.taskToken,timeTaskData.allmillisecond,timeTaskData.retryCount);
                }
            }
        }
    }

    public TimeTaskData createTimeTaskData(long taskToken,int retryCount){
        TimeTaskData timeTaskData =  new TimeTaskData();
        timeTaskData.taskToken= taskToken;
        timeTaskData.starttime= System.currentTimeMillis();
        timeTaskData.invalidate = false;
        timeTaskData.retryCount=retryCount;

        timeTaskDataHashtable.put(taskToken+"",timeTaskData);

        return timeTaskData;
    }


    public TimeTaskData findTimeTaskData(String key){
        synchronized (lock){
            if(key!=null)
                return timeTaskDataHashtable.get(key);
        }
        return null;
    }



    //开启一个延时定时器
    public void scheduledWaitTask(long taskToken, long millisecond) {
        scheduledWaitTask(taskToken,millisecond,1);
    }
    //开启一个延时定时器
    public void scheduledWaitTask(long taskToken, long millisecond,int retryCount) {
        synchronized (lock){
            invalidateWaitTask(taskToken);

            TimeTaskData  timeTaskData =createTimeTaskData(taskToken,retryCount);
            timeTaskData.millisecond= millisecond;
            timeTaskData.allmillisecond=millisecond;

            Message msg =handler.obtainMessage();
            msg.what = TASK_WAT;
            msg.obj = "" + taskToken;
            handler.sendMessageDelayed(msg, timeTaskData.millisecond);

        }
    }



    //暂停延时定时器
    public void pauseWaitTask(long taskToken) {
        synchronized (lock){
            handler.removeMessages(TASK_WAT, "" + taskToken);


            TimeTaskData  timeTaskData = timeTaskDataHashtable.get(""+taskToken);
            timeTaskData.pause = true;
            timeTaskData.invalidate =true;
            timeTaskData.millisecond = System.currentTimeMillis() - timeTaskData.starttime ;
        }
    }


    //回复延时定时器，执行剩余时间
    public void resumeWaitTask(long taskToken) {
        synchronized (lock) {
            handler.removeMessages(TASK_WAT, "" + taskToken);

            TimeTaskData timeTaskData = timeTaskDataHashtable.get("" + taskToken);
            timeTaskData.pause = false;
            timeTaskData.invalidate =false;

            Message msg =handler.obtainMessage();
            msg.what = TASK_WAT;
            msg.obj = "" + taskToken;
            handler.sendMessageDelayed(msg,timeTaskData.millisecond);
        }
    }

    //将定时器置为无效
    public void invalidateWaitTask(long taskToken) {
        synchronized (lock){
            handler.removeMessages(TASK_WAT,""+taskToken);
            timeTaskDataHashtable.remove(""+taskToken);
        }
    }

    //将所有定时器置为无效
    public void invalidateAllWaitTask() {
        synchronized (lock){
            handler.removeMessages(TASK_WAT);
        }
    }

    public interface TimeTaskListener {
        void handlerTimeTaskFinished(long taskToken,int retryCount);
    }

    class TimeTaskData {
        public int retryCount=1;
        public long taskToken;
        public long starttime;

        public long allmillisecond;//总时间，millisecond的值不准
        public long millisecond;

        public boolean pause;
        public boolean invalidate;

    }


}
