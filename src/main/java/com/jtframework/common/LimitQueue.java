package com.jtframework.common;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 固定长度队列
 * @author gary
 *
 */
public class LimitQueue<E> extends LinkedList implements Collection {

    //队列长度
    private int limit;


    public LimitQueue(int limit){
        this.limit = limit;
    }

    public synchronized boolean add(Object e){
        if(this.size() >= limit){
            //如果超出长度,入队时,先出队
            this.pollFirst();
        }
        this.addLast(e);
        return true;
    }

    public E getLast(){
        return this.getLast();
    }

    public E[] toArray(){
        return (E[]) super.toArray();
    }


}
