package monster.shop.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    @GetMapping(value = "/list")
    public String shopList(Model model) {
        return "shop";
    }
}
