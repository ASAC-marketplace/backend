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

    //배너 추가
    @GetMapping("/recent")
    public List<ItemDto> getRecentProducts(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return itemService.getRecentProducts(page, size);
    }

    //9 메인 마감세일
    @GetMapping("/endofseason")
    public List<ItemMainEndDto> getEndOfSeasonSaleItems(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemByPromotionType(PromotionType.END_OF_SEASON_SALE, page, size);
    }

    //10 메인 주말특가
    @GetMapping("/weekend")
    public List<ItemMainEndDto> getWeekendItems(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemByPromotionType(PromotionType.WEEKEND_SPECIAL, page, size);
    }
}
