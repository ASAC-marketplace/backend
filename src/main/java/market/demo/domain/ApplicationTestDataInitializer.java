package market.demo.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import market.demo.domain.item.ItemTestDataCreator;
import market.demo.domain.member.MemberTestDataCreator;
import market.demo.domain.order.OrderTestDataCreator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationTestDataInitializer {

    private final MemberTestDataCreator memberTestDataCreator;
    private final ItemTestDataCreator itemTestDataCreator;
    private final OrderTestDataCreator orderTestDataCreator;

    @PostConstruct
    public void init() {
        memberTestDataCreator.init();
        itemTestDataCreator.init();
        orderTestDataCreator.init();
    }
}
