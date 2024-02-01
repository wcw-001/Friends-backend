package com.wcw.usercenter.once;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * 导入Excel
 * @author wcw
 */
public class ImportExcel {
    /**
     * 监听器读取数据
     */
    @Test
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "D:\\用户中心\\user-center\\src\\main\\resources\\user.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, UserInfo.class, new TableListener()).sheet().doRead();
    }
    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    @Test
    public void synchronousRead() {
        String fileName = "D:\\用户中心\\user-center\\src\\main\\resources\\user.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserInfo> list = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        for (UserInfo userInfo : list) {
            System.out.println(userInfo);
        }
    }
}
