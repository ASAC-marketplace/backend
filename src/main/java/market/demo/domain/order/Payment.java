package market.demo.domain.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.demo.domain.order.Order;
import market.demo.domain.status.PaymentMethod;
import market.demo.domain.type.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "total_price")
    private Long totalPrice;

    public Payment(Order order, Long totalPrice, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        this.order = order;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    public void completePayment(String transactionId) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
    }

    public void updatePaymentStatus(PaymentStatus newStatus) {
        if (paymentStatus != null) paymentStatus = newStatus;
    }
}
