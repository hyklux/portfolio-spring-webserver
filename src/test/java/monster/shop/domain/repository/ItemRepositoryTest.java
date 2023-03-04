package monster.shop.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import monster.shop.domain.constant.ItemSellStatus;
import monster.shop.domain.model.Item;
import monster.shop.domain.model.QItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void before() {
        createItemList();
    }

    @AfterEach
    void after() {
        List<Item> itemList = itemRepository.findAll();
        itemRepository.deleteAll(itemList);
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        Item saveItem = itemRepository.save(item);
        log.debug("item : {}", saveItem);
        assertThat(saveItem.getId()).isNotNull();
    }

    private void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            Item save = itemRepository.save(item);
            assertThat(save.getId()).isNotNull();
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    void findByItemNmTest() {
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for (Item item : itemList) {
            log.debug("{}", item);
        }
        assertThat(itemList).hasSize(1);
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    void findByItemNmOrItemDetailTest() {
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        itemList.forEach(System.out::println);
        assertThat(itemList).hasSize(2);
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    void findByPriceLessThanTest() {
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        itemList.forEach(System.out::println);
        assertThat(itemList).hasSize(4);
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    void findByPriceLessThanOrderByPriceDesc() {
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        itemList.forEach(System.out::println);
        assertThat(itemList).hasSize(4);
    }

    @Test
    @DisplayName("가격 오름차순 조회 테스트")
    void findByPriceLessThanOrderByPriceAsc() {
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceAsc(10005);
        itemList.forEach(System.out::println);
        assertThat(itemList).hasSize(4);
    }

    @Test
    @DisplayName("@Query 이용한 상품 조회 테스트")
    void findByItemDetailTest() {
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        itemList.forEach(System.out::println);
        assertThat(itemList).hasSize(10);
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    void findByItemDetailByNative() {
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        itemList.forEach(System.out::println);
        assertThat(itemList).hasSize(10);
    }

    @Test
    @DisplayName("QueryDsl 조회 테스트1")
    void queryDslTest() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();
        assertThat(itemList).hasSize(10);
        itemList.forEach(k -> log.debug("{}", k));
    }

    private void itemSoldOut() {
        List<Item> all = itemRepository
                .findAll()
                .stream()
                .skip(5)
                .collect(Collectors.toList());
//        all.forEach(d -> log.debug("{}", d));
        for (Item item : all) {
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            log.debug("update : {}", item);
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 QueryDsl 조회 테스트2")
    void queryDslTest2() {
        itemSoldOut();

        BooleanBuilder builder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        builder.and(item.itemDetail.like("%" + itemDetail + "%"));
        builder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            builder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        PageRequest pageable = PageRequest.of(0, 5);
        Page<Item> itemPageResult = itemRepository.findAll(builder, pageable);
        System.out.println("total element : " + itemPageResult.getTotalElements());

        List<Item> resultItemList = itemPageResult.getContent();
        for (Item resultItem : resultItemList) {
            System.out.println("resultItem = " + resultItem);
        }
        assertThat(resultItemList).hasSize(2);
    }
}