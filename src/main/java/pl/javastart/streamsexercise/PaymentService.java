package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

class PaymentService {

    private PaymentRepository paymentRepository;
    private DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    List<Payment> findPaymentsSortedByDateDesc() {
        PaymentDateTimeComparator comparator = new PaymentDateTimeComparator();
        return paymentRepository.findAll().stream()
                .sorted(comparator)
                .collect(toList());
    }

    List<Payment> findPaymentsForCurrentMonth() {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentDate().getYear() == dateTimeProvider.yearMonthNow().getYear())
                .filter(payment -> payment.getPaymentDate().getMonth() == dateTimeProvider.yearMonthNow().getMonth())
                .collect(toList());
    }

    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentDate().getYear() == yearMonth.getYear())
                .filter(payment -> payment.getPaymentDate().getMonth() == yearMonth.getMonth())
                .collect(toList());
    }

    List<Payment> findPaymentsForGivenLastDays(int days) {
        return paymentRepository.findAll().stream()
                .filter(payment -> (dateTimeProvider.zonedDateTimeNow().minusDays(days).isBefore(payment.getPaymentDate())))
                .collect(toList());
    }

    Set<Payment> findPaymentsWithOnePaymentItem() {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentItems().size() == 1)
                .collect(toSet());
    }

    Set<String> findProductsSoldInCurrentMonth() {
        Set<String> namesSet = new HashSet<>();
        paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentDate().getYear() == dateTimeProvider.yearMonthNow().getYear())
                .filter(payment -> payment.getPaymentDate().getMonth() == dateTimeProvider.yearMonthNow().getMonth())
                .map(Payment::getPaymentItems)
                .forEach(paymentItems -> paymentItems
                        .forEach(paymentItem -> namesSet.add(paymentItem.getName())));

        return namesSet;
    }

    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        BigDecimal sum = new BigDecimal(0);
        List<PaymentItem> items = new ArrayList<>();

        paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentDate().getYear() == yearMonth.getYear())
                .filter(payment -> payment.getPaymentDate().getMonth() == yearMonth.getMonth())
                .map(Payment::getPaymentItems)
                .forEach(items::addAll);

        for (PaymentItem item : items) {
            sum = sum.add(item.getFinalPrice());
        }
        return sum;
    }

    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        BigDecimal discountSum = new BigDecimal(0);
        List<PaymentItem> items = new ArrayList<>();

        paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentDate().getYear() == yearMonth.getYear())
                .filter(payment -> payment.getPaymentDate().getMonth() == yearMonth.getMonth())
                .map(Payment::getPaymentItems)
                .forEach(items::addAll);

        for (PaymentItem item : items) {
            discountSum = discountSum.add(item.getRegularPrice().subtract(item.getFinalPrice()));
        }
        return discountSum;
    }

    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {
        List<PaymentItem> userItems = new ArrayList<>();

        paymentRepository.findAll().stream()
                .filter(payment -> payment.getUser().getEmail().equals(userEmail))
                .forEach(payment -> userItems.addAll(payment.getPaymentItems()));

        return userItems;
    }

    Set<Payment> findPaymentsWithValueOver(int value) {
        Set<Payment> paymentSet = new HashSet<>();
        List<Payment> paymentItemsList = new ArrayList<>(paymentRepository.findAll());

        for (Payment payment : paymentItemsList) {
            BigDecimal sumTotal = new BigDecimal(0);

            for (PaymentItem paymentItem : payment.getPaymentItems()) {
                sumTotal = sumTotal.add(paymentItem.getFinalPrice());
            }

            if (sumTotal.compareTo(BigDecimal.valueOf(value)) > 0) {
                paymentSet.add(payment);
            }
        }
        return paymentSet;
    }

}
