package market.demo.controller.order;

import lombok.RequiredArgsConstructor;
import market.demo.dto.cart.CartDto;
import market.demo.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.lang.Long.valueOf;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    //23번 장바구니
    @PostMapping("/insert/{loginId}")
    public ResponseEntity<String> insertCart (@PathVariable("loginId") String loginId,
                                              @RequestParam Long itemId){
        cartService.insertCart(loginId, itemId);
        return ResponseEntity.ok("장바구니 아이템 추가되었습니다.");
    }

    @PostMapping("/delete/{loginId}")
    public ResponseEntity<String> deleteCart (@PathVariable("loginId") String loginId,
                                              @RequestParam Long itemId){
        cartService.deleteCart(loginId, itemId);
        return ResponseEntity.ok("장바구니 아이템 삭제되었습니다.");
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<CartDto> showCart (@PathVariable("loginId") String loginId){
        return ResponseEntity.ok(cartService.showCart(loginId));
    }

    @PostMapping("/item/add")
    public ResponseEntity<String> addCartItem (@RequestParam Long cartId, Long itemId){
        cartService.changeCartItem(cartId, itemId, 1);
        return ResponseEntity.ok("아이템 개수 추가 성공");
    }

   @PostMapping("/item/minus")
    public ResponseEntity<String> minusCartItem (@RequestParam Long cartId, Long itemId){
        cartService.changeCartItem(cartId, itemId, -1 );
        return ResponseEntity.ok("아이템 개수 감소 성공");
    }
}
