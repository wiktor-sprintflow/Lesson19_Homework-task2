package pl.javastart.streamsexercise;

import java.util.Comparator;

public class PaymentDateTimeComparator implements Comparator<Payment> {
    @Override
    public int compare(Payment p1, Payment p2) {
        if (p1.getPaymentDate().compareTo(p2.getPaymentDate()) > 0)
            return -1;
        else if (p1.getPaymentDate().compareTo(p2.getPaymentDate()) < 0)
            return 1;
        else return 0;
    }
}
