package market.demo.controller.item;

import lombok.RequiredArgsConstructor;
import market.demo.domain.type.PromotionType;
import market.demo.dto.item.ItemDto;
import market.demo.dto.item.ItemMainEndDto;
import market.demo.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //11 메인 반응 신상품
    @GetMapping("/recent")
    public List<ItemDto> getRecentProducts(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return itemService.getRecentProducts(page, size);
    }

    //9 메인 마감세일
    @GetMapping("/EndOfSeason")
    public List<ItemMainEndDto> getEndOfSeasonSaleItems(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemByPromotionType(PromotionType.END_OF_SEASON_SALE, page, size);
    }
}
