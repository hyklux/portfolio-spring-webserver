package monster.shop.domain.dto;

import lombok.Getter;
import lombok.Setter;
import monster.shop.domain.constant.ItemSellStatus;


@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;
    private ItemSellStatus searchSellStatus;
    private String searchBy;
    private String searchQuery = "";
}
