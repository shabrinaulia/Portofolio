import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginForm {
    private Stage stage;
    private App mainApp; // MainApp instance
    private TextField nameInput;
    private TextField phoneInput;

    public LoginForm(Stage stage, App mainApp) { // Pass MainApp instance to constructor
        this.stage = stage;
        this.mainApp = mainApp; // Store MainApp instance
    }

    // Membuat form login
    private Scene createLoginForm() {
        GridPane grid = new GridPane(); // Membuat layout grid
        grid.setAlignment(Pos.CENTER); // Setel alignment ke tengah
        grid.setHgap(10); // Setel horizontal gap
        grid.setVgap(10); // Setel vertical gap
        grid.setPadding(new Insets(25, 25, 25, 25)); // Setel padding
        grid.setStyle("-fx-background-color: #141C2C;"); // Setel warna latar belakang

        // Tambah logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        logo.setFitWidth(150); // Setel lebar logo
        logo.setFitHeight(150); // Setel tinggi logo
        GridPane.setHalignment(logo, HPos.CENTER);
        grid.add(logo, 0, 0, 2, 1);

        Label title = new Label("Welcome to DepeFood!"); // Judul aplikasi
        title.setFont(Font.font("Hanken", FontWeight.EXTRA_BOLD, 27));
        title.setTextFill(Color.web("#4F6FAF")); // Setel warna teks
        GridPane.setHalignment(title, HPos.CENTER);
        grid.add(title, 0, 1, 2, 1);

        // Menambahkan label dan text field untuk nama dan nomor telepon
        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Hanken", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.web("#F2CBBD")); // Setel warna teks
        grid.add(nameLabel, 0, 2);

        nameInput = new TextField(); // Input nama
        grid.add(nameInput, 1, 2);

        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setFont(Font.font("Hanken", FontWeight.BOLD, 15));
        phoneLabel.setTextFill(Color.web("#F2CBBD")); // Setel warna teks
        grid.add(phoneLabel, 0, 3);

        phoneInput = new TextField(); // Input nomor telepon
        grid.add(phoneInput, 1, 3);

        // Menambahkan tombol login
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: #141C2C;"); // Gaya tombol
        loginButton.setOnAction(e -> handleLogin()); // Aksi tombol login
        GridPane.setHalignment(loginButton, HPos.RIGHT);
        grid.add(loginButton, 1, 4);

        return new Scene(grid, 400, 400); // Mengembalikan scene
    }

    // Mengelola login pengguna
    private void handleLogin() {
        String name = nameInput.getText().trim(); // Ambil teks nama
        String phone = phoneInput.getText().trim(); // Ambil teks nomor telepon

        if (name.isEmpty() || phone.isEmpty()) { // Cek jika nama atau nomor telepon kosong
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Name or phone number cannot be empty"); // Tampilkan pesan error
            return;
        }

        try {
            User user = DepeFood.getUser(name, phone); // Cek kredensial pengguna
            if (user == null) { // Jika pengguna tidak ditemukan
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid name or phone number"); // Tampilkan pesan error
            } else {
                mainApp.setUser(user); // Set pengguna ke instance MainApp
                if (user.getRole().equalsIgnoreCase("admin")) { // Jika peran admin
                    AdminMenu adminMenu = new AdminMenu(stage, mainApp, user); 
                    stage.setScene(adminMenu.createBaseMenu()); // Tampilkan menu admin
                } else if (user.getRole().equalsIgnoreCase("customer")) { // Jika peran customer
                    CustomerMenu customerMenu = new CustomerMenu(stage, mainApp, user); 
                    stage.setScene(customerMenu.createBaseMenu()); // Tampilkan menu customer
                    mainApp.addScene("Customer", customerMenu.createBaseMenu()); 
                }
                nameInput.clear(); // Kosongkan input nama
                phoneInput.clear(); // Kosongkan input nomor telepon
            }
        } catch (Exception ex) { // Tangkap pengecualian
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + ex.getMessage()); // Tampilkan pesan error
        }
    }

    // Menampilkan alert
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType); // Buat alert
            alert.setTitle(title); // Setel judul alert
            alert.setHeaderText(null); 
            alert.setContentText(content); // Setel konten alert
            alert.showAndWait(); // Tampilkan alert
        });
    }

    // Mengembalikan scene form login
    public Scene getScene() {
        return this.createLoginForm();
    }
}
