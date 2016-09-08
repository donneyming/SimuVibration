package com.cms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;
public class MsgList {



	private Vector<MsgItem> messageList = null;
	private Lock lock = null;
	/** 单键实例 */
	private static MsgList mList;

	public static synchronized MsgList instance() {
		if (mList == null) {
			mList = new MsgList();
		}
		return mList;
	}

	private MsgList() {
		if (messageList == null) {
			messageList = new Vector<MsgItem>();
		}
		if (lock == null) {
			lock = new ReentrantLock();
		}
	}

	public MsgItem GetMsg(int index) {
        MsgItem item = null;
        try {
            lock.lock();
            item =  messageList.get(index);
        } finally {
            lock.unlock();
        }
        return item;
	}

	public void AddMsg(MsgItem msg) {
		try {
			lock.lock();
			messageList.add(msg);
		} catch (Exception ex) {

		} finally {
			lock.unlock();
		}
	}

	public void RemoveMsg(MsgItem msg) {
		try {
			lock.lock();
			messageList.remove(msg);
		} catch (Exception ex) {

		} finally {
			lock.unlock();
		}
	}

	public int GetSize() {
		return messageList.size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
