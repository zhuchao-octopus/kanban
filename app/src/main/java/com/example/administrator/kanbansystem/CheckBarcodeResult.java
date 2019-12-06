package com.example.administrator.kanbansystem;

import java.util.List;

/**
 * Created by ztz on 2017/5/15 0015.
 */

public class CheckBarcodeResult {

    /**
     * msg : 批内重码
     * state : 5
     * date : null
     */

    private String msg;
    private int state;
    private List<beanData> date;
    private String date1;
    private String date2;
    private String date3;
    private String date4;

    public List<beanData> getDate() {
        return date;
    }

    public void setDate(List<beanData> date) {
        this.date = date;
    }

    public String getDate5() {
        return date5;
    }

    public void setDate5(String date5) {
        this.date5 = date5;
    }

    private String date5;



    public String getDate4() {
        return date4;
    }

    public void setDate4(String date4) {
        this.date4 = date4;
    }

    public String getDate3() {
        return date3;
    }

    public void setDate3(String date3) {
        this.date3 = date3;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String data2) {
        this.date2 = data2;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public static class beanData{
        private String line;
        private List<boardsData> productBoards;

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public List<boardsData> getProductBoards() {
            return productBoards;
        }

        public void setProductBoards(List<boardsData> productBoards) {
            this.productBoards = productBoards;
        }
    }
    public static class boardsData{
        private String productxt;
        private String orderid;
        private String customername;
        private String model;
        private String motherbtype;
        private int ordernumber;
        private String prodnumber;
        private String prodnumber_in;
        private String prod_Prodnumber;
        private int cumulat_number;
        private int cumulat_Prodnumber;
        private int order_shortage;
        private String remarks;

        public String getProductxt() {
            return productxt;
        }

        public void setProductxt(String productxt) {
            this.productxt = productxt;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getCustomername() {
            return customername;
        }

        public void setCustomername(String customername) {
            this.customername = customername;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getMotherbtype() {
            return motherbtype;
        }

        public void setMotherbtype(String motherbtype) {
            this.motherbtype = motherbtype;
        }

        public int getOrdernumber() {
            return ordernumber;
        }

        public void setOrdernumber(int ordernumber) {
            this.ordernumber = ordernumber;
        }

        public String getProdnumber() {
            return prodnumber;
        }

        public void setProdnumber(String prodnumber) {
            this.prodnumber = prodnumber;
        }

        public String getProdnumber_in() {
            return prodnumber_in;
        }

        public void setProdnumber_in(String prodnumber_in) {
            this.prodnumber_in = prodnumber_in;
        }

        public String getProd_Prodnumber() {
            return prod_Prodnumber;
        }

        public void setProd_Prodnumber(String prod_Prodnumber) {
            this.prod_Prodnumber = prod_Prodnumber;
        }

        public int getCumulat_number() {
            return cumulat_number;
        }

        public void setCumulat_number(int cumulat_number) {
            this.cumulat_number = cumulat_number;
        }

        public int getCumulat_Prodnumber() {
            return cumulat_Prodnumber;
        }

        public void setCumulat_Prodnumber(int cumulat_Prodnumber) {
            this.cumulat_Prodnumber = cumulat_Prodnumber;
        }

        public int getOrder_shortage() {
            return order_shortage;
        }

        public void setOrder_shortage(int order_shortage) {
            this.order_shortage = order_shortage;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
