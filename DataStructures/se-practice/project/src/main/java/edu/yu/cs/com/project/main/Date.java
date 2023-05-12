package edu.yu.cs.com.project.main;

import java.util.Objects;

public class Date implements Comparable{
    private int[] calendar;
    private int month;
    private int day;
    private int currentVal;
    public Date(int month, int day){
        this.month = month;
        this.day = day;
        this.calendar = new int[360];
        this.currentVal = (this.month * 30) + this.day;

        this.calendar[currentVal] = 1;
    }

    public int getMonth(){
        return this.month;
    }

    public int getDay(){
        return this.day;
    }

    public int getCurrentVal(){
        return currentVal;
    }



    @Override
    public int compareTo(Object o) {
        if(!(o instanceof Date)){
            throw new IllegalArgumentException();
        }
        Date obj = (Date) o;
        return this.currentVal - obj.getCurrentVal();
    }
}
