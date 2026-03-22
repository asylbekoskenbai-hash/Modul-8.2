public class Main {
    public static void main(String[] args) {
        System.out.println("=== Декоратор: система отчетов ===\n");
        
        IReport salesReport = new SalesReport();
        System.out.println("Базовый отчет по продажам:");
        System.out.println(salesReport.generate());
        System.out.println();

        IReport decoratedReport = new DateFilterDecorator(salesReport, "2025-01-01", "2025-12-31");
        decoratedReport = new SortingDecorator(decoratedReport, "date");
        decoratedReport = new CsvExportDecorator(decoratedReport);
        System.out.println("Отчет по продажам с фильтром по датам, сортировкой и экспортом в CSV:");
        System.out.println(decoratedReport.generate());
        System.out.println();

        IReport userReport = new UserReport();
        userReport = new FilterByUserTypeDecorator(userReport, "premium");
        userReport = new PdfExportDecorator(userReport);
        System.out.println("Отчет по пользователям (только premium) с экспортом в PDF:");
        System.out.println(userReport.generate());
        System.out.println();

        IReport amountFiltered = new SalesReport();
        amountFiltered = new FilterByAmountDecorator(amountFiltered, 500.0);
        System.out.println("Отчет по продажам с фильтром по сумме > 500:");
        System.out.println(amountFiltered.generate());
        System.out.println();

        System.out.println("=== Адаптер: логистические службы ===\n");

        DeliveryServiceFactory factory = new DeliveryServiceFactory();

        IInternalDeliveryService internal = factory.getDeliveryService("internal");
        internal.deliverOrder("ORD-001");
        System.out.println("Статус: " + internal.getDeliveryStatus("ORD-001"));
        System.out.println("Стоимость доставки: $" + internal.calculateDeliveryCost("ORD-001", 2.5));
        System.out.println();

        IInternalDeliveryService serviceA = factory.getDeliveryService("externalA");
        serviceA.deliverOrder("ORD-002");
        System.out.println("Статус: " + serviceA.getDeliveryStatus("ORD-002"));
        System.out.println("Стоимость доставки: $" + serviceA.calculateDeliveryCost("ORD-002", 3.0));
        System.out.println();

        IInternalDeliveryService serviceB = factory.getDeliveryService("externalB");
        serviceB.deliverOrder("PKG-123");
        System.out.println("Статус: " + serviceB.getDeliveryStatus("PKG-123"));
        System.out.println("Стоимость доставки: $" + serviceB.calculateDeliveryCost("PKG-123", 1.8));
        System.out.println();

        IInternalDeliveryService serviceC = factory.getDeliveryService("externalC");
        serviceC.deliverOrder("REF-456");
        System.out.println("Статус: " + serviceC.getDeliveryStatus("REF-456"));
        System.out.println("Стоимость доставки: $" + serviceC.calculateDeliveryCost("REF-456", 4.2));
        System.out.println();

        try {
            IInternalDeliveryService unknown = factory.getDeliveryService("unknown");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}

interface IReport {
    String generate();
}

class SalesReport implements IReport {
    @Override
    public String generate() {
        return "Отчет по продажам:\n" +
                "1. Товар A - 100.0, дата 2025-02-10\n" +
                "2. Товар B - 600.0, дата 2025-01-15\n" +
                "3. Товар C - 300.0, дата 2025-03-05\n" +
                "4. Товар D - 800.0, дата 2025-04-20\n";
    }
}

class UserReport implements IReport {
    @Override
    public String generate() {
        return "Отчет по пользователям:\n" +
                "1. user1 (обычный)\n" +
                "2. user2 (premium)\n" +
                "3. user3 (premium)\n" +
                "4. user4 (обычный)\n";
    }
}

abstract class ReportDecorator implements IReport {
    protected IReport wrappedReport;

    public ReportDecorator(IReport report) {
        this.wrappedReport = report;
    }

    @Override
    public String generate() {
        return wrappedReport.generate();
    }
}

class DateFilterDecorator extends ReportDecorator {
    private String startDate;
    private String endDate;

    public DateFilterDecorator(IReport report, String start, String end) {
        super(report);
        this.startDate = start;
        this.endDate = end;
    }

    @Override
    public String generate() {
        String original = wrappedReport.generate();
        return original + "[Фильтр по датам: " + startDate + " - " + endDate + "]\n";
    }
}

class SortingDecorator extends ReportDecorator {
    private String criterion;

    public SortingDecorator(IReport report, String criterion) {
        super(report);
        this.criterion = criterion;
    }

    @Override
    public String generate() {
        String original = wrappedReport.generate();
        return original + "[Сортировка по: " + criterion + "]\n";
    }
}

class CsvExportDecorator extends ReportDecorator {
    public CsvExportDecorator(IReport report) {
        super(report);
    }

    @Override
    public String generate() {
        String original = wrappedReport.generate();
        return original + "[Экспорт в CSV: данные преобразованы в CSV-формат]\n";
    }
}

class PdfExportDecorator extends ReportDecorator {
    public PdfExportDecorator(IReport report) {
        super(report);
    }

    @Override
    public String generate() {
        String original = wrappedReport.generate();
        return original + "[Экспорт в PDF: создан PDF-документ]\n";
    }
}

class FilterByAmountDecorator extends ReportDecorator {
    private double minAmount;

    public FilterByAmountDecorator(IReport report, double minAmount) {
        super(report);
        this.minAmount = minAmount;
    }

    @Override
    public String generate() {
        String original = wrappedReport.generate();
        return original + "[Фильтр по сумме: > " + minAmount + "]\n";
    }
}

class FilterByUserTypeDecorator extends ReportDecorator {
    private String userType;

    public FilterByUserTypeDecorator(IReport report, String userType) {
        super(report);
        this.userType = userType;
    }

    @Override
    public String generate() {
        String original = wrappedReport.generate();
        return original + "[Фильтр по типу пользователя: " + userType + "]\n";
    }
}

interface IInternalDeliveryService {
    void deliverOrder(String orderId);
    String getDeliveryStatus(String orderId);
    double calculateDeliveryCost(String orderId, double weight);
}

class InternalDeliveryService implements IInternalDeliveryService {
    @Override
    public void deliverOrder(String orderId) {
        System.out.println("[Внутренняя служба] Доставка заказа " + orderId);
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        return "Внутренняя служба: заказ " + orderId + " доставлен";
    }

    @Override
    public double calculateDeliveryCost(String orderId, double weight) {
        return weight * 2.0;
    }
}

class ExternalLogisticsServiceA {
    public void shipItem(int itemId) {
        System.out.println("[ExternalServiceA] Отправка товара с ID: " + itemId);
    }
    public String trackShipment(int shipmentId) {
        return "ExternalServiceA: отправка " + shipmentId + " в пути";
    }
    public double calculateCost(int itemId, double weight) {
        return weight * 3.5;
    }
}

class LogisticsAdapterA implements IInternalDeliveryService {
    private ExternalLogisticsServiceA service;

    public LogisticsAdapterA(ExternalLogisticsServiceA service) {
        this.service = service;
    }

    @Override
    public void deliverOrder(String orderId) {
        try {
            int id = Integer.parseInt(orderId.replaceAll("\\D", ""));
            service.shipItem(id);
            System.out.println("[LogisticsAdapterA] Заказ " + orderId + " передан в ExternalServiceA");
        } catch (Exception e) {
            System.err.println("[LogisticsAdapterA] Ошибка доставки: " + e.getMessage());
        }
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        try {
            int id = Integer.parseInt(orderId.replaceAll("\\D", ""));
            return service.trackShipment(id);
        } catch (Exception e) {
            return "[LogisticsAdapterA] Не удалось получить статус: " + e.getMessage();
        }
    }

    @Override
    public double calculateDeliveryCost(String orderId, double weight) {
        try {
            int id = Integer.parseInt(orderId.replaceAll("\\D", ""));
            return service.calculateCost(id, weight);
        } catch (Exception e) {
            return -1;
        }
    }
}

class ExternalLogisticsServiceB {
    public void sendPackage(String packageInfo) {
        System.out.println("[ExternalServiceB] Отправка посылки: " + packageInfo);
    }
    public String checkPackageStatus(String trackingCode) {
        return "ExternalServiceB: статус посылки " + trackingCode + " - доставлена";
    }
    public double calculateShippingCost(String packageInfo, double weight) {
        return weight * 2.8;
    }
}

class LogisticsAdapterB implements IInternalDeliveryService {
    private ExternalLogisticsServiceB service;

    public LogisticsAdapterB(ExternalLogisticsServiceB service) {
        this.service = service;
    }

    @Override
    public void deliverOrder(String orderId) {
        try {
            service.sendPackage("Заказ: " + orderId);
            System.out.println("[LogisticsAdapterB] Заказ " + orderId + " передан в ExternalServiceB");
        } catch (Exception e) {
            System.err.println("[LogisticsAdapterB] Ошибка доставки: " + e.getMessage());
        }
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        try {
            return service.checkPackageStatus(orderId);
        } catch (Exception e) {
            return "[LogisticsAdapterB] Не удалось получить статус: " + e.getMessage();
        }
    }

    @Override
    public double calculateDeliveryCost(String orderId, double weight) {
        try {
            return service.calculateShippingCost("Заказ: " + orderId, weight);
        } catch (Exception e) {
            return -1;
        }
    }
}

class ExternalLogisticsServiceC {
    public void submitDelivery(String orderRef) {
        System.out.println("[ExternalServiceC] Отправка заказа: " + orderRef);
    }
    public String getDeliveryInfo(String ref) {
        return "ExternalServiceC: заказ " + ref + " выполнен";
    }
    public double getDeliveryPrice(String orderRef, double weight) {
        return weight * 4.0;
    }
}

class LogisticsAdapterC implements IInternalDeliveryService {
    private ExternalLogisticsServiceC service;

    public LogisticsAdapterC(ExternalLogisticsServiceC service) {
        this.service = service;
    }

    @Override
    public void deliverOrder(String orderId) {
        try {
            service.submitDelivery(orderId);
            System.out.println("[LogisticsAdapterC] Заказ " + orderId + " передан в ExternalServiceC");
        } catch (Exception e) {
            System.err.println("[LogisticsAdapterC] Ошибка доставки: " + e.getMessage());
        }
    }

    @Override
    public String getDeliveryStatus(String orderId) {
        try {
            return service.getDeliveryInfo(orderId);
        } catch (Exception e) {
            return "[LogisticsAdapterC] Не удалось получить статус: " + e.getMessage();
        }
    }

    @Override
    public double calculateDeliveryCost(String orderId, double weight) {
        try {
            return service.getDeliveryPrice(orderId, weight);
        } catch (Exception e) {
            return -1;
        }
    }
}

class DeliveryServiceFactory {
    public IInternalDeliveryService getDeliveryService(String type) {
        switch (type.toLowerCase()) {
            case "internal":
                return new InternalDeliveryService();
            case "externala":
                return new LogisticsAdapterA(new ExternalLogisticsServiceA());
            case "externalb":
                return new LogisticsAdapterB(new ExternalLogisticsServiceB());
            case "externalc":
                return new LogisticsAdapterC(new ExternalLogisticsServiceC());
            default:
                throw new IllegalArgumentException("Неизвестный тип службы доставки: " + type);
        }
    }
}