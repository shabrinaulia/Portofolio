import javafx.scene.Scene;
import javafx.scene.control.Alert;

public abstract class MemberMenu {
    private Scene scene; // Menyimpan referensi ke scene

    // Metode abstrak untuk membuat menu dasar
    abstract protected Scene createBaseMenu();
    
    // Metode untuk menampilkan alert
    public void showAlert(String title, String header, String content, Alert.AlertType c) {
        Alert alert = new Alert(c); // Buat alert dengan tipe yang ditentukan
        alert.setTitle(title); // Setel judul alert
        alert.setHeaderText(header); // Setel header alert
        alert.setContentText(content); // Setel konten alert
        alert.showAndWait(); // Tampilkan alert dan tunggu hingga pengguna menutupnya
    }

    // Metode untuk mengembalikan scene
    public Scene getScene() {
        return this.scene; // Kembalikan scene yang disimpan
    }

    // Metode untuk menyegarkan data
    protected void refresh() {
        // Implementasi method ini untuk merefresh data yang dimiliki aplikasi
    }
}
