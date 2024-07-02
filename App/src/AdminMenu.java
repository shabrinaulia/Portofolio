import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminMenu extends MemberMenu {
    private Stage stage; // Menyimpan referensi ke stage utama
    private Scene scene; // Menyimpan referensi ke scene utama
    private User user; // Menyimpan informasi pengguna
    private Scene addRestaurantScene; // Scene untuk menambahkan restoran
    private Scene addMenuScene; // Scene untuk menambahkan menu
    private Scene viewRestaurantsScene; // Scene untuk melihat restoran
    private List<Restaurant> restoList = new ArrayList<>(); // Daftar restoran
    private App mainApp; // Referensi ke instance MainApp
    private ComboBox<String> restaurantComboBox; // ComboBox untuk memilih restoran
    private ListView<String> menuItemsListView = new ListView<>(); // ListView untuk menampilkan item menu
    private TextField nameRestoInput; // Input untuk nama restoran
    private TextField restaurantNameInput; // Input untuk nama restoran
    private TextField menuItemNameInput; // Input untuk nama item menu
    private TextField priceInput; // Input untuk harga menu

    public AdminMenu(Stage stage, App mainApp, User user) {
        this.stage = stage;
        this.mainApp = mainApp;
        this.user = user; // Simpan informasi pengguna
        this.scene = createBaseMenu();
        this.addRestaurantScene = createAddRestaurantForm();
        this.addMenuScene = createAddMenuForm();
        this.viewRestaurantsScene = createViewRestaurantsForm();
    }

    @Override
    public Scene createBaseMenu() {
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(20));
        menuLayout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        // Tambahkan logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/resources/admin.png")));
        logo.setFitWidth(250); // Setel lebar logo
        logo.setFitHeight(200); // Setel tinggi logo
        logo.setPreserveRatio(true); // Pertahankan rasio aspek
        menuLayout.getChildren().add(logo);

        // Tambahkan label judul
        Label titleLabel = new Label("Press the Button");
        titleLabel.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #F0C9BA;"); // Setel warna teks
        menuLayout.getChildren().add(titleLabel);

        // Tambahkan tombol untuk menambah restoran
        Button addRestaurantButton = new Button("Add Restaurant");
        styleButton(addRestaurantButton);
        addRestaurantButton.setOnAction(e -> stage.setScene(addRestaurantScene));
        
        // Tambahkan tombol untuk menambah menu
        Button addMenuButton = new Button("Add Menu");
        styleButton(addMenuButton);
        addMenuButton.setOnAction(e -> stage.setScene(addMenuScene));

        // Tambahkan tombol untuk melihat restoran
        Button viewRestaurantsButton = new Button("View Restaurants");
        styleButton(viewRestaurantsButton);
        viewRestaurantsButton.setOnAction(e -> stage.setScene(viewRestaurantsScene));

        // Tambahkan tombol untuk logout
        Button logoutButton = new Button("Logout");
        logoutButton.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 20));
        logoutButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: #141C2C; -fx-font-size: 16px;");
        logoutButton.setOnAction(e -> handleLogout());

        // Tambahkan semua tombol ke layout
        menuLayout.getChildren().addAll(addRestaurantButton, addMenuButton, viewRestaurantsButton, logoutButton);

        return new Scene(menuLayout, 400, 600);
    }

    // Metode untuk menambahkan gaya pada tombol
    private void styleButton(Button button) {
        button.setFont(Font.font("Hanken", FontWeight.NORMAL, 27));
        button.setStyle("-fx-background-color: #F0C9BA; -fx-text-fill: #141C2C; -fx-font-size: 16px;");
        button.setPrefWidth(200); // Setel lebar preferensi untuk visibilitas yang lebih baik
    }

    // Metode untuk menangani logout
    private void handleLogout() {
        mainApp.setUser(null);
        stage.setScene(mainApp.getScene("Login"));
    }

    // Metode untuk membuat form tambah restoran
    private Scene createAddRestaurantForm() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        // Tambahkan gambar
        ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/resources/resto.png")));
        image.setFitWidth(250); // Setel lebar gambar
        image.setFitHeight(200); // Setel tinggi gambar
        image.setPreserveRatio(true); // Pertahankan rasio aspek
        layout.getChildren().add(image);

        // Tambahkan label dan input untuk nama restoran
        Label nameRestoLabel = new Label("Enter restaurant name:");
        nameRestoLabel.setStyle("-fx-text-fill: #4F6FAF; -fx-font-size: 18px;");

        nameRestoInput = new TextField();
        nameRestoInput.setPromptText("Enter restaurant name");

        // Tambahkan tombol submit
        Button submitButton = new Button("Submit");
        styleButton(submitButton);
        submitButton.setOnAction(e -> {
            String restaurantName = nameRestoInput.getText();
            handleTambahRestoran(restaurantName);
        });

        // Tambahkan tombol kembali
        Button backButton = new Button("Back");
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(scene));

        // Tambahkan semua komponen ke layout
        layout.getChildren().addAll(nameRestoLabel, nameRestoInput, submitButton, backButton);
        return new Scene(layout, 400, 600);
    }

    // Metode untuk membuat form tambah menu
    private Scene createAddMenuForm() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        // Tambahkan label judul
        Label titleLabel = new Label("Add Menu");
        titleLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: F0C9BA;");

        // Tambahkan label dan input untuk nama restoran
        Label nameRestaurantLabel = new Label("Enter restaurant name:");
        nameRestaurantLabel.setStyle("-fx-text-fill: #4F6FAF; -fx-font-size: 18px;");
        restaurantNameInput = new TextField();
        restaurantNameInput.setPromptText("Restaurant Name");

        // Tambahkan label dan input untuk nama item menu
        Label nameMenuLabel = new Label("Enter menu item name:");
        nameMenuLabel.setStyle("-fx-text-fill: #4F6FAF; -fx-font-size: 18px;");
        menuItemNameInput = new TextField();
        menuItemNameInput.setPromptText("Menu Item Name");

        // Tambahkan label dan input untuk harga
        Label hargaLabel = new Label("Enter price:");
        hargaLabel.setStyle("-fx-text-fill: #4F6FAF; -fx-font-size: 18px;");
        priceInput = new TextField();
        priceInput.setPromptText("Price");

        // Tambahkan tombol untuk menambah item menu
        Button addButton = new Button("Add Menu Item");
        styleButton(addButton);
        addButton.setOnAction(e -> { // Ketika tombol diklik
            String restaurantName = restaurantNameInput.getText(); // Ambil nama restoran dari input
            String menuItemName = menuItemNameInput.getText(); // Ambil nama item menu dari input
            double price; // Variabel untuk harga
            try {
                price = Double.parseDouble(priceInput.getText()); // Konversi harga dari teks ke double
                if (restaurantName.trim().isEmpty() || menuItemName.trim().isEmpty()) { // Cek jika input kosong
                    showAlert(AlertType.ERROR, "Error", "Restaurant name and menu item name cannot be empty."); // Tampilkan error
                    return; 
                }
                Restaurant restaurant = restoList.stream() // Mencari restoran dalam daftar
                    .filter(r -> r.getNama().equals(restaurantName)) // Filter berdasarkan nama restoran
                    .findFirst().orElse(null); // Mengambil restoran pertama atau null jika tidak ditemukan
                if (restaurant == null) { // Cek jika restoran tidak ditemukan
                    showAlert(AlertType.ERROR, "Error", "Restaurant not found."); 
                } else {
                    handleTambahMenuRestoran(restaurant, menuItemName, price); // Tambah item menu ke restoran
                }
            } catch (NumberFormatException ex) { // Tangkap pengecualian jika harga tidak valid
                showAlert(AlertType.ERROR, "Error", "Price must be a number."); 
            }
        });
        

        // Tambahkan tombol kembali
        Button backButton = new Button("Back");
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(scene));

        // Tambahkan semua komponen ke layout
        layout.getChildren().addAll(titleLabel, nameRestaurantLabel ,restaurantNameInput,nameMenuLabel, menuItemNameInput, hargaLabel, priceInput, addButton, backButton);
        return new Scene(layout, 400, 600);
    }

    // Metode untuk membuat form melihat restoran
    private Scene createViewRestaurantsForm() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        // Tambahkan gambar
        ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/resources/restoran.png")));
        image.setFitWidth(200); // Setel lebar gambar
        image.setFitHeight(200); // Setel tinggi gambar
        image.setPreserveRatio(true); // Pertahankan rasio aspek
        layout.getChildren().add(image);

        // Tambahkan label judul
        Label titleLabel = new Label("View Restaurants");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #F2CBBD;");

        // Tambahkan ComboBox untuk memilih restoran
        restaurantComboBox = new ComboBox<>();
        restaurantComboBox.setPromptText("Select Restaurant");
        updateRestaurantComboBox();

        // Setel ListView untuk menampilkan item menu
        menuItemsListView.setPrefHeight(200);

        // Setel aksi saat restoran dipilih dari ComboBox
        restaurantComboBox.setOnAction(e -> { 
            String selectedRestaurant = restaurantComboBox.getValue(); // Ambil nilai restoran yang dipilih
            Restaurant restaurant = restoList.stream() // Cari restoran dalam daftar
                .filter(r -> r.getNama().equals(selectedRestaurant)) // Filter berdasarkan nama restoran
                .findFirst().orElse(null); // Ambil restoran pertama atau null jika tidak ditemukan
            if (restaurant != null) { // Jika restoran ditemukan
                updateMenuItemsListView(restaurant); // Perbarui ListView dengan item menu restoran
            } else {
                showAlert(AlertType.ERROR, "Error", "Restaurant not found."); // Tampilkan error jika restoran tidak ditemukan
            }
        });
        

        // Tambahkan tombol kembali
        Button backButton = new Button("Back");
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(scene));

        // Tambahkan semua komponen ke layout
        layout.getChildren().addAll(titleLabel, restaurantComboBox, menuItemsListView, backButton);
        return new Scene(layout, 400, 600);
    }

    // Metode untuk menangani penambahan restoran baru
    private void handleTambahRestoran(String nama) {
        if (nama == null || nama.trim().isEmpty()) { // Cek jika nama kosong
            showAlert(AlertType.ERROR, "Error", "Restaurant name cannot be empty."); // Tampilkan error
            return; // Keluar dari metode
        }
    
        if (nama.trim().length() < 4) { // Cek jika nama kurang dari 4 karakter
            showAlert(AlertType.ERROR, "Error", "Restaurant name must be at least 4 characters long."); // Tampilkan error
            return; // Keluar dari metode
        }
    
        String validName = DepeFood.getValidRestaurantName(nama); // Dapatkan nama restoran yang valid
        if (validName != null) { // Jika nama valid
            Restaurant newRestaurant = new Restaurant(validName); // Buat objek restoran baru
            DepeFood.handleTambahRestoran(nama); // Tambahkan restoran ke daftar
            restoList = DepeFood.getRestoList(); // Perbarui daftar restoran
            showAlert(AlertType.INFORMATION, "Success", "Restaurant added successfully!"); // Tampilkan pesan sukses
            nameRestoInput.clear(); // Kosongkan field input
            updateRestaurantComboBox(); // Perbarui ComboBox restoran
            stage.setScene(scene); // Kembali ke scene utama
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to add restaurant. Please try again."); // Tampilkan error jika gagal
        }
    }
    

    // Metode untuk menangani penambahan item menu baru
    private void handleTambahMenuRestoran(Restaurant restaurant, String itemName, double price) {
        if (itemName == null || itemName.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Menu item name cannot be empty.");
            return;
        }

        Menu newMenuItem = new Menu(itemName, price); // Membuat objek Menu baru dengan nama item dan harga yang diberikan
        restaurant.addMenu(newMenuItem);  // Menambahkan item menu baru ke daftar menu restoran
        showAlert(AlertType.INFORMATION, "Success", "Menu item added successfully!");
        restaurantNameInput.clear(); // Kosongkan field setelah menambahkan item menu
        menuItemNameInput.clear();
        priceInput.clear();
        stage.setScene(scene);
    }

    // Metode untuk menampilkan alert
    private void showAlert(AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    // Metode untuk memperbarui ComboBox restoran
    private void updateRestaurantComboBox() {
        restaurantComboBox.setItems(FXCollections.observableArrayList( // Atur item ComboBox
            restoList.stream() // Stream daftar restoran
                .map(Restaurant::getNama) // Ambil nama setiap restoran
                .collect(Collectors.toList()) // Kumpulkan nama-nama ke dalam daftar
        ));
    }

    // Metode untuk memperbarui ListView item menu
    private void updateMenuItemsListView(Restaurant restaurant) {
        menuItemsListView.setItems(FXCollections.observableArrayList( // Atur item ListView
            restaurant.getMenu().stream() // Stream daftar menu restoran
                .map(item -> item.getNamaMakanan() + " - " + String.format("%.0f", item.getHarga())) // Format nama makanan dan harga
                .collect(Collectors.toList()) // Kumpulkan item menu ke dalam daftar
        ));
    }
}
