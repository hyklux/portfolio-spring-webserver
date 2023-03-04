package monster.shop.domain.service;

import monster.shop.domain.constant.ItemSellStatus;
import monster.shop.domain.constant.OrderStatus;
import monster.shop.domain.dto.OrderDto;
import monster.shop.domain.model.Item;
import monster.shop.domain.model.Member;
import monster.shop.domain.model.Order;
import monster.shop.domain.model.OrderItem;
import monster.shop.domain.repository.ItemRepository;
import monster.shop.domain.repository.MemberRepository;
import monster.shop.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;

    public Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(15000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember() {
        Member member = new Member();
        member.setEmail("tingcobell@nm-monster.com");
        member.setName("윤일");
        member.setPassword("1234");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    void orderTest() {
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        System.out.println("member = " + member);

        Long orderId = orderService.order(orderDto, member.getEmail());
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();
        int totalPrice = orderDto.getCount() * item.getPrice();
        assertThat(totalPrice).isEqualTo(order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    void cancelOrder() {
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        Long orderId = orderService.order(orderDto, member.getEmail());
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        assertThat(item.getStockNumber()).isEqualTo(90);

        orderService.cancelOrder(orderId);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockNumber()).isEqualTo(100);
    }
}