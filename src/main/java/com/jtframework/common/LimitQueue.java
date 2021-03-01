package com.jtframework.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * 固定长度队列
 *
 * @author gary
 */
public class LimitQueue<E> extends LinkedList<E> implements Serializable {

    //队列长度
    private int limit;

    public LimitQueue() {
        this.limit = 0;
    }

    public LimitQueue(int limit) {
        this.limit = limit;
    }

    public synchronized boolean add(Object e) {
        if (this.limit != 0 && this.size() >= limit) {
            //如果超出长度,入队时,先出队
            this.pollFirst();
        }

        this.addLast((E) e);
        return true;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
