package com.cms.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class WriteFileThread {
    MsgList msgList;
    Boolean flag = true;

    public WriteFileThread(MsgList msgList) {
        msgList = msgList;
    }

    public static String getRandomFileName() {

        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        // Random random = new Random();

        // int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) +
        // 10000;// 获取5位随机数

        // return rannum + str;// 当前时间
        return str;
    }

    private static String getCurrentDateString() {
        long result = System.currentTimeMillis();
        return String.valueOf(result);
    }

    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.getPath();
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void writelogFile(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            String filePathName = getSDPath() + "/" + fileName + getRandomFileName() + ".txt";
            FileWriter writer = new FileWriter(filePathName, true);
            writer.write("["+content+"],"+"\r\n");//直接生成格式文件
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writelogFile(String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            String filePathName = getSDPath() + "/" + getRandomFileName()
                    + ".txt";
            FileWriter writer = new FileWriter(filePathName, true);
            writer.write(getCurrentDateString() + "," + content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writelogFile(MsgItem content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            String filePathName = getSDPath() + "/" + getRandomFileName() + "_" + content.FileName
                    + ".txt";
            FileWriter writer = new FileWriter(filePathName, true);
            writer.write(content.msgText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
