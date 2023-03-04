package monster.shop.domain.model;

import lombok.extern.slf4j.Slf4j;
import monster.shop.domain.constant.ItemSellStatus;
import monster.shop.domain.dto.MemberFormDto;
import monster.shop.domain.repository.ItemRepository;
import monster.shop.domain.repository.MemberRepository;
import monster.shop.domain.repository.OrderItemRepository;
import monster.shop.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setName("윤일");
        memberFormDto.setEmail("tingcobell@nm-monster.com");
        memberFormDto.setPassword("12345678");
        memberFormDto.setAddress("경기도 김포시 걸포동");

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    void cascadeTest() {
        Order order = new Order();
        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        orderRepository.saveAndFlush(order);
        em.clear();

        Order findOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(findOrder.getOrderItems()).hasSize(3);
    }

    public Order createOrder() {
        Order order = new Order();
        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = createMember();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    void orphanRemovalTest() {
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        em.flush();
        em.clear();
        Order findOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(findOrder.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    void lazyLoadingTest() {
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        log.debug("Order class : " + orderItem.getOrder().getClass());
        assertThat(orderItem).isNotNull();

        log.debug("====================================================");
        log.debug("Order date : " + orderItem.getOrder().getOrderDate());
        log.debug("====================================================");
    }
}