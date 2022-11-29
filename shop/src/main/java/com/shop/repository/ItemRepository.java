package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
//    2개의 제네릭 타입을 사용하는데 첫 번째는 엔티티 타입을, 두번째에는 기본키 타입을 넣어준다.
//    Item 클래스는 키본키 타입이 Long이므로 Long을 넣어줌
//    JpaRepository는 기본적인 CRUD 및 페이징 처리를 위한 메소드가 정의되어 있음

    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
//    상품을 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드

    List<Item> findByPriceLessThan(Integer price);
//    파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터를 조회하는 쿼리 메소드

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

}
