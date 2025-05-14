public class ExpenseModel {
    String desc ;
    String date ;
    Double amount;
    int userid;
    int groupid;
    int expenseid;

    public ExpenseModel(String desc, String date, Double amount, int userid, int groupid, int expenseid) {
        this.desc = desc;
        this.date = date;
        this.amount = amount;
        this.userid = userid;
        this.groupid = groupid;
        this.expenseid = expenseid;
    }

    public ExpenseModel() {

    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getExpenseid() {
        return expenseid;
    }

    public void setExpenseid(int expenseid) {
        this.expenseid = expenseid;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }


}
