package com.example.go4lunch.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.go4lunch.databinding.ActivitySettingsBinding;
import com.example.go4lunch.di.DI;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.ui.manager.UserManager;

public class SettingsActivity extends AppCompatActivity {
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private final UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        SwitchCompat switchCompat = binding.switchReceiveNotifications;
        setContentView(binding.getRoot());
        boolean isChecked = service.getReceiveNotifications();
        switchCompat.setChecked(isChecked);
        switchCompat.setOnClickListener(view -> {
            boolean newValue = switchCompat.isChecked();
            userManager.setReceiveNotifications(newValue);
            service.setReceiveNotifications(newValue);
        });
    }
}
