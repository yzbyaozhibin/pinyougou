package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 商品搜索控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-09<p>
 */
@RestController
public class ItemSearchController {

    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    /** 搜索方法(解决搜索高并发) */
    @PostMapping("/Search")
    public Map search(@RequestBody Map<String,Object> search){
        return itemSearchService.search(search);
    }

}
