package com.zjyouth.pojo;

import java.util.List;

/**
 * 作者:created by Administrator on 2018/8/8 19:35
 * 作用:
 */
public class LogisticsDetail {

    /**
     * message : ok
     * nu : 220192092873
     * ischeck : 0
     * condition : 00
     * com : zhongtong
     * status : 200
     * state : 0
     * data : [{"time":"2018-08-08 09:40:11","ftime":"2018-08-08 09:40:11","context":"【杭州市】 快件已被 【丰巢的杭州万轮科技园 丰巢智能快递柜】 代收, 如有问题请电联（4000633333,13705819219）,感谢使用中通快递,期待再次为您服务!","location":"丰巢的杭州万轮科技园 丰巢智能快递柜"},{"time":"2018-08-08 08:00:54","ftime":"2018-08-08 08:00:54","context":"【杭州市】 【滨江东部】 的侯鹏13705819219（13705819219） 正在第1次派件, 请保持电话畅通,并耐心等待","location":"滨江东部"},{"time":"2018-08-08 05:36:52","ftime":"2018-08-08 05:36:52","context":"【杭州市】 快件到达 【滨江东部】","location":"滨江东部"},{"time":"2018-08-07 14:57:08","ftime":"2018-08-07 14:57:08","context":"【嘉兴市】 快件离开 【杭州中转部】 发往 【滨江东部】","location":"杭州中转部"},{"time":"2018-08-07 14:51:10","ftime":"2018-08-07 14:51:10","context":"【嘉兴市】 快件到达 【杭州中转部】","location":"杭州中转部"},{"time":"2018-08-05 19:18:37","ftime":"2018-08-05 19:18:37","context":"【哈尔滨市】 快件离开 【哈尔滨中转】 发往 【杭州中转部】","location":"哈尔滨中转"},{"time":"2018-08-05 19:14:31","ftime":"2018-08-05 19:14:31","context":"【哈尔滨市】 快件到达 【哈尔滨中转】","location":"哈尔滨中转"},{"time":"2018-08-05 18:02:06","ftime":"2018-08-05 18:02:06","context":"【哈尔滨市】 快件离开 【哈尔滨透笼】 发往 【杭州中转部】","location":"哈尔滨透笼"},{"time":"2018-08-05 17:24:24","ftime":"2018-08-05 17:24:24","context":"【哈尔滨市】 【哈尔滨透笼】（0451-84674244） 的 于涛 （18745127530） 已揽收","location":"哈尔滨透笼"}]
     */

    private String message;
    private String nu;
    private String ischeck;
    private String condition;
    private String com;
    private String status;
    private String state;
    private List<DataBean> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * time : 2018-08-08 09:40:11
         * ftime : 2018-08-08 09:40:11
         * context : 【杭州市】 快件已被 【丰巢的杭州万轮科技园 丰巢智能快递柜】 代收, 如有问题请电联（4000633333,13705819219）,感谢使用中通快递,期待再次为您服务!
         * location : 丰巢的杭州万轮科技园 丰巢智能快递柜
         */

        private String time;
        private String ftime;
        private String context;
        private String location;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFtime() {
            return ftime;
        }

        public void setFtime(String ftime) {
            this.ftime = ftime;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
