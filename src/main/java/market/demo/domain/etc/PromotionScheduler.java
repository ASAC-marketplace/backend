package market.demo.domain.etc;

import lombok.RequiredArgsConstructor;
import market.demo.domain.type.PromotionType;
import market.demo.service.ItemService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionScheduler {

    private final ItemService itemService;

    //매주 금요일 자정에 실행
    @Scheduled(cron = "0 0 0 * * FRI")
    public void applyWeekendSpecial() {
        List<Long> itemIds = itemService.findRandomItemIds(10);
        itemService.updatePromotionTypeForItems(itemIds, PromotionType.WEEKEND_SPECIAL);
    }

    // 매주 일요일 자정에 실행
    @Scheduled(cron = "0 0 0 * * SUN")
    public void removeWeekendSpecial() {
        itemService.resetPromotionType(PromotionType.WEEKEND_SPECIAL);
    }
}
