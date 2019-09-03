package com.zjyouth.service;

import com.zjyouth.common.ServerResponse;

import java.util.Map;

/**
 * Created by Administrator on 2018/8/7.
 */

public interface IStatisticManageService {


      ServerResponse<Map> getBaseCount();

}