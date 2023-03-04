package monster.shop.domain.repository;

import monster.shop.domain.dto.ItemSearchDto;
import monster.shop.domain.dto.MainItemDto;
import monster.shop.domain.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
