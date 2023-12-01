package market.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.dto.itemdetailinfo.ItemDetailDto;
import market.demo.dto.itemdetailinfo.ItemReviewsDto;
import market.demo.service.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    //17 api 상품 상세 정보
    @GetMapping
    public ItemDetailDto showItemDetail(@RequestParam Long itemId) {
        return itemService.searchItemDetail(itemId);
    }

    @GetMapping("/reviews")
    public ItemReviewsDto showItemReviews(@RequestParam Long itemId){
        return itemService.searchItemReview(itemId);
    }


}
