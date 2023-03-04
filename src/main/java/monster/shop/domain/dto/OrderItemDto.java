package monster.shop.domain.dto;

import lombok.Getter;
import lombok.Setter;
import monster.shop.domain.model.OrderItem;

@Getter
@Setter
public class OrderItemDto {

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

    private String itemNm;  // 상품명
    private int count;      // 주문수량
    private int orderPrice; // 주문금액
    private String imgUrl;  // 상품 이미지 경로
}
