package market.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import market.demo.domain.status.DeliveryStatus;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @Embedded
    private Address address;

    private DeliveryStatus deliveryStatus;

    private String deliveryRequest; //배송 요청

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;
}
