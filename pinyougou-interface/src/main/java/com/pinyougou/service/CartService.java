package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List; /**
 * 购物车的服务接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-18<p>
 */
public interface CartService {


    /**
     * 添加SKU商品到购物车
     * @param carts 购物车集合
     * @param itemId SKU的id
     * @param num 购买数量
     * @return 返回修改后的购物车集合
     */
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    /** 把购物车数据存储到Redis */
    void saveCartRedis(String userId, List<Cart> carts);

    /** 从Redis数据库中获取购物车 */
    List<Cart> findCartRedis(String userId);

    /**
     *  购物车合并
     * @param cookieCarts Cookie购物车
     * @param redisCarts Redis购物车
     * @return 返回合并后的购物车集合
     */
    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts);

    void saveCartOrdersToRedis(String userId, List<Cart> cartOrders, List<Cart> carts);

    List<Cart> findCartOrdersFromRedis(String userId);
}
