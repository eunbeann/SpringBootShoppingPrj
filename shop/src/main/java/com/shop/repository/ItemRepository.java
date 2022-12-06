package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
//    Query~ 인터페이스 상속 추가
//    2개의 제네릭 타입을 사용하는데 첫 번째는 엔티티 타입을, 두번째에는 기본키 타입을 넣어준다.
//    Item 클래스는 키본키 타입이 Long이므로 Long을 넣어줌
//    JpaRepository는 기본적인 CRUD 및 페이징 처리를 위한 메소드가 정의되어 있음

    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
//    상품을 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드

    List<Item> findByPriceLessThan(Integer price);
//    파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터를 조회하는 쿼리 메소드

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
// @Query 어노테이션 안에 JPQL로 작성한 쿼리문 넣기. from 뒤에는 엔티티 클래스로 작성한 Item을 지정, Item으로부터 데이터는 select
    List <Item> findByItemDetail (@Param("itemDetail") String itemDetail);
//    파라미터에 @Param 어노테이션을 이용하려 파라미너토 넘어온 값을 JPQL에 들어갈 변수로 지정 가능. 현재는 itemDetail 변수를 "like % %" 사이에 ":itemDetail" 값이 들어가도록 작성

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
//    value 안에 네이티브 쿼리문을 작성하고 "nativeQuery = true" 지정
    List<Item> findByItemDetailByNative (@Param("itemDetail") String itemDetail);
}
