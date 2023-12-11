package market.demo.domain.etc;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import market.demo.domain.member.Address;
import market.demo.domain.order.Order;
import market.demo.domain.status.DeliveryStatus;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private String deliveryRequest; //배송 요청

    @OneToOne(mappedBy = "delivery", fetch = LAZY, cascade = CascadeType.ALL)
    private Order order;

    public Delivery() {

    }

    public void updateDeliveryStatus(DeliveryStatus newStatus) {
        this.deliveryStatus = newStatus;
    }

    public Delivery(Order order, Address address, DeliveryStatus deliveryStatus) {
        this.order = order;
        this.address = address;
        this.deliveryStatus = deliveryStatus;
    }
}
