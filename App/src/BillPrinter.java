import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BillPrinter {
    private Stage stage;
    private App mainApp;
    private User user;
    private Scene previousScene;

    public BillPrinter(Stage stage, App mainApp, User user) {
        this.stage = stage;
        this.mainApp = mainApp;
        this.user = user;
    }

    public Scene createBillPrinterForm() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: #141C2C;");

        // Label judul
        Label titleBill = new Label("Print Bill");
        titleBill.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        titleBill.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #F0C9BA;");

        // Label untuk field Order ID
        Label orderFieldLabel = new Label("Enter Order ID:");
        orderFieldLabel.setFont(Font.font("Hanken", FontWeight.NORMAL, 20));
        orderFieldLabel.setStyle("-fx-text-fill: #F0C9BA; -fx-font-size: 18px;");
        TextField orderField = new TextField();
        orderField.setPromptText("Enter your Order ID");

        // Tombol untuk mencetak bill
        Button printButton = new Button("Print");
        printButton.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        printButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: #141C2C; -fx-font-size: 16px;");
        // Atur aksi saat tombol diklik
        printButton.setOnAction(e -> {
            Order order = DepeFood.getOrderOrNull(orderField.getText()); // Dapatkan order berdasarkan ID
            if (order == null) {
                showAlert(Alert.AlertType.ERROR, "Order Not Found", "The order ID you entered does not exist. Please check the order ID and try again.");
            } else {
                previousScene = stage.getScene(); // Simpan referensi ke scene sebelumnya
                stage.setScene(printBill(orderField.getText())); // Ganti ke scene print bill
            }
        });

        // Tombol untuk kembali
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        backButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: #141C2C; -fx-font-size: 16px;");
        backButton.setOnAction(e -> {
            mainApp.setScene(mainApp.getScene("Customer")); // Kembali ke scene customer saat tombol "Back" ditekan
        });

        // Setel alignment layout ke tengah
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleBill, orderFieldLabel, orderField, printButton, backButton);
        return new Scene(layout, 400, 400);
    }

    private Scene printBill(String orderId) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: #141C2C;");

        // Label judul
        Label titlePrintBill = new Label("Bill");
        titlePrintBill.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD,35));
        titlePrintBill.setStyle("-fx-text-fill: #4F6FAF;");

        Order order = DepeFood.getOrderOrNull(orderId); // Dapatkan order berdasarkan ID
        Label bill = new Label(outputBillPesanan(order)); // Buat label dengan detail bill
        bill.setFont(Font.font("Hanken", FontWeight.NORMAL, 20));
        bill.setStyle("-fx-font-size: 15;-fx-text-fill: #F0C9BA;");

        // Tombol untuk kembali
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        backButton.setStyle("-fx-background-color: #F0C9BA; -fx-text-fill: #141C2C; -fx-font-size: 16px;");
        backButton.setOnAction(e -> { // Atur aksi saat tombol diklik
            stage.setScene(previousScene); // Kembali ke scene sebelumnya saat tombol "Back" ditekan
        });

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titlePrintBill, bill, backButton);
        return new Scene(layout, 400, 400);
    }

    private String outputBillPesanan(Order order) {
        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Setel pemisah ribuan
        decimalFormat.setDecimalFormatSymbols(symbols);
        return String.format("Bill:%n" +
                "Order ID: %s%n" +
                "Tanggal Pemesanan: %s%n" +
                "Lokasi Pengiriman: %s%n" +
                "Status Pengiriman: %s%n" +
                "Pesanan:%n%s%n" +
                "Biaya Ongkos Kirim: Rp %s%n" +
                "Total Biaya: Rp %s%n",
                order.getOrderId(), // Dapatkan ID order
                order.getTanggal(), // Dapatkan tanggal pemesanan
                user.getLokasi(), // Dapatkan lokasi pengguna
                !order.getOrderFinished() ? "Not Finished" : "Finished", // Cek status pengiriman
                getMenuPesananOutput(order), // Dapatkan detail menu pesanan
                decimalFormat.format(order.getOngkir()), // Format biaya ongkos kirim
                decimalFormat.format(order.getTotalHarga())); // Format total biaya
    }

    private String getMenuPesananOutput(Order order) {
        StringBuilder pesananBuilder = new StringBuilder(); // Buat StringBuilder untuk menyimpan detail pesanan
        DecimalFormat decimalFormat = new DecimalFormat(); // Buat objek DecimalFormat untuk format angka
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(); // Buat objek DecimalFormatSymbols
        symbols.setGroupingSeparator('\u0000'); // Tidak ada pemisah ribuan untuk harga menu
        decimalFormat.setDecimalFormatSymbols(symbols); // Terapkan simbol pemisah ke DecimalFormat
        for (Menu menu : order.getSortedMenu()) { // Iterasi melalui menu pesanan
            pesananBuilder.append("- ").append(menu.getNamaMakanan()).append(" ") // Tambahkan nama menu
                    .append(decimalFormat.format(menu.getHarga())).append("\n"); // Tambahkan harga menu
        }
        if (pesananBuilder.length() > 0) {
            pesananBuilder.deleteCharAt(pesananBuilder.length() - 1); // Hapus karakter terakhir (newline)
        }
        return pesananBuilder.toString(); // Kembalikan string detail menu
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType); // Buat alert
            alert.setTitle(title); // Setel judul alert
            alert.setHeaderText(null);
            alert.setContentText(content); // Setel konten alert
            alert.showAndWait(); // Tampilkan alert
        });
    }

    public Scene getScene() {
        return this.createBillPrinterForm(); // Kembalikan form untuk mencetak bill
    }

    // Class ini opsional
    public class MenuItem {
        private final StringProperty itemName;
        private final StringProperty price;

        public MenuItem(String itemName, String price) {
            this.itemName = new SimpleStringProperty(itemName); // Setel nama item
            this.price = new SimpleStringProperty(price); // Setel harga
        }

        public StringProperty itemNameProperty() {
            return itemName; // Kembalikan properti nama item
        }

        public StringProperty priceProperty() {
            return price; // Kembalikan properti harga
        }

        public String getItemName() {
            return itemName.get(); // Dapatkan nama item
        }

        public String getPrice() {
            return price.get(); // Dapatkan harga
        }
    }
}
