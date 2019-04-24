package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页数据
 */
public class PageBean<T> implements Serializable{

    //从数据库中查询出来
    private List<T> data;  //一页数据
    private int count;  //总记录数

    //这个计算得到
    private int first;  //首页
    private int previous;  //上页
    private int next;  //下页
    private int total;  //总页数，末页
    private int start;
    private int end;
    //由用户提供
    private int current;  //当前页
    private int size;  //页面大小

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFirst() {
        return 1;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    //计算上一页
    public int getPrevious() {
        if (getCurrent() > 1) {
            return getCurrent() - 1;
        }
        return 1;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    //计算下一页
    public int getNext() {
        if (getCurrent() < getTotal()) {
            return getCurrent() + 1;
        }
        return getTotal();
    }

    public void setNext(int next) {
        this.next = next;
    }

    //计算总页数
    public int getTotal() {
        //如果能整除就整除，不能整除加1
        if (getCount() % getSize() == 0 ) {
            return getCount() / getSize();   //总记录数/页面大小
        }
        else {
            return getCount() / getSize() + 1;   //总记录数/页面大小
        }
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        if (1 > current - 2 || getTotal() < 5){
            return 1;
        }else if(getTotal() < current + 2 ){
            return getTotal() - 5;
        }
        return current - 2;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        if (getTotal() < current + 2 || getTotal() < 5){
            return getTotal();
        }else if (current - 2 < 1){
            return 5;
        }
        return current + 2;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
