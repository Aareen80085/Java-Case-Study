import java.math.BigDecimal;

/**
 * Seeds initial sample data into the system for demonstration.
 * Pre-loads vehicles into HashMap, records into LinkedList, and prices in BigDecimal.
 */
public class DataGenerator {

    public static void populateSampleData(VehicleServiceManager manager) {
        try {
            // 1. Add Sample Spare Parts (prices with BigDecimal)
            Part part1 = new Part("P-OIL-01", "Heavy Duty Engine Oil (5L)", "Fluids", new BigDecimal("55.00"), 40);
            Part part2 = new Part("P-FLT-02", "Synthetic Oil Filter", "Filters", new BigDecimal("25.00"), 50);
            Part part3 = new Part("P-BRK-03", "Front Ceramic Brake Pads", "Brakes", new BigDecimal("110.00"), 20);
            Part part4 = new Part("P-AIR-04", "Engine Air Filter", "Filters", new BigDecimal("35.00"), 30);

            manager.addPart(part1);
            manager.addPart(part2);
            manager.addPart(part3);
            manager.addPart(part4);

            // 2. Add Sample Fleet Vehicles (stored in HashMap)
            Vehicle vehicle1 = new Vehicle();
            vehicle1.registrationNumber = "TRK-101";
            vehicle1.brand = "Volvo";
            vehicle1.model = "FH16 Heavy Hauler";
            vehicle1.year = 2021;
            vehicle1.vehicleType = "Truck";
            vehicle1.currentMileage = 82000;
            vehicle1.serviceInterval = 15000;
            vehicle1.nextServiceMileage = 80000; // Service Due (82000 >= 80000)
            manager.addVehicle(vehicle1);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.registrationNumber = "VAN-202";
            vehicle2.brand = "Ford";
            vehicle2.model = "Transit Cargo";
            vehicle2.year = 2022;
            vehicle2.vehicleType = "Van";
            vehicle2.currentMileage = 28500;
            vehicle2.serviceInterval = 10000;
            vehicle2.nextServiceMileage = 30000; // Active (28500 < 30000)
            manager.addVehicle(vehicle2);

            Vehicle vehicle3 = new Vehicle();
            vehicle3.registrationNumber = "CAR-303";
            vehicle3.brand = "Toyota";
            vehicle3.model = "Hilux Utility 4WD";
            vehicle3.year = 2023;
            vehicle3.vehicleType = "Car";
            vehicle3.currentMileage = 16200;
            vehicle3.serviceInterval = 8000;
            vehicle3.nextServiceMileage = 16000; // Service Due (16200 >= 16000)
            manager.addVehicle(vehicle3);

            Vehicle vehicle4 = new Vehicle();
            vehicle4.registrationNumber = "BUS-404";
            vehicle4.brand = "Mercedes-Benz";
            vehicle4.model = "Tourismo Coach";
            vehicle4.year = 2020;
            vehicle4.vehicleType = "Bus";
            vehicle4.currentMileage = 115000;
            vehicle4.serviceInterval = 12000;
            vehicle4.nextServiceMileage = 120000; // Active
            manager.addVehicle(vehicle4);

            // 3. Configure realistic sample service details for each vehicle (no duplicates)
            ServiceRecord record1 = vehicle1.serviceHistory.getLast();
            record1.serviceType = "Oil & Filter Change";
            record1.serviceDate = "2026-03-15";
            record1.laborCost = new BigDecimal("150.00");
            record1.partsCost = new BigDecimal("80.00");
            record1.totalCost = manager.calculateRecordTotal(record1);
            record1.notes = "Replaced 10L oil and filter. Everything normal.";

            ServiceRecord record2 = vehicle2.serviceHistory.getLast();
            record2.serviceType = "Brake Inspection & Pad Replacement";
            record2.serviceDate = "2026-06-20";
            record2.laborCost = new BigDecimal("120.00");
            record2.partsCost = new BigDecimal("110.00");
            record2.totalCost = manager.calculateRecordTotal(record2);
            record2.notes = "Front pads replaced with ceramic pads.";

            ServiceRecord record3 = vehicle3.serviceHistory.getLast();
            record3.serviceType = "Scheduled Maintenance";
            record3.serviceDate = "2026-08-10";
            record3.laborCost = new BigDecimal("90.00");
            record3.partsCost = new BigDecimal("60.00");
            record3.totalCost = manager.calculateRecordTotal(record3);
            record3.notes = "Fluids topped up and safety inspection completed.";

            ServiceRecord record4 = vehicle4.serviceHistory.getLast();
            record4.serviceType = "Annual Roadworthiness & AC Inspection";
            record4.serviceDate = "2026-09-01";
            record4.laborCost = new BigDecimal("200.00");
            record4.partsCost = new BigDecimal("150.00");
            record4.totalCost = manager.calculateRecordTotal(record4);
            record4.notes = "Full HVAC filter replacement and annual safety check.";

        } catch (Exception e) {
            System.err.println("Sample data initialization error: " + e.getMessage());
        }
    }
}
