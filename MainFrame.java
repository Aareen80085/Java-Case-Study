import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Main Graphical User Interface (GUI) for the Vehicle Service System.
 * Clean, readable Swing layout with 4 tabs:
 * 1. Fleet Vehicles
 * 2. Record Service
 * 3. Spare Parts Inventory
 * 4. Fleet Reports & Summary
 */
public class MainFrame extends JFrame {

    public VehicleServiceManager vehicleServiceManager;
    public ReportService reportService;

    // Table Models for storing and displaying rows in GUI tables
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel serviceHistoryTableModel;
    private DefaultTableModel sparePartTableModel;

    // Summary Metric Labels displayed in the Reports Tab
    private JLabel totalVehiclesValueLabel;
    private JLabel totalServicesValueLabel;
    private JLabel vehiclesDueValueLabel;
    private JLabel totalMaintenanceSpendValueLabel;
    private JTextArea reportDisplayTextArea;

    /**
     * Initializes and displays the main application window.
     */
    public void startApplication(VehicleServiceManager serviceManager) {
        this.vehicleServiceManager = serviceManager;
        this.reportService = new ReportService();

        setTitle("Vehicle Service Record System (B.Tech CSE Semester III)");
        setSize(980, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers window on screen

        JTabbedPane navigationTabs = new JTabbedPane();
        navigationTabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        navigationTabs.addTab("  Fleet Vehicles  ", buildFleetVehiclesPanel());
        navigationTabs.addTab("  Record Service  ", buildRecordServicePanel());
        navigationTabs.addTab("  Spare Parts Inventory  ", buildSparePartsPanel());
        navigationTabs.addTab("  Fleet Reports & Summary  ", buildFleetReportsPanel());

        add(navigationTabs);

        // Load initial data into tables and labels
        updateVehicleTableData();
        updateServiceHistoryTableData();
        updateSparePartTableData();
        updateSummaryDashboardStats();

        setVisible(true);
    }

    /* =========================================================================
     * TAB 1: FLEET VEHICLES
     * ========================================================================= */
    private JPanel buildFleetVehiclesPanel() {
        JPanel containerPanel = new JPanel(new BorderLayout(10, 10));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Form on the left to add a new vehicle
        JPanel addVehicleFormPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        addVehicleFormPanel.setBorder(BorderFactory.createTitledBorder("Add New Vehicle"));
        addVehicleFormPanel.setPreferredSize(new Dimension(340, 0));

        JTextField registrationInputField = new JTextField();
        JTextField brandInputField = new JTextField();
        JTextField modelInputField = new JTextField();
        JTextField manufacturingYearInputField = new JTextField("2023");
        JComboBox<String> vehicleTypeDropdown = new JComboBox<>(new String[]{"Car", "Truck", "Van", "Bus"});
        JTextField currentMileageInputField = new JTextField("10000");
        JTextField serviceIntervalInputField = new JTextField("10000");

        addVehicleFormPanel.add(new JLabel("Registration Number:"));
        addVehicleFormPanel.add(registrationInputField);

        addVehicleFormPanel.add(new JLabel("Brand / Make:"));
        addVehicleFormPanel.add(brandInputField);

        addVehicleFormPanel.add(new JLabel("Model Name:"));
        addVehicleFormPanel.add(modelInputField);

        addVehicleFormPanel.add(new JLabel("Manufacturing Year:"));
        addVehicleFormPanel.add(manufacturingYearInputField);

        addVehicleFormPanel.add(new JLabel("Vehicle Type:"));
        addVehicleFormPanel.add(vehicleTypeDropdown);

        addVehicleFormPanel.add(new JLabel("Current Mileage (km):"));
        addVehicleFormPanel.add(currentMileageInputField);

        addVehicleFormPanel.add(new JLabel("Service Interval (km):"));
        addVehicleFormPanel.add(serviceIntervalInputField);

        JButton addVehicleButton = new JButton("Add Vehicle");
        addVehicleButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        addVehicleButton.setBackground(new Color(40, 167, 69));
        addVehicleButton.setForeground(Color.WHITE);

        addVehicleFormPanel.add(new JLabel("")); // Empty cell for alignment
        addVehicleFormPanel.add(addVehicleButton);

        // Table on the right to display vehicles
        String[] vehicleTableColumnNames = {
                "Registration Number", "Brand", "Model", "Year", "Type",
                "Current Mileage (km)", "Next Service (km)", "Maintenance Status"
        };
        vehicleTableModel = new DefaultTableModel(vehicleTableColumnNames, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false; // Prevent typing in table
            }
        };

        JTable vehicleTable = new JTable(vehicleTableModel);
        vehicleTable.setRowHeight(24);
        JScrollPane vehicleTableScrollPane = new JScrollPane(vehicleTable);

        containerPanel.add(addVehicleFormPanel, BorderLayout.WEST);
        containerPanel.add(vehicleTableScrollPane, BorderLayout.CENTER);

        // Click handler to add vehicle
        addVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String enteredRegistration = registrationInputField.getText().trim().toUpperCase();
                String enteredBrand = brandInputField.getText().trim();
                String enteredModel = modelInputField.getText().trim();
                String enteredYearText = manufacturingYearInputField.getText().trim();
                String selectedVehicleType = (String) vehicleTypeDropdown.getSelectedItem();
                String enteredMileageText = currentMileageInputField.getText().trim();
                String enteredIntervalText = serviceIntervalInputField.getText().trim();

                if (enteredRegistration.isEmpty() || enteredBrand.isEmpty() || enteredModel.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Please fill in Registration, Brand, and Model!",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int parsedYear = Integer.parseInt(enteredYearText);
                    int parsedCurrentMileage = Integer.parseInt(enteredMileageText);
                    int parsedServiceInterval = Integer.parseInt(enteredIntervalText);

                    Vehicle newVehicle = new Vehicle();
                    newVehicle.registrationNumber = enteredRegistration;
                    newVehicle.brand = enteredBrand;
                    newVehicle.model = enteredModel;
                    newVehicle.year = parsedYear;
                    newVehicle.vehicleType = selectedVehicleType;
                    newVehicle.currentMileage = parsedCurrentMileage;
                    newVehicle.serviceInterval = parsedServiceInterval;
                    newVehicle.nextServiceMileage = parsedCurrentMileage + parsedServiceInterval;

                    // Handled safely with Custom Exception (also creates initial service record)
                    vehicleServiceManager.addVehicle(newVehicle);

                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Vehicle " + enteredRegistration + " added successfully to fleet and logged in service records!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    registrationInputField.setText("");
                    brandInputField.setText("");
                    modelInputField.setText("");

                    updateVehicleTableData();
                    updateServiceHistoryTableData();
                    updateSummaryDashboardStats();

                } catch (DuplicateVehicleException dve) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            dve.getMessage(),
                            "Duplicate Vehicle Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException error) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Year, Current Mileage, and Service Interval must be valid whole numbers!",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return containerPanel;
    }

    /* =========================================================================
     * TAB 2: RECORD SERVICE
     * ========================================================================= */
    private JPanel buildRecordServicePanel() {
        JPanel containerPanel = new JPanel(new BorderLayout(10, 10));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Form on the left
        JPanel recordServiceFormPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        recordServiceFormPanel.setBorder(BorderFactory.createTitledBorder("Log Completed Maintenance"));
        recordServiceFormPanel.setPreferredSize(new Dimension(350, 0));

        JTextField vehicleRegistrationInputField = new JTextField();
        JComboBox<String> serviceTypeDropdown = new JComboBox<>(new String[]{
                "Oil & Filter Change",
                "Brake Inspection & Pads",
                "General Periodic Maintenance",
                "Tire Rotation & Alignment",
                "Engine Tune-Up",
                "Transmission Service"
        });
        JTextField serviceDateInputField = new JTextField(LocalDate.now().toString());
        JTextField mileageAtServiceInputField = new JTextField();
        JTextField laborCostInputField = new JTextField("100.00");
        JTextField partsCostInputField = new JTextField("50.00");
        JTextField technicianNotesInputField = new JTextField();

        recordServiceFormPanel.add(new JLabel("Vehicle Registration:"));
        recordServiceFormPanel.add(vehicleRegistrationInputField);

        recordServiceFormPanel.add(new JLabel("Service Type:"));
        recordServiceFormPanel.add(serviceTypeDropdown);

        recordServiceFormPanel.add(new JLabel("Service Date:"));
        recordServiceFormPanel.add(serviceDateInputField);

        recordServiceFormPanel.add(new JLabel("Mileage at Service (km):"));
        recordServiceFormPanel.add(mileageAtServiceInputField);

        recordServiceFormPanel.add(new JLabel("Labor Cost ($):"));
        recordServiceFormPanel.add(laborCostInputField);

        recordServiceFormPanel.add(new JLabel("Parts Cost ($):"));
        recordServiceFormPanel.add(partsCostInputField);

        recordServiceFormPanel.add(new JLabel("Technician Notes:"));
        recordServiceFormPanel.add(technicianNotesInputField);

        JButton saveServiceRecordButton = new JButton("Save Service Record");
        saveServiceRecordButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        saveServiceRecordButton.setBackground(new Color(0, 123, 255));
        saveServiceRecordButton.setForeground(Color.WHITE);

        recordServiceFormPanel.add(new JLabel(""));
        recordServiceFormPanel.add(saveServiceRecordButton);

        // Table on the right to display service records
        String[] serviceTableColumnNames = {
                "Service ID", "Vehicle Reg", "Service Type", "Date",
                "Mileage (km)", "Labor ($)", "Parts ($)", "Total Cost ($)", "Notes"
        };
        serviceHistoryTableModel = new DefaultTableModel(serviceTableColumnNames, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        JTable serviceHistoryTable = new JTable(serviceHistoryTableModel);
        serviceHistoryTable.setRowHeight(24);
        JScrollPane serviceTableScrollPane = new JScrollPane(serviceHistoryTable);

        containerPanel.add(recordServiceFormPanel, BorderLayout.WEST);
        containerPanel.add(serviceTableScrollPane, BorderLayout.CENTER);

        // Click handler to save service record
        saveServiceRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String enteredRegistration = vehicleRegistrationInputField.getText().trim().toUpperCase();
                String selectedServiceType = (String) serviceTypeDropdown.getSelectedItem();
                String enteredServiceDate = serviceDateInputField.getText().trim();
                String enteredMileageText = mileageAtServiceInputField.getText().trim();
                String enteredLaborCostText = laborCostInputField.getText().trim();
                String enteredPartsCostText = partsCostInputField.getText().trim();
                String enteredTechnicianNotes = technicianNotesInputField.getText().trim();

                if (enteredRegistration.isEmpty() || enteredMileageText.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Vehicle Registration Number and Mileage at Service are required!",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int parsedMileageAtService = Integer.parseInt(enteredMileageText);
                    BigDecimal parsedLaborCost = new BigDecimal(enteredLaborCostText).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal parsedPartsCost = new BigDecimal(enteredPartsCostText).setScale(2, RoundingMode.HALF_UP);

                    ServiceRecord newServiceRecord = new ServiceRecord();
                    newServiceRecord.vehicleRegistrationNumber = enteredRegistration;
                    newServiceRecord.serviceType = selectedServiceType;
                    newServiceRecord.serviceDate = enteredServiceDate;
                    newServiceRecord.mileageAtService = parsedMileageAtService;
                    newServiceRecord.laborCost = parsedLaborCost;
                    newServiceRecord.partsCost = parsedPartsCost;
                    newServiceRecord.notes = enteredTechnicianNotes;

                    // Handled safely with Custom Exceptions
                    vehicleServiceManager.addServiceRecord(newServiceRecord);

                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Service record saved successfully!\nTotal Cost: $" + newServiceRecord.totalCost
                                    + "\nNext service milestone automatically updated.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    vehicleRegistrationInputField.setText("");
                    mileageAtServiceInputField.setText("");
                    technicianNotesInputField.setText("");

                    updateVehicleTableData();
                    updateServiceHistoryTableData();
                    updateSummaryDashboardStats();

                } catch (VehicleNotFoundException vnfe) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            vnfe.getMessage(),
                            "Vehicle Not Found", JOptionPane.ERROR_MESSAGE);
                } catch (InvalidMileageException ime) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            ime.getMessage(),
                            "Invalid Mileage", JOptionPane.WARNING_MESSAGE);
                } catch (NumberFormatException error) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Mileage, Labor Cost, and Parts Cost must be valid numerical values!",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return containerPanel;
    }

    /* =========================================================================
     * TAB 3: SPARE PARTS INVENTORY
     * ========================================================================= */
    private JPanel buildSparePartsPanel() {
        JPanel containerPanel = new JPanel(new BorderLayout(10, 10));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Form on the left to add a spare part (consistent with Fleet and Service tabs)
        JPanel addPartFormPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        addPartFormPanel.setBorder(BorderFactory.createTitledBorder("Add Spare Part to Catalog"));
        addPartFormPanel.setPreferredSize(new Dimension(340, 0));

        JTextField partNumberInputField = new JTextField();
        JTextField partNameInputField = new JTextField();
        JTextField categoryInputField = new JTextField();
        JTextField unitPriceInputField = new JTextField();
        JTextField stockQuantityInputField = new JTextField("0");
        JButton addSparePartButton = new JButton("Add Part");
        addSparePartButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        addSparePartButton.setBackground(new Color(40, 167, 69));
        addSparePartButton.setForeground(Color.WHITE);

        addPartFormPanel.add(new JLabel("Part Number:"));
        addPartFormPanel.add(partNumberInputField);

        addPartFormPanel.add(new JLabel("Part Name:"));
        addPartFormPanel.add(partNameInputField);

        addPartFormPanel.add(new JLabel("Category:"));
        addPartFormPanel.add(categoryInputField);

        addPartFormPanel.add(new JLabel("Unit Price ($):"));
        addPartFormPanel.add(unitPriceInputField);

        addPartFormPanel.add(new JLabel("Stock Quantity:"));
        addPartFormPanel.add(stockQuantityInputField);

        addPartFormPanel.add(new JLabel("")); // Spacer
        addPartFormPanel.add(addSparePartButton);

        // Table in center to display spare parts
        String[] partTableColumnNames = {"Part Number", "Part Name", "Category", "Unit Price ($)", "Stock Quantity"};
        sparePartTableModel = new DefaultTableModel(partTableColumnNames, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        JTable sparePartsTable = new JTable(sparePartTableModel);
        sparePartsTable.setRowHeight(24);
        JScrollPane sparePartsScrollPane = new JScrollPane(sparePartsTable);

        containerPanel.add(addPartFormPanel, BorderLayout.WEST);
        containerPanel.add(sparePartsScrollPane, BorderLayout.CENTER);

        // Click handler to add a spare part
        addSparePartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String enteredPartNumber = partNumberInputField.getText().trim();
                String enteredPartName = partNameInputField.getText().trim();
                String enteredCategory = categoryInputField.getText().trim();
                String enteredPriceText = unitPriceInputField.getText().trim();
                String enteredStockText = stockQuantityInputField.getText().trim();

                if (enteredPartNumber.isEmpty() || enteredPartName.isEmpty() || enteredPriceText.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Please enter Part Number, Part Name, and Unit Price!",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    BigDecimal parsedPrice = new BigDecimal(enteredPriceText).setScale(2, RoundingMode.HALF_UP);
                    int parsedStockQuantity = enteredStockText.isEmpty() ? 0 : Integer.parseInt(enteredStockText);

                    Part newSparePart = new Part(enteredPartNumber, enteredPartName, enteredCategory, parsedPrice, parsedStockQuantity);
                    vehicleServiceManager.addPart(newSparePart);
                    updateSparePartTableData();

                    partNumberInputField.setText("");
                    partNameInputField.setText("");
                    unitPriceInputField.setText("");
                    stockQuantityInputField.setText("");
                } catch (NumberFormatException error) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Price and Stock Quantity must be valid numbers!",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return containerPanel;
    }

    /* =========================================================================
     * TAB 4: FLEET REPORTS & SUMMARY
     * ========================================================================= */
    private JPanel buildFleetReportsPanel() {
        JPanel containerPanel = new JPanel(new BorderLayout(10, 10));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top 4 metric summary cards
        JPanel metricsDashboardPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        metricsDashboardPanel.setPreferredSize(new Dimension(0, 70));

        totalVehiclesValueLabel = createDashboardMetricCard("Total Vehicles", "0", metricsDashboardPanel);
        totalServicesValueLabel = createDashboardMetricCard("Completed Services", "0", metricsDashboardPanel);
        vehiclesDueValueLabel = createDashboardMetricCard("Vehicles Due for Service", "0", metricsDashboardPanel);
        totalMaintenanceSpendValueLabel = createDashboardMetricCard("Total Maintenance Spend", "$0.00", metricsDashboardPanel);

        // Control panel with buttons and search input
        JPanel reportControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        JButton generateFullReportButton = new JButton("Generate Full Fleet Summary");
        generateFullReportButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        JTextField searchRegistrationInputField = new JTextField(10);
        JButton searchHistoryButton = new JButton("Search Vehicle History");

        reportControlsPanel.add(generateFullReportButton);
        reportControlsPanel.add(new JLabel("  |  Search by Vehicle Reg:"));
        reportControlsPanel.add(searchRegistrationInputField);
        reportControlsPanel.add(searchHistoryButton);

        // Scrollable text area for displaying reports
        reportDisplayTextArea = new JTextArea();
        reportDisplayTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportDisplayTextArea.setEditable(false);
        reportDisplayTextArea.setBackground(new Color(248, 249, 250));
        reportDisplayTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane reportScrollPane = new JScrollPane(reportDisplayTextArea);

        JPanel topSectionContainer = new JPanel(new BorderLayout(5, 5));
        topSectionContainer.add(metricsDashboardPanel, BorderLayout.NORTH);
        topSectionContainer.add(reportControlsPanel, BorderLayout.SOUTH);

        containerPanel.add(topSectionContainer, BorderLayout.NORTH);
        containerPanel.add(reportScrollPane, BorderLayout.CENTER);

        generateFullReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                reportDisplayTextArea.setText(reportService.generateFleetSummary(vehicleServiceManager));
            }
        });

        searchHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String searchedRegistration = searchRegistrationInputField.getText().trim().toUpperCase();
                searchRegistrationInputField.setText(searchedRegistration);
                if (searchedRegistration.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Please enter a vehicle registration number to search!",
                            "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                reportDisplayTextArea.setText(reportService.generateVehicleHistoryReport(vehicleServiceManager, searchedRegistration));
            }
        });

        return containerPanel;
    }

    private JLabel createDashboardMetricCard(String cardTitle, String initialDisplayValue, JPanel parentPanel) {
        JPanel metricCardPanel = new JPanel(new BorderLayout(4, 4));
        metricCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        metricCardPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(cardTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(initialDisplayValue, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(new Color(30, 30, 30));

        metricCardPanel.add(titleLabel, BorderLayout.NORTH);
        metricCardPanel.add(valueLabel, BorderLayout.CENTER);
        parentPanel.add(metricCardPanel);

        return valueLabel;
    }

    /* =========================================================================
     * TABLE AND DASHBOARD REFRESH HELPERS
     * ========================================================================= */
    public void updateVehicleTableData() {
        if (vehicleTableModel == null || vehicleServiceManager == null) return;
        vehicleTableModel.setRowCount(0);

        for (Vehicle currentVehicle : vehicleServiceManager.getAllVehicles()) {
            currentVehicle.updateStatus();

            vehicleTableModel.addRow(new Object[]{
                    currentVehicle.registrationNumber,
                    currentVehicle.brand,
                    currentVehicle.model,
                    currentVehicle.year,
                    currentVehicle.vehicleType,
                    currentVehicle.currentMileage,
                    currentVehicle.nextServiceMileage,
                    currentVehicle.status
            });
        }
    }

    public void updateServiceHistoryTableData() {
        if (serviceHistoryTableModel == null || vehicleServiceManager == null) return;
        serviceHistoryTableModel.setRowCount(0);

        for (ServiceRecord currentRecord : vehicleServiceManager.getAllServiceRecords()) {
            serviceHistoryTableModel.addRow(new Object[]{
                    currentRecord.recordId,
                    currentRecord.vehicleRegistrationNumber,
                    currentRecord.serviceType,
                    currentRecord.serviceDate,
                    currentRecord.mileageAtService,
                    currentRecord.laborCost != null ? currentRecord.laborCost.toPlainString() : "0.00",
                    currentRecord.partsCost != null ? currentRecord.partsCost.toPlainString() : "0.00",
                    currentRecord.totalCost != null ? currentRecord.totalCost.toPlainString() : "0.00",
                    currentRecord.notes
            });
        }
    }

    public void updateSparePartTableData() {
        if (sparePartTableModel == null || vehicleServiceManager == null) return;
        sparePartTableModel.setRowCount(0);

        for (Part currentPart : vehicleServiceManager.getAllParts()) {
            sparePartTableModel.addRow(new Object[]{
                    currentPart.partNumber,
                    currentPart.partName,
                    currentPart.category,
                    currentPart.price != null ? currentPart.price.toPlainString() : "0.00",
                    currentPart.stockQuantity
            });
        }
    }

    public void updateSummaryDashboardStats() {
        if (vehicleServiceManager == null || totalVehiclesValueLabel == null) return;

        totalVehiclesValueLabel.setText(String.valueOf(vehicleServiceManager.getTotalVehicles()));
        totalServicesValueLabel.setText(String.valueOf(vehicleServiceManager.getTotalServiceRecords()));
        vehiclesDueValueLabel.setText(String.valueOf(vehicleServiceManager.countVehiclesDueForService()));
        totalMaintenanceSpendValueLabel.setText("$" + vehicleServiceManager.calculateGrandTotalSpend().toPlainString());
    }
}
