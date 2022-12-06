package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.OpenJPATemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//통합 테스트를 위해 스프링부트에서 제공하는 어노테이션. 실제 애플리케이션을 구동할 때 처럼 모든 Bean을 IoC 컨테이너에 등록
//애플리케이션의 규모가 크면 속도가 느려질 수 있음.
@TestPropertySource(locations = "classpath:application-test.properties")
//테스트 코드 실행 시 application.properties에 설정해둔 값보다 application.properties에 설정해둔 값보다 application-test.properties에 같은 설정이 있다면 더 높은 우선순위를 부여하게 됨. 기존에는 MySQL을 테스트 코드 실행시에는 H2 데이터 베이스를 사용
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;


    @Autowired
    ItemRepository itemRepository;
//   ItemRepository를 사용하기 위해서 @Autowired 어노테이션을 이용하여 Bean 주입

    @Test
//    테스트할 메소드 위에 선언하여 해당 메소드를 테스트 대상으로 지정함
    @DisplayName("상품 저장 테스트")
//    Junit5에 추가된 어노테이션으로 테스트 코드 실행 시 @DisplayName에 지정한 테스트명이 노출
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemList(){
//      테스트 코드 실행시 데이터베이스에 상품 데이터가 없으므로 테스트 데이터 생성을 위해서 10개의 상품을 저장하는 메소드르 작성하여 findByItemNmTest()에서 실행
        for(int i=1; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
//      itemRepository 인터페이스에 작성했던 findByItemNm 메소드 호출 -> 파라미터로는 "테스트 상품1"이라는 상품명 전달
        for(Item item : itemList) {
            System.out.println(item.toString());
//      조회 결과 얻은 item 객체들을 출력함.
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
//        기존에 만들었던 테스트 상품을 만든느 메소드를 실행하여 조회할 대상을 만들어주기
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
//        상품명이 "테스트 상품1" 또는 상품 상세 설명이 "테스트 상품 상세 설명5"이면 해당 상품을 itemList에 할당합니다. 테스트 코드를 실행하면 조건대로 2개의 상품이 출력되는 것을 볼 수 있습니다.

        for(Item item :itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item: itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();

        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void createItemList2() {
//        상품 데이터를 만드는 새로운 메소드 하나 만들기. 1번~5번 상품은 상품의 판매 상태를 SELL(판매 중)으로 지정하고ㅓ,. 6번 ~10번 상품은 판매 상태를 SOLD_OUT(품절)으로 세팅해 생성
        for( int i=1; i<=5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for (int i=6;i<=10;i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2() {
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
//        BooleanBuilder는 쿼리에 들어갈 조건을 만들어주는 빌더라고 생각하면 됨. Predicate를 구현하고 있으며, 메소드 체인 형식으로 사용 가능
        QItem item = QItem.item;
        String itemDetail = "테슽츠 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%"+itemDetail+"%"));
//        필요한 상품을 조회하는데 필요한 "and" 조건을 추가하고 있음. 아래 소스에서 상품의 판매상태가 SELL일 때만 booleanBuilder에 판매상태 조건을 동적으로 추가하는 것을 볼 수 있음.
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
//        데이터를 페이징해 조회하도록 PageRequest.of() 메소드를 이용해 Pageable 객체를 생성. 첫 번째 인자는 조회할 페이지의 번호, 두 번째 인자는 한 페이지 당 조회할 데이터의 개수를 넣어줌
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
//        QueryDslPredicateExecutor 인터페이스에서 정의한 findAll() 메소드를 이용해 조건에 맞는 데이터를 Page 객체로 받아옴
        System.out.println("total elements : " + itemPagingResult. getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem:resultItemList) {
            System.out.println(resultItem.toString());
        }


    }


}