package market.demo.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import market.demo.domain.item.ItemTestDataCreator;
import market.demo.domain.member.MemberTestDataCreator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationTestDataInitializer {

    private final MemberTestDataCreator memberTestDataCreator;
    private final ItemTestDataCreator itemTestDataCreator;

    @PostConstruct
    public void init() {
        memberTestDataCreator.init();
        itemTestDataCreator.init();
    }
}
