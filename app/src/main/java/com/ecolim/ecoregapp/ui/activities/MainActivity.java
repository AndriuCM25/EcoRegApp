package com.ecolim.ecoregapp.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.ecolim.ecoregapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Navigation Component
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHost.getNavController();

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // FAB → ir a Registro
        FloatingActionButton fab = findViewById(R.id.fab_nuevo);
        fab.setOnClickListener(v -> navController.navigate(R.id.registroFragment));

        // Ocultar FAB en ciertas pantallas
        navController.addOnDestinationChangedListener((ctrl, dest, args) -> {
            if (dest.getId() == R.id.registroFragment ||
                dest.getId() == R.id.loginActivity) {
                fab.hide();
            } else {
                fab.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
