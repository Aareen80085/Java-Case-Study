import javax.swing.*;

/**
 * Main application entry point.
 * Beginner-friendly: No package statements, standard easy execution.
 */
public class Main {

    public static void main(String[] args) {
        // Optional: Set clean look and feel for modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Default look and feel is used if Nimbus is not available
        }

        // 1. Create the system manager
        VehicleServiceManager vehicleServiceManager = new VehicleServiceManager();

        // 2. Pre-populate sample vehicles, parts, and service records
        DataGenerator.populateSampleData(vehicleServiceManager);

        // 3. Launch the graphical user interface
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame applicationWindow = new MainFrame();
                applicationWindow.startApplication(vehicleServiceManager);
            }
        });
    }
}
