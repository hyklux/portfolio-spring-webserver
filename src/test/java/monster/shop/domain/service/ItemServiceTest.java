package monster.shop.domain.service;

import monster.shop.domain.constant.ItemSellStatus;
import monster.shop.domain.dto.ItemFormDto;
import monster.shop.domain.model.Item;
import monster.shop.domain.model.ItemImg;
import monster.shop.domain.repository.ItemImgRepository;
import monster.shop.domain.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws Exception {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = "C:/shop/item";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(multipartFile);
        }
        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception {
        ItemFormDto dto = new ItemFormDto();
        dto.setItemNm("테스트 상품");
        dto.setItemSellStatus(ItemSellStatus.SELL);
        dto.setItemDetail("테스트 상품 입니다.");
        dto.setPrice(1000);
        dto.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(dto, multipartFileList);

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        assertThat(dto.getItemNm()).as("item not match name").isEqualTo(item.getItemNm());
        assertThat(dto.getItemSellStatus()).as("item not match status").isEqualTo(item.getItemSellStatus());
        assertThat(dto.getItemDetail()).as("item not match detail").isEqualTo(item.getItemDetail());
        assertThat(dto.getPrice()).as("item not match price").isEqualTo(item.getPrice());
        assertThat(dto.getStockNumber()).as("item not match stock count").isEqualTo(item.getStockNumber());
        assertThat(multipartFileList.get(0).getOriginalFilename()).as("item not match file name").isEqualTo(itemImgList.get(0).getOriImgName());
    }
}