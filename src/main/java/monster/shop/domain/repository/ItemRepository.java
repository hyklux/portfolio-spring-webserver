package monster.shop.domain.repository;

import monster.shop.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    /**
     * itemNm 을 통해 아이템 리스트 반환.
     * @param itemNm 아이템 이름
     * @return 아이템 리스트 반환.
     */
    List<Item> findByItemNm(String itemNm);

    /**
     * 아이템 이름 또는 아이템 상세 정보 검색
     * @param itemNm 아이템 이름
     * @param itemDetail 아이템 상세 설명
     * @return 아이템 리스트 반환
     */
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    /**
     * where itemPrice < price 조건 검색
     * @param price 가격
     * @return 조건에 충족되는 아이템 리스트 반환.
     */
    List<Item> findByPriceLessThan(Integer price);

    /**
     * 가격 내림차순 조회 테스트
     * @param price 가격
     * @return 조건 충족 아이템 리스트 반환.
     */
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    /**
     * 가격 오름차순 조회 테스트
     * @param price 가격
     * @return 조건 충족 아이템 리스트 반환.
     */
    List<Item> findByPriceLessThanOrderByPriceAsc(Integer price);

    /**
     * Query annotation 이용한 like 조건 테스트.
     * @param itemDetail 아이템 상세 정보.
     * @return 조건 충족 아이템 리스트 반환.
     */
    @Query("SELECT i from Item i where i.itemDetail like %:itemDetail% order by i.price desc ")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    /**
     * Query native - DB Sql 사용하는 문법
     * @param itemDetail 아이템 상세 정보
     * @return 조건 충족 아이템 리스트 반환.
     */
    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
