package market.demo.service.inquiry;

public class InquiryNotFoundException extends RuntimeException {
    public InquiryNotFoundException(String message) {
        super(message);
    }
}
