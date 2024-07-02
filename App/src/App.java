import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    private Stage window; // Menyimpan referensi ke stage utama
    private Map<String, Scene> allScenes = new HashMap<>(); // Menyimpan semua scene aplikasi
    private Scene currentScene; // Menyimpan scene saat ini
    private static User user; // Menyimpan pengguna yang sedang login

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage; // Inisialisasi stage utama
        window.setTitle("DepeFood Ordering System"); // Set judul aplikasi
        DepeFood.initUser(); // Inisialisasi pengguna

        // Inisialisasi semua scene
        Scene loginScene = new LoginForm(window, this).getScene(); // Buat scene login

        // Masukkan semua scene ke dalam peta
        allScenes.put("Login", loginScene);
        
        // Set scene awal aplikasi ke scene login
        setScene(loginScene);
        window.show(); // Tampilkan jendela
    }

    public void setUser(User newUser) {
        user = newUser; // Set pengguna baru
    }

    public static User getUserLoggedIn() {
        return user; // Ambil pengguna yang sedang login
    }

    // Metode untuk mengatur scene
    public void setScene(Scene scene) {
        window.setScene(scene); // Set scene ke jendela
        currentScene = scene; // Simpan scene saat ini
    }

    // Metode untuk mendapatkan scene berdasarkan nama
    public Scene getScene(String sceneName) {
        return allScenes.get(sceneName); // Ambil scene dari peta berdasarkan nama
    }

    public void addScene(String sceneName, Scene scene) {
        allScenes.put(sceneName, scene); // Tambahkan scene baru ke peta
    }

    public void logout() {
        setUser(null); // Kosongkan pengguna saat ini
        setScene(getScene("Login")); // Pindah ke scene login
    }

    public static void main(String[] args) {
        launch(args); 
    }
}
