package com.ems.common.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import com.ems.domain.dto.emp.ImportEmp;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description TODO
 * @create 2024-12-29  22:28
 */
@Slf4j
public class BaseExcelListener<T> extends AnalysisEventListener<T> {

    // 用于存储读取到的Excel数据对象列表
    private List<T>  dataList = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        log.info("读取到一条数据:{}", JSON.toJSONString(t));
        // 每读取一行数据，就将其添加到dataList中
        dataList.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 当所有数据读取完成后，可以在这里进行一些后续操作，如打印读取到的数据数量
        log.info("读取完成，共读取了 {} 条数据", dataList.size());
    }

    // 提供一个方法用于获取存储数据的列表
    public List<T> getDataList() {
        return dataList;
    }
}