package com.example.progressdaemon.main.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.example.progressdaemon.R;
import com.example.progressdaemon.main.LogUtils;

/**
 * Created by mulinrui on 2018/2/1.
 */
public class AccountHelper {

    private static final String TAG = "AccountHelper";

    public static final String ACCOUNT = "mlr";
    public static final String PASSWORD = "mlr";

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

}
