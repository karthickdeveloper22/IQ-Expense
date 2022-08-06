package com.androidheroes.iqexpensemanager.Models;

public class ModelExpenseCategories {
    int expenseid,userid;
    String name;

    public ModelExpenseCategories(int expenseid, int userid, String name) {
        this.expenseid = expenseid;
        this.userid = userid;
        this.name = name;
    }

    public int getExpenseid() {
        return expenseid;
    }

    public void setExpenseid(int expenseid) {
        this.expenseid = expenseid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
