package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.PageBean;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.*;

/**
 * 商品搜索服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-09<p>
 */
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /** 添加或修改索引 */
    public void saveOrUpdate(List<SolrItem> solrItems){
        try{
            UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
            if (updateResponse.getStatus() == 0){
                solrTemplate.commit();
            }else {
                solrTemplate.rollback();
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 删除索引 */
    public void delete(Long[] goodsIds){
        try{
            // 创建查询对象
            Query query = new SimpleQuery();
            // 创建条件对象
            Criteria criteria = new Criteria("goodsId").is(Arrays.asList(goodsIds));
            // 添加条件对象
            query.addCriteria(criteria);
            // 删除
            UpdateResponse updateResponse = solrTemplate.delete(query);
            if (updateResponse.getStatus() == 0){
                solrTemplate.commit();
            }else {
                solrTemplate.rollback();
            }

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 搜索方法 */
    public Map<String,Object> search(Map<String, Object> search){
        Integer pageNum = (Integer) search.get("pageNum");
        Integer pageSize = (Integer) search.get("pageSize");
        String criteria = (String) search.get("criteria");
        String category = (String) search.get("category");
        String brand = (String) search.get("brand");
        Map<String,String> spec = (Map<String, String>) search.get("spec");
        Map<String,String> sort = (Map<String, String>) search.get("sort");
        String price = (String) search.get("price");
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        if (category != null){
            query.addCriteria(new Criteria("category").contains(category));
        }
        if (brand != null){
            query.addCriteria(new Criteria("brand").contains(brand));
        }
        if (!spec.isEmpty()){
            for (String s : spec.keySet()) {
                if (spec.get(s) != null){
                    query.addCriteria(new Criteria("spec_"+s).contains(spec.get(s)));
                }
            }
        }
        if (!sort.isEmpty()){
            for (String s : sort.keySet()) {
                if (sort.get(s) != null){
                    query.addSort(new Sort(sort.get(s) .equals("asc")  ? Sort.Direction.ASC: Sort.Direction.DESC,s));
                }
            }
        }
        if (price != null ){
            String[] split = price.split("-");
            query.addCriteria(new Criteria("price").greaterThanEqual(split[0]));
            if (!split[1].equals("*")){
                query.addCriteria(new Criteria("price").lessThanEqual(split[1]));
            }
        }
        query.setOffset((pageNum-1)*pageSize);
        query.setRows(pageSize);
        HighlightPage<SolrItem>  page ;
        Map<String, Object> map = new HashMap<>();
        if (!criteria.equals("")){
            query.addCriteria(new Criteria("keywords").is(criteria));
            HighlightOptions highlightOptions =
                    new HighlightOptions().addField("title")
                            .setSimplePrefix("<span style='color:red'><strong>")
                            .setSimplePostfix("</st></span>");
            query.setHighlightOptions(highlightOptions);
            page = solrTemplate.queryForHighlightPage(query, SolrItem.class);
            for (HighlightEntry<SolrItem> entry : page.getHighlighted()) {
                SolrItem solrItem = entry.getEntity();
                if (entry.getHighlights().size() > 0
                        && entry.getHighlights().get(0)
                        .getSnipplets().size() > 0) {
                    /** 设置高亮的结果 */
                    solrItem.setTitle(entry.getHighlights().get(0)
                            .getSnipplets().get(0));
                }
            }
        }else {
            query.addCriteria(new Criteria("keywords").contains(criteria));
            page = solrTemplate.queryForHighlightPage(query, SolrItem.class);
        }
        //Query query = new SimpleQuery("*:*");

        PageBean<Object> pageBean = new PageBean<>();
        pageBean.setSize(page.getTotalPages());
        pageBean.setCount((int) page.getTotalElements());
        pageBean.setCurrent(pageNum);
        pageBean.setSize(pageSize);
        map.put("itemList",page.getContent());
        map.put("page",pageBean);

        query.setOffset(0);
        query.setRows((int)page.getTotalElements());
        Set<String> categorySet = new HashSet<>();
        Set<String> brandSet = new HashSet<>();
        Map<String,Set<String>> specMap = new HashMap<>();
        for (SolrItem solrItem : solrTemplate.queryForPage(query,SolrItem.class).getContent()) {
            categorySet.add(solrItem.getCategory());
            brandSet.add(solrItem.getBrand());
            Map<String, String> specMaps = solrItem.getSpecMap();
            if (specMaps != null){
                for (String key : specMaps.keySet()) {
                    if (specMap.containsKey(key)){
                        Set<String> values = specMap.get(key);
                        values.add(specMaps.get(key));
                        specMap.put(key,values);
                    }else {
                        Set<String> values = new HashSet<>();
                        values.add(specMaps.get(key));
                        specMap.put(key,values);
                    }
                }
            }
        }
        map.put("categorySet",categorySet);
        map.put("brandSet",brandSet);
        map.put("specMap",specMap);
        return map;
    }


}
