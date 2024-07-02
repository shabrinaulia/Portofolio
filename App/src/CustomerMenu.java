import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerMenu extends MemberMenu {
    private Stage stage; // Menyimpan referensi ke stage utama
    private Scene scene; // Scene utama
    private Scene addOrderScene; // Scene untuk menambah pesanan
    private Scene printBillScene; // Scene untuk mencetak bill
    private Scene payBillScene; // Scene untuk membayar bill
    private Scene cekSaldoScene; // Scene untuk cek saldo
    private BillPrinter billPrinter; // Instance of BillPrinter
    private ComboBox<String> restaurantComboBox = new ComboBox<>(); // ComboBox untuk restoran
    private static Label balanceLabel = new Label(); // Label untuk saldo
    private App mainApp; // Menyimpan referensi ke aplikasi utama
    private List<Restaurant> restoList = new ArrayList<>(); // Daftar restoran
    private User user; // Menyimpan referensi ke pengguna saat ini

    private TextField orderIdInput; // Input untuk ID pesanan
    private ComboBox<String> paymentMethodComboBox; // ComboBox untuk metode pembayaran
    private ListView<String> menuItemsListView = new ListView<>(); // ListView untuk item menu
    private DatePicker datePicker; // DatePicker untuk tanggal pesanan

    public CustomerMenu(Stage stage, App mainApp, User user) {
        this.stage = stage;
        this.mainApp = mainApp;
        this.user = user; // Simpan informasi pengguna
        restoList = DepeFood.getRestoList(); // Ambil daftar restoran
        updateRestaurantComboBox(); // Perbarui ComboBox restoran
        this.scene = createBaseMenu(); // Buat scene utama
        this.addOrderScene = createTambahPesananForm(); // Buat scene tambah pesanan
        this.billPrinter = new BillPrinter(stage, mainApp, this.user); // Inisialisasi BillPrinter
        this.printBillScene = billPrinter.getScene(); // Ambil scene dari BillPrinter
        this.payBillScene = createBayarBillForm(); // Buat scene bayar bill
        this.cekSaldoScene = createCekSaldoScene(); // Buat scene cek saldo
    }

    // Membuat menu utama untuk pengguna
    @Override
    public Scene createBaseMenu() {
        VBox menuLayout = new VBox(10); // Layout dengan jarak 10 piksel antar elemen
        menuLayout.setAlignment(Pos.CENTER); // Setel alignment ke tengah
        menuLayout.setPadding(new Insets(20)); // Setel padding
        menuLayout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/resources/customer.png"))); // Tambah logo
        logo.setFitWidth(300); // Setel lebar logo
        logo.setFitHeight(250); // Setel tinggi logo
        logo.setPreserveRatio(true); // Pertahankan rasio aspek
        menuLayout.getChildren().add(logo);

        Label titleLabel = new Label("Customer Menu"); // Judul menu
        titleLabel.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #F0C9BA;");

        Button tambahPesananButton = new Button("Add Order"); // Tombol tambah pesanan
        styleButton(tambahPesananButton);
        tambahPesananButton.setOnAction(e -> stage.setScene(addOrderScene));

        Button cetakBillButton = new Button("Print Bill"); // Tombol cetak bill
        styleButton(cetakBillButton);
        cetakBillButton.setOnAction(e -> stage.setScene(printBillScene));
        mainApp.addScene("Print Bill", printBillScene);

        Button bayarBillButton = new Button("Pay Bill"); // Tombol bayar bill
        styleButton(bayarBillButton);
        bayarBillButton.setOnAction(e -> stage.setScene(payBillScene));

        Button cekSaldoButton = new Button("Balance Check"); // Tombol cek saldo
        styleButton(cekSaldoButton);
        cekSaldoButton.setOnAction(e -> stage.setScene(cekSaldoScene));

        Button logoutButton = new Button("Logout"); // Tombol logout
        logoutButton.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        logoutButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: #141C2C; -fx-font-size: 16px;");
        logoutButton.setOnAction(e -> mainApp.logout());

        menuLayout.getChildren().addAll(titleLabel, tambahPesananButton, cetakBillButton, bayarBillButton, cekSaldoButton, logoutButton);
        return new Scene(menuLayout, 400, 600); // Kembalikan scene
    }

    // Menerapkan gaya pada tombol
    private void styleButton(Button button) {
        button.setFont(Font.font("Hanken", FontWeight.NORMAL, 20)); // Setel font tombol
        button.setStyle("-fx-background-color: #F0C9BA; -fx-text-fill: #141C2C; -fx-font-size: 16px;"); // Setel gaya tombol
        button.setPrefWidth(200); // Setel lebar tombol
    }

    // Membuat form untuk menambah pesanan
    private Scene createTambahPesananForm() {
        VBox layout = new VBox(10); // Layout dengan jarak 10 piksel antar elemen
        layout.setAlignment(Pos.CENTER); // Setel alignment ke tengah
        layout.setPadding(new Insets(20)); // Setel padding
        layout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        Label titleLabel = new Label("Add Order"); // Judul form
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #F2CBBD;");

        Label restoranLabel = new Label("Restaurant:"); // Label untuk restoran
        restoranLabel.setStyle("-fx-font-size: 16;-fx-text-fill: #F0C9BA;");
        restaurantComboBox.setPromptText("Select Restaurant"); // Placeholder ComboBox

        Label dateLabel = new Label("Order Date:"); // Label untuk tanggal pesanan
        dateLabel.setStyle("-fx-font-size: 16;-fx-text-fill: #F0C9BA;");
        datePicker = new DatePicker(); // DatePicker untuk tanggal pesanan
        datePicker.setValue(LocalDate.now()); // Setel nilai default ke hari ini
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Format tanggal
        
        datePicker.setConverter(new StringConverter<>() { // Konverter untuk DatePicker
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date); // Format tanggal
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter); // Parse tanggal
                } else {
                    return null;
                }
            }
        });

        datePicker.setPromptText("dd/MM/yyyy"); // Placeholder DatePicker

        Label menuLabel = new Label("Menu:"); // Label untuk menu
        menuLabel.setStyle("-fx-text-fill: #F0C9BA;");
        menuItemsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // Multi-select ListView

        Button menuButton = new Button("Check Menu"); // Tombol untuk memeriksa menu
        styleButton(menuButton);
        menuButton.setOnAction(e -> {
            String restaurantNameSelected = restaurantComboBox.getValue(); // Ambil restoran yang dipilih
            Restaurant restaurantSelected = restoList.stream()
                    .filter(r -> r.getNama().equals(restaurantNameSelected))
                    .findFirst().orElse(null); // Temukan restoran yang sesuai
            if (restaurantSelected != null) {
                List<String> menuItems = restaurantSelected.getMenu().stream()
                        .map(Menu::getNamaMakanan)
                        .collect(Collectors.toList()); // Ambil item menu
                menuItemsListView.setItems(FXCollections.observableArrayList(menuItems)); // Setel item menu ke ListView
            } else {
                showAlert(AlertType.ERROR, "Error", "Restaurant not found."); // Tampilkan pesan error jika restoran tidak ditemukan
            }
        });

        Button submitButton = new Button("Submit"); // Tombol submit
        styleButton(submitButton);
        submitButton.setOnAction(e -> {
            String restaurantName = restaurantComboBox.getValue(); // Ambil nama restoran
            String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Format tanggal
            List<String> selectedItems = menuItemsListView.getSelectionModel().getSelectedItems(); // Ambil item yang dipilih
            handleBuatPesanan(restaurantName, date, selectedItems); // Panggil metode untuk membuat pesanan
        });

        Button backButton = new Button("Back"); // Tombol kembali
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(scene)); // Kembali ke scene utama

        layout.getChildren().addAll(titleLabel, restoranLabel, restaurantComboBox, dateLabel, datePicker, menuLabel, menuButton, menuItemsListView, submitButton, backButton);
        return new Scene(layout, 400, 600); // Kembalikan scene
    }

    // Membuat form untuk membayar bill
    private Scene createBayarBillForm() {
        VBox layout = new VBox(10); // Layout dengan jarak 10 piksel antar elemen
        layout.setAlignment(Pos.CENTER); // Setel alignment ke tengah
        layout.setPadding(new Insets(20)); // Setel padding
        layout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/resources/payment.png"))); // Tambah gambar
        image.setFitWidth(250); // Setel lebar gambar
        image.setFitHeight(200); // Setel tinggi gambar
        image.setPreserveRatio(true); // Pertahankan rasio aspek
        layout.getChildren().add(image);

        Label titleLabel = new Label("Bill Payment"); // Judul form
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #F2CBBD;");

        Label orderIdLabel = new Label("Order ID:"); // Label untuk Order ID
        orderIdLabel.setStyle("-fx-font-size: 16;-fx-text-fill: #F0C9BA;");
        orderIdInput = new TextField(); // Input untuk Order ID
        orderIdInput.setPromptText("Enter Order ID"); // Placeholder

        Label paymentMethodLabel = new Label("Payment Method:"); // Label untuk metode pembayaran
        paymentMethodLabel.setStyle("-fx-font-size: 16;-fx-text-fill: #F0C9BA;");
        paymentMethodComboBox = new ComboBox<>(); // ComboBox untuk metode pembayaran
        paymentMethodComboBox.setItems(FXCollections.observableArrayList("Credit Card", "Debit", "Cash")); // Pilihan metode pembayaran
        paymentMethodComboBox.setPromptText("Select Payment Method"); // Placeholder

        Button submitButton = new Button("Submit"); // Tombol submit
        styleButton(submitButton);
        submitButton.setOnAction(e -> {
            String orderId = orderIdInput.getText(); // Ambil Order ID
            String paymentMethod = paymentMethodComboBox.getValue(); // Ambil metode pembayaran
            handleBayarBill(orderId, paymentMethod); // Panggil metode untuk membayar bill
        });

        Button backButton = new Button("Back"); // Tombol kembali
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(scene)); // Kembali ke scene utama

        layout.getChildren().addAll(titleLabel, orderIdLabel, orderIdInput, paymentMethodLabel, paymentMethodComboBox, submitButton, backButton);
        return new Scene(layout, 400, 600); // Kembalikan scene
    }

    // Membuat form untuk cek saldo
    private Scene createCekSaldoScene() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER); // Setel alignment ke tengah
        layout.setPadding(new Insets(20)); // Setel padding
        layout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/resources/balance.png"))); // Tambah gambar
        image.setFitWidth(250); // Setel lebar gambar
        image.setFitHeight(200); // Setel tinggi gambar
        image.setPreserveRatio(true); // Pertahankan rasio aspek
        layout.getChildren().add(image);

        Label titleLabel = new Label("Cek Saldo"); // Judul form
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #F2CBBD;");

        Label nameLabel = new Label("Name: " + user.getNama()); // Label untuk nama pengguna
        nameLabel.setStyle("-fx-font-size: 18;-fx-text-fill: #F0C9BA;");

        balanceLabel.setText("Balance: " + user.getSaldo()); // Label untuk saldo
        balanceLabel.setStyle("-fx-font-size: 18;-fx-text-fill: #F0C9BA;");

        Button backButton = new Button("Back"); // Tombol kembali
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(scene)); // Kembali ke scene utama

        layout.getChildren().addAll(titleLabel, nameLabel, balanceLabel, backButton);
        return new Scene(layout, 400, 600); // Kembalikan scene
    }

    // Mengelola pembuatan pesanan baru
    private void handleBuatPesanan(String namaRestoran, String tanggalPemesanan, List<String> menuItems) {
        if (DepeFood.getRestaurantByName(namaRestoran) == null) { // Cek jika restoran valid
            showAlert(AlertType.ERROR, "Error", "Enter a valid restaurant!"); // Tampilkan pesan error
            return;
        }
        if (!OrderGenerator.validateDate(tanggalPemesanan)) { // Cek jika tanggal valid
            showAlert(AlertType.ERROR, "Error", "Enter a valid date!"); // Tampilkan pesan error
            return;
        }
        Restaurant restaurant = DepeFood.getRestaurantByName(namaRestoran); // Dapatkan restoran
        Order order = new Order(
                OrderGenerator.generateOrderID(namaRestoran, tanggalPemesanan, user.getNomorTelepon()), // Buat ID pesanan
                tanggalPemesanan, // Setel tanggal pesanan
                OrderGenerator.calculateDeliveryCost(user.getLokasi()), // Hitung ongkir
                restaurant, // Setel restoran
                DepeFood.getMenuRequest(restaurant, menuItems)); // Setel menu pesanan

        showAlert(AlertType.INFORMATION, "Success", "Order with ID " + order.getOrderId() + " successfully added!"); // Tampilkan pesan sukses
        user.addOrderHistory(order); // Tambah pesanan ke riwayat pengguna
        
        // Clear the input fields after adding the order
        restaurantComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        menuItemsListView.getSelectionModel().clearSelection();
    }

    // Mengelola pembayaran bill
    private void handleBayarBill(String orderID, String paymentOption) {
        //validasi
        boolean isOrderIDValid = false;
        for (Order order : user.getOrderHistory()) { // Cek jika ID pesanan valid
            if (order.getOrderId().equals(orderID)) {
                isOrderIDValid = true;
                break;
            }
        }
        if (!isOrderIDValid) {
            showAlert(AlertType.ERROR, "Error", "Order ID not found."); // Tampilkan pesan error jika ID pesanan tidak valid
            return;
        }

        Order userOrder = null;
            for (Order order : user.getOrderHistory()) {
                if (order.getOrderId().equals(orderID)) {
                    userOrder = order; // Dapatkan pesanan pengguna
                }
        DepeFoodPaymentSystem paymentSystem = user.getPaymentSystem(); // Sistem pembayaran pengguna
        boolean isCreditCard = paymentSystem instanceof CreditCardPayment; // Cek jenis pembayaran

        if ((isCreditCard && paymentOption.equals("Debit")) || (!isCreditCard && paymentOption.equals("Credit Card"))) {
            showAlert(AlertType.ERROR, "Error", "Selected payment method does not match user's registered payment system."); // Tampilkan pesan error jika metode pembayaran tidak sesuai
            return;
        }

        long amountToPay = 0;

        try {
            amountToPay = paymentSystem.processPayment(user.getSaldo(), (long) userOrder.getTotalHarga()); // Proses pembayaran
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Payment processing failed. Please try again."); // Tampilkan pesan error jika pembayaran gagal
            return;
        }


        long saldoLeft = user.getSaldo() - amountToPay; // Hitung sisa saldo

        user.setSaldo(saldoLeft); // Setel saldo pengguna
        balanceLabel.setText("Balance: " + user.getSaldo()); // Perbarui label saldo
        DepeFood.handleUpdateStatusPesanan(userOrder); // Perbarui status pesanan

        DecimalFormat decimalFormat = new DecimalFormat(); // Format untuk angka
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Setel pemisah ribuan
        decimalFormat.setDecimalFormatSymbols(symbols);

        showAlert(AlertType.INFORMATION, "Success", "Payment successful! Remaining balance: " + decimalFormat.format(saldoLeft)); // Tampilkan pesan sukses

        // Clear the input fields after successful payment
        orderIdInput.clear();
        paymentMethodComboBox.getSelectionModel().clearSelection();
        }
    }

    // Menampilkan alert
    private void showAlert(AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    // Memperbarui item dalam ComboBox restoran
    private void updateRestaurantComboBox() {
        restoList = DepeFood.getRestoList(); // Ambil daftar restoran
        restaurantComboBox.setItems(FXCollections.observableArrayList(
                restoList.stream()
                        .map(Restaurant::getNama) // Ambil nama restoran
                        .collect(Collectors.toList()) // Kumpulkan ke dalam daftar
        ));
    }
}
