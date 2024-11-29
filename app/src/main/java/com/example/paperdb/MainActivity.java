package com.example.paperdb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText nameText, sizeText, priceText;
    private Button addButton, updateButton, deleteButton, selectImageButton;
    private ImageView imageView;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private String selectedClothingItem;
    private String imagePath = null;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

        nameText = findViewById(R.id.nameText);
        sizeText = findViewById(R.id.sizeText);
        priceText = findViewById(R.id.priceText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        imageView = findViewById(R.id.imageView);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getClothingItemNames());
        listView.setAdapter(adapter);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Получаем имя выбранного товара
            selectedClothingItem = adapter.getItem(position);

            // Извлекаем объект ClothingItem из PaperDB по имени
            ClothingItem clothing = Paper.book().read(selectedClothingItem, null);

            if (clothing != null) {
                // Заполняем поля ввода данными из объекта clothing
                nameText.setText(clothing.getName());
                sizeText.setText(clothing.getSize());
                priceText.setText(String.valueOf(clothing.getPrice()));

                // Извлекаем путь к изображению
                String selectedImagePath = clothing.getImagePath();

                if (selectedImagePath != null) {
                    // Если путь к изображению не пустой, отображаем его в ImageView
                    imageView.setImageURI(Uri.parse(selectedImagePath));
                } else {
                    // Если изображения нет, очищаем ImageView
                    imageView.setImageResource(0);
                }
            } else {
                Toast.makeText(MainActivity.this, "Ошибка при загрузке товара", Toast.LENGTH_SHORT).show();
            }
        });




        addButton.setOnClickListener(view -> {
            String name = nameText.getText().toString();
            String size = sizeText.getText().toString();
            String priceString = priceText.getText().toString();

            if (!name.isEmpty() && !size.isEmpty() && !priceString.isEmpty() && imagePath != null) {
                double price = Double.parseDouble(priceString);
                ClothingItem item = new ClothingItem(name, name, size, price, imagePath);
                Paper.book().write(name, item);
                updateClothingList();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Заполните все поля и выберите изображение!", Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(view -> {
            if (selectedClothingItem == null) {
                Toast.makeText(MainActivity.this, "Выберите товар!", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nameText.getText().toString();
            String size = sizeText.getText().toString();
            String priceString = priceText.getText().toString();

            if (!name.isEmpty() && !size.isEmpty() && !priceString.isEmpty() && imagePath != null) {
                double price = Double.parseDouble(priceString);
                ClothingItem item = new ClothingItem(selectedClothingItem, name, size, price, imagePath);
                Paper.book().write(selectedClothingItem, item);
                updateClothingList();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Заполните все поля и выберите изображение!", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(view -> {
            if (selectedClothingItem == null) {
                Toast.makeText(MainActivity.this, "Выберите товар!", Toast.LENGTH_SHORT).show();
                return;
            }

            Paper.book().delete(selectedClothingItem);
            updateClothingList();
            clearInputs();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            imagePath = selectedImageUri.toString(); // Сохраняем путь URI
            imageView.setImageURI(selectedImageUri); // Отображаем изображение
        }
    }

    private void clearInputs() {
        nameText.setText("");
        sizeText.setText("");
        priceText.setText("");
        imageView.setImageResource(0); // Убираем изображение
        imagePath = null;
        selectedClothingItem = null;
    }

    private void updateClothingList() {
        adapter.clear();
        adapter.addAll(getClothingItemNames());
        adapter.notifyDataSetChanged();
    }

    private List<String> getClothingItemNames() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }
}
