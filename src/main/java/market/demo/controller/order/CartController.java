package market.demo.controller.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.demo.domain.member.jwt.TokenProvider;
import market.demo.dto.cart.CartDto;
import market.demo.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.lang.Long.valueOf;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final TokenProvider tokenProvider;

    //23번 장바구니
    @PostMapping("/insert")
    public ResponseEntity<String> insertItemIntoCart(@RequestParam Long itemId) {
        String loginId = tokenProvider.getLoginIdFromCurrentRequest(); // JWT에서 loginId 추출
        cartService.insertCart(loginId, itemId);
        return ResponseEntity.ok("장바구니 아이템 추가되었습니다.");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteItemFromCart (
                                              @RequestParam Long itemId){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest(); // JWT에서 loginId 추출
        log.info("loginId = {}", loginId);
        cartService.deleteCart(loginId, itemId);
        return ResponseEntity.ok("장바구니 아이템 삭제되었습니다.");
    }

    @GetMapping()
    public ResponseEntity<CartDto> showCart (){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        return ResponseEntity.ok(cartService.showCart(loginId));
    }

    @PostMapping("/item/add")
    public ResponseEntity<String> addCartItem (@RequestParam Long itemId){
        String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        cartService.changeCartItem(loginId, itemId, 1);
        return ResponseEntity.ok("아이템 개수 추가 성공");
    }

   @PostMapping("/item/minus")
    public ResponseEntity<String> minusCartItem (@RequestParam Long itemId){
       String loginId = tokenProvider.getLoginIdFromCurrentRequest();
        cartService.changeCartItem(loginId, itemId, -1 );
        return ResponseEntity.ok("아이템 개수 감소 성공");
    }
}
