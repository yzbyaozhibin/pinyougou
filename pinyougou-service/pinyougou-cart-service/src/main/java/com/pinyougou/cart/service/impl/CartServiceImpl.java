package com.pinyougou.cart.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.Cart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-18<p>
 */
@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加SKU商品到购物车
     * @param carts 购物车集合
     * @param itemId SKU的id
     * @param num 购买数量
     * @return 返回修改后的购物车集合
     */
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num){
        try{
            // 1. 根据sku的id从tb_item查询商品数据
            Item item = itemMapper.selectByPrimaryKey(itemId);


            // 2. 根据商家的id从用户的购物车集合中找到对应的商家的购物车
            String sellerId = item.getSellerId();
            Cart cart = searchCartBySellerId(carts, sellerId);

            // 3. 判断用户是否购买过该商家的商品
            if (cart == null){ // 代表没有购买过
                // 创建商家的购物车
                cart = new Cart();
                // 设置商家id
                cart.setSellerId(sellerId);
                // 设置商家名称
                cart.setSellerName(item.getSeller());

                // 创建该商家的购物车商品集合
                List<OrderItem> orderItems = new ArrayList<>();
                // 创建购买的商品对象OrderItem (把item转化成OrderItem)
                OrderItem orderItem = createOrderItem(item, num);

                // 添加用户购买的商品到商家的购物车商品集合中
                orderItems.add(orderItem);

                // 设置该商家的购物车商品
                cart.setOrderItems(orderItems);

                // 添加到用户的购物车集合中
                carts.add(cart);

            }else{ // 代表购买过

                // 获取该商家购买的商品集合
                List<OrderItem> orderItems = cart.getOrderItems();
                // 根据商品id从商家的购物车商品集合中搜索该商品
                OrderItem orderItem = searchOrderItemByItemId(orderItems, item.getId());

                // 判断是否购买过同样的商品
                if (orderItem != null){ // 购买过同样的商品
                    // 购买数量相加
                    orderItem.setNum(orderItem.getNum() + num);
                    // 计算小计金额
                    orderItem.setTotalFee(new BigDecimal(orderItem.getNum()
                            * orderItem.getPrice().doubleValue()));

                    // 判断购买数量是否等于零
                    if (orderItem.getNum() == 0){
                        // 从商家的购物车商品集合中删除该商品
                        orderItems.remove(orderItem);
                    }
                    if (orderItems.size() == 0){
                        // 从用户的购物车集合中删除该商家的购物车
                        carts.remove(cart);
                    }

                }else{ // 没有购买过同样的商品
                    orderItem = createOrderItem(item, num);
                    // 添加到该商家的购物车商品集合中
                    orderItems.add(orderItem);
                }
            }
            return carts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据商品id从商家的购物车商品集合中搜索该商品 */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long id) {
        for (OrderItem orderItem : orderItems) {
            if (id.equals(orderItem.getItemId())){
                return orderItem;
            }
        }
        return null;
    }

    /** 创建购物车中的商品 */
    private OrderItem createOrderItem(Item item, Integer num) {
        // 创建购物车中的商品对象
        OrderItem orderItem = new OrderItem();
        // SKU的id
        orderItem.setItemId(item.getId());
        // SPU的id
        orderItem.setGoodsId(item.getGoodsId());
        // 商品标题
        orderItem.setTitle(item.getTitle());
        // 商品价格
        orderItem.setPrice(item.getPrice());
        // 购买数量
        orderItem.setNum(num);
        // 小计金额
        orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * num));
        // 商品图片
        orderItem.setPicPath(item.getImage());
        // 商家id
        orderItem.setSellerId(item.getSellerId());

        return orderItem;
    }

    // 根据商家的id从用户的购物车集合中找到对应的商家的购物车
    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        // 迭代用户的购物车集合
        for (Cart cart : carts) {
            if (sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }


    /** 把购物车数据存储到Redis */
    public void saveCartRedis(String userId, List<Cart> carts){
        try{
            redisTemplate.boundValueOps("cart_" + userId).set(carts);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 从Redis数据库中获取购物车 */
    public List<Cart> findCartRedis(String userId){
        try{
            List<Cart> carts = (List<Cart>)redisTemplate.boundValueOps("cart_" + userId).get();
            if (carts == null){
                carts = new ArrayList<>();
            }
            return carts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 购物车合并
     * @param cookieCarts Cookie购物车
     * @param redisCarts Redis购物车
     * @return 返回合并后的购物车集合
     */
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts){
       try{
           // 循环Cookie中的购物车集合
           for (Cart cookieCart : cookieCarts) {
               for (OrderItem orderItem : cookieCart.getOrderItems()) {
                   redisCarts = addItemToCart(redisCarts, orderItem.getItemId(),
                           orderItem.getNum());
               }
           }
           return redisCarts;
       }catch (Exception ex){
           throw new RuntimeException(ex);
       }
    }

    @Override
    public void saveCartOrdersToRedis(String userId, List<Cart> cartOrders, List<Cart> carts) {
        List<Cart> cartsCopy = JSONArray.parseArray(JSONArray.toJSONString(carts),Cart.class);
        try{
            redisTemplate.boundValueOps("cartOrder_" + userId).set(cartOrders);
            for (Cart cartOrder : cartOrders) {
                for (int j = 0; j < cartOrder.getOrderItems().size(); j++) {
                    for (int k = 0; k < cartsCopy.size(); k++) {
                        for (int l = 0; l < cartsCopy.get(k).getOrderItems().size(); l++) {
                            if (cartOrder.getOrderItems().get(j).getItemId().longValue() == cartsCopy.get(k).getOrderItems().get(l).getItemId().longValue()) {
                                carts.get(k).getOrderItems().remove(l);
                                if (carts.get(k).getOrderItems().size() == 0) {
                                    carts.remove(k);
                                }
                            }
                        }
                    }
                }
            }

            redisTemplate.boundValueOps("cart_" + userId).set(carts);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<Cart> findCartOrdersFromRedis(String userId){
        try{
            List<Cart> carts = (List<Cart>)redisTemplate.boundValueOps("cartOrder_" + userId).get();
            if (carts == null){
                carts = new ArrayList<>();
            }
            return carts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
