import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.rewear_app1.DatabaseHelper;

public class UserViewModel extends ViewModel {

    private MutableLiveData<Integer> userCount = new MutableLiveData<>();
    private DatabaseHelper dbHelper;

    public UserViewModel(Application application) {
        dbHelper = new DatabaseHelper(application);
        loadUserCount();
    }

    public LiveData<Integer> getUserCount() {
        return userCount;
    }

    private void loadUserCount() {
        int count = dbHelper.getUserCount();
        userCount.setValue(count);
    }
}
