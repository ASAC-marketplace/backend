package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.order.Order;
import market.demo.domain.type.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payment")
public class Payment {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "total_price")
    private Long totalPrice;
}
