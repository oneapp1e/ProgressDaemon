title: 双进程守护
tags:
  - Android
  - 进程
  - 保活
  - 守护
categories: Android高级
date: 2018-02-02 10:34:11


前言

	Android开发app，都会遇到这样的需求，怎么才能让我们的产品在用户的手机上停留的时间够长，怎么提升留存，怎么提升推送到达率等等。这些问题提出的根源还是在于Android的碎片化严重还有就是Google在中国不能使用，因此国内就出来了很多各个厂商自己定制的系统，各种鱼龙混杂的推送，而最近发布的Android系统又收紧各种权限，各个厂商为了提升性能，各种优化，最后导致的问题就是app进程被系统回收，推送服务达不到要求等等。

	虽然现在再写这篇文章有点晚，在最新的Android8.0系统上还没有更好的办法进行保活，但还是要总结一下自己走过的坑。

提权

	在进行进程保活之前，先来熟悉一下进程的权限提升，来保证app的进程不那么快被回收。

	进程的优先级根据重要性层次结构分为5级：

		1.前台进程

		2.可见进程

		3.服务进程

		4.后台进程

		5.空进程

		参考:Android官方文档

	那么进程提升权限的方法就是尽量提升进程等级来保护进程不被回收。

提权方法：

1. 配置服务优先级
   在manifest中配置服务的过滤器
       <service android:name=".main.service.InnnerService" > 
       	<intent-filter android:priority="1000">
            	<action android:name="com.mlr.myservice" />
        	</intent-filter>
        </service>
2. 将服务设置为前台进程
   Android文档:托管正在“前台”运行的 Service（服务已调用 startForeground()）
   新建一个ForegroundService通过调用 startForeground() 方法使service成为前台进程
           @Override
           public void onCreate() {
               super.onCreate();
               //让服务变成前台服务
               LogUtils.e(" ForegroundService 服务提权已经启动 ");
               startForeground(10, new Notification());
               //如果是18以上的设备  这样操作会在通知栏有一个通知，那再开启个服务把通知干掉
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                   startService(new Intent(getApplicationContext(), InnnerService.class));
               }
           }
   如果是18以上的设备通过 startForeground() 方法，会在系统通知栏生成一条通知，为了去除通知启动一个 InnnerService 使用同一个 noticeId ,在 stopself 
       public class InnnerService extends Service {
       
           @Nullable
           @Override
           public IBinder onBind(Intent intent) {
               return null;
           }
       
           @Override
           public void onCreate() {
               super.onCreate();
               //使用同一个通知id
               startForeground(10, new Notification());
               stopSelf();
           }
       }
   
3. Activity提权
   	启动一个一个像素透明的activity，由于要不影响用户其他操作，因此只能锁屏后提权，在用户解锁后销毁掉，否则不销毁在用户开屏的时间会直接打开app。
   	新建KeepActivity设置宽高一像素
               Window window = getWindow();
               window.setGravity(Gravity.LEFT | Gravity.TOP);
               //宽高设置1
               WindowManager.LayoutParams attributes = window.getAttributes();
               attributes.width = 1;
               attributes.height = 1;
               attributes.x = 0;
               attributes.y = 0;
               window.setAttributes(attributes);
   	在manifest中注册
               <activity
                   android:name=".main.keep.KeepActivity"
                   android:excludeFromRecents="true"
                   android:taskAffinity="com.mlr.demo.keep"
                   android:theme="@style/KeepTheme" />
   	Theme设置
           <style name="KeepTheme">
               <item name="android:windowBackground">@null</item>
               <item name="android:windowIsTranslucent">true</item>
           </style>
   

双进程保活

   	双进程保活机制是使用的aidl机制，本地进程和远程进程通过AIDL通信建立连接，本地进程被kill后，远程的进程收到 onServiceDisconnected 连接断开的通知，那么远程进程在此时重启本地进程并重新建立连接，同理远程进程被kill后，本地可可以如此处理。这种机制也已经被大厂ROM给限制的差不多了。慎用吧。

	新建 loclService 同时对service进行的提权处理:

    public class LocalService extends Service {
    
        private MyBind mMyBind;
        private MyConn mMyConn;
    
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return mMyBind;
        }
    
        @Override
        public void onCreate() {
            super.onCreate();
            LogUtils.setDebugable(true);
            LogUtils.e("testbbs 本地服务启动了");
    
            mMyBind = new MyBind();
            if (mMyConn == null) {
                mMyConn = new MyConn();
            }
            startForeground(20, new Notification());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                startService(new Intent(this, InnerService.class));
            }
        }
    
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            bindService(new Intent(LocalService.this, RemoteService.class), mMyConn, BIND_IMPORTANT);
            return super.onStartCommand(intent, flags, startId);
        }
    
        class MyBind extends IProgressService.Stub {
    
            @Override
            public String getServiceName() throws RemoteException {
                return "LocalService";
            }
        }
    
        class MyConn implements ServiceConnection {
    
    
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.e("testbbs 连接远程服务开始");
            }
    
            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtils.e("testbbs 远程服务被杀死了");
                Toast.makeText(LocalService.this, "远程服务被杀死了!", Toast.LENGTH_SHORT).show();
                //启动远程服务  绑定远程服务
                startService(new Intent(LocalService.this, RemoteService.class));
                if (mMyConn == null) {
                    mMyConn = new MyConn();
                }
                bindService(new Intent(LocalService.this, RemoteService.class), mMyConn,
                        BIND_IMPORTANT);
            }
        }
    
        public static class InnerService extends Service {
    
            @Nullable
            @Override
            public IBinder onBind(Intent intent) {
                return null;
            }
    
            @Override
            public void onCreate() {
                super.onCreate();
                startForeground(20, new Notification());
                stopSelf();
            }
        }
    
    }

	同理新建 RemoteService ，忘了最重要的了，新建AIDL文件(这个是第一步啊)：

    package com.example.progressdaemon;
    interface IProgressService {
    	String getServiceName();
    }
    



使用JobScheduler保活

	JobScheduler是Android5.0以后提供的方法，JobScheduler是允许在特定状态与特定时间间隔周期执行任务。可以理解为：开启的一个系统的定时器。但是在国内大厂的rom上表现不佳。

        public static void startJob(Context context) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobInfo.Builder builder = new JobInfo.Builder(10, new ComponentName(context.getPackageName(),
                        MyJobService.class.getName())).setPersisted(true);
    
                //小于7.0
                if (Build.VERSION.SDK_INT < 24) {
                    builder.setPeriodic(1_000);
                } else {
                    builder.setMinimumLatency(1_000);
                }
                jobScheduler.schedule(builder.build());
            }
        }

	注意manifest中需要在service中添加权限android.permission.BIND_JOB_SERVICE

            <service
                android:name=".main.Jobschuduler.MyJobService"
                android:exported="false"
                android:permission="android.permission.BIND_JOB_SERVICE" />

	新建一个jobService

    public class MyJobService extends JobService {
    
        private static final String TAG = "MyJobService";
    
        @Override
        public boolean onStartJob(JobParameters params) {
            LogUtils.e(TAG + " 开启job");
            //7.0以上 轮询
            if (Build.VERSION.SDK_INT >= 24) {
                JobHelper.startJob(this);
            }
        }
    
        @Override
        public boolean onStopJob(JobParameters params) {
            LogUtils.e(TAG + " job 停止了");
            return false;
        }
    
    }





账户同步保活

	这个对于没有帐号同步需求的app，去做一个帐号同步功能会给用户一个很不好的体验的，具体看需求吧。

	添加账户同步功能比较复杂：

        /**
         * 添加一个账户
         */
        public static void addAccount(Context context) {
            AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            String ACCOUNT_TYPE = context.getResources().getString(R.string.account_type);
            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
            if (accounts.length > 0) {
                LogUtils.e(TAG + " 账户已经存在");
                return;
            }
            //如果账户不存在 给这个账户类型添加一个帐户
            Account mlr = new Account(ACCOUNT, ACCOUNT_TYPE);
            accountManager.addAccountExplicitly(mlr, PASSWORD, new Bundle());
        }
    
    
        /**
         * 设置 账户自动同步
         */
        public static void autoSync(Context context) {
            AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            String ACCOUNT_TYPE = context.getResources().getString(R.string.account_type);
            String AUTHORITY = context.getResources().getString(R.string.authority);
            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
            if (accounts.length > 0) {
                Account account = accounts[0];
                //设置同步
                ContentResolver.setIsSyncable(account, AUTHORITY, 1);
                //自动同步
                ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
                //设置同步周期
                ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), 1);
            }
        }

	还需要在manifest中设置账户授权和同步服务：

            <!--账户服务-->
            <service android:name=".main.account.AuthenticationService">
    
                <intent-filter>
                    <action android:name="android.accounts.AccountAuthenticator" />
                </intent-filter>
                <meta-data
                    android:name="android.accounts.AccountAuthenticator"
                    android:resource="@xml/accountauthenticator" />
    
            </service>
            <service android:name=".main.account.SyncService">
                <intent-filter>
                    <action android:name="android.content.SyncAdapter" />
                </intent-filter>
                <meta-data
                    android:name="android.content.SyncAdapter"
                    android:resource="@xml/sync_adapter" />
            </service>
    
            <provider
                android:name=".main.account.SyncProvider"
                android:authorities="com.mlr.progress.daemon.provider" />

	记得到xml中配置 accountauthenticator ，及 sync_adapter 及添加相关权限



推送保活

	根据手机设备不同，在小米手机（包括 MIUI）接入小米推送、华为手机接入华为推送，其他接入个推，极光等可以作为拉活方案的有效补充。



广播保活

	系统经常会发送一些特定的系统广播，如：开屏，锁屏，开机，电量，安装，注册等，通过注册广播监听器，对这些广播进行接收，然后拉活app。

	但是Google早已经发现了这个问题，在Android7.0开始对广播进行了限制，8.0更加严格。



合作保活

	有多个合作的app在用户设备上安装，只要开启其中一个，就可以将其他合作的app给拉活。这种方式在大厂出产的app全家桶中都有出现，腾讯系，阿里系，百度系等都有自己的全家桶服务。

  

Native层fork保活

	在各大厂商的努力合作下，该方式基本不能使用了，不过还是说明一下原理：和双进程守护原理差不多，不过比这更耗电，需要启动一个natice轮询进程，时刻查询app住进程是否存在，不存在就重新创建。



---

欢迎大家指正：

https://github.com/oneapp1e/ProgressDaemon
