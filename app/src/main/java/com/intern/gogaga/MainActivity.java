package com.intern.gogaga;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ImageButton s_settingBtn, s_LogoutBtn, editProBtn;
    private CircleImageView profile_img;
    private TextView nameTv, professionTv, s_emailTv;
    private TextView requestTv, offringTv, recommendsTv;
    private ImageButton floatBtn;
    private RelativeLayout recommendsUi, offeringUI, requestUi;
    private TextView offerUiText,recommendUiText,requestUiText;
    //permission constants
    private static final int LOCATION_REQUEST_CODE = 866;
    private static final int CAMERA_REQUEST_CODE = 867;
    private static final int STORAGE_REQUEST_CODE = 868;

    private static final int IMAGE_PICK_GALLERY_CODE = 865;
    private static final int IMAGE_PICK_CAMERA_CODE = 864;

    //permission arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private LocationManager locationManager;

    private double latitude, longitude;

    //image picked uri
    private Uri productImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        s_settingBtn = findViewById(R.id.s_settingBtn);
        s_LogoutBtn = findViewById(R.id.s_LogoutBtn);
        editProBtn = findViewById(R.id.editProBtn);

        profile_img = findViewById(R.id.profile_img);

        nameTv = findViewById(R.id.nameTv);
        professionTv = findViewById(R.id.professionTv);
        s_emailTv = findViewById(R.id.s_emailTv);

        requestTv = findViewById(R.id.requestTv);
        offringTv = findViewById(R.id.offringTv);
        recommendsTv = findViewById(R.id.recommendsTv);

        recommendsUi = findViewById(R.id.recommendsUi);
        offeringUI = findViewById(R.id.offeringUI);
        requestUi = findViewById(R.id.requestUi);

        offerUiText = findViewById(R.id.offerUiText);
        recommendUiText = findViewById(R.id.recommendUiText);
        requestUiText = findViewById(R.id.requestUiText);



        floatBtn = findViewById(R.id.floatBtn);
        //init permissions arrays
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        showRequestUi();
        offringTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOfferingUi();
            }
        });
        requestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestUi();
            }
        });
        recommendsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecommends();
            }
        });

        s_settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsDialog();
            }
        });
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewPostforCategory();
            }
        });
        editProBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Edit Profile options
                openBottomSheetForProfile();
            }
        });
        s_LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exitIntent = new Intent(Intent.ACTION_MAIN);
                exitIntent.addCategory(Intent.CATEGORY_HOME);
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
                return;
            }
        });


    }

    private void openSettingsDialog() {
        Toast.makeText(this, "This Feature is comming soon", Toast.LENGTH_SHORT).show();
    }

    private TextView selectChoice,pickDemand,pickLocation;
    private EditText detailsEt;
    private String selectCh = "Click to select choice";
    private String selectDemand = "Pick a Demand";
    private String selectLocation = "Location";

    private void openAddNewPostforCategory() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.create_post_layout, null);
        detailsEt = view.findViewById(R.id.detailsEt);
        Button createPostBtn = view.findViewById(R.id.createPostBtn);
        selectChoice = view.findViewById(R.id.selectChoice);
        pickDemand = view.findViewById(R.id.pickDemand);
        pickLocation = view.findViewById(R.id.pickLocation);

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkData();
                if (selectChoice.getText().toString().equals(selectCh)){
                    selectChoice.setError("complete step 1");
                }
                else   if (pickDemand.getText().toString().equals(selectDemand)){
                    pickDemand.setError("complete step 2");
                }
                else   if (pickLocation.getText().toString().equals(selectCh)){
                    pickLocation.setError("complete step 3");
                }
                else   if (detailsEt.getText().toString().isEmpty()){
                    detailsEt.setError("please mention in details");
                }
                else{

                    String choice = selectChoice.getText().toString();
                    String demand = pickDemand.getText().toString();
                    String locationAdd = pickLocation.getText().toString();
                    String detail = detailsEt.getText().toString();

                    String text =  "you " + choice  + " us " + demand + " at " + locationAdd + "\n"+"  details :  "+detail;
                    if (choice.equals("offerings")){
                        bottomSheetDialog.dismiss();
                        offerUiText.setText(text);
                        offringTv.isSelected();
                    }
                    else if (choice.equals("Asking for")){
                        bottomSheetDialog.dismiss();
                        requestUiText.setText(text);
                        requestTv.isSelected();

                    }
                    else {
                        bottomSheetDialog.dismiss();
                        recommendUiText.setText(text);
                        recommendsTv.isSelected();
                    }
                }
            }
        });
        selectChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChoiceDialog();
            }
        });
        pickDemand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDemandDialog();
            }
        });
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Live Location
                if (checkLocationPermissions()) {
                    //already Allowed
                    detectLocation();
                } else {
                    //not Allowed, request for  permission
                    requestLocationPermission();
                }
            }
        });

        //set view
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();

    }

    private void pickDemandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Demand ")
                .setItems(Categories.demand, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Categories.demand[which];
                        pickDemand.setText(category);
                    }
                })
                .show();
    }


    private void openChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("For")
                .setItems(Categories.postCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Categories.postCategories[which];
                        selectChoice.setText(category);
                    }
                })
                .show();
    }

    private void showRequestUi() {

        // show selected ui and hide orders ui
        requestUi.setVisibility(View.VISIBLE);
        offeringUI.setVisibility(View.GONE);
        recommendsUi.setVisibility(View.GONE);

        requestTv.setTextColor(getResources().getColor(R.color.colorBlack));
        requestTv.setBackgroundResource(R.drawable.shape_rect04);

        offringTv.setTextColor(getResources().getColor(R.color.colorWhite));
        offringTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        recommendsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        recommendsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOfferingUi() {
        // show selected ui and hide orders ui
        requestUi.setVisibility(View.GONE);
        offeringUI.setVisibility(View.VISIBLE);
        recommendsUi.setVisibility(View.GONE);

        offringTv.setTextColor(getResources().getColor(R.color.colorBlack));
        offringTv.setBackgroundResource(R.drawable.shape_rect04);

        requestTv.setTextColor(getResources().getColor(R.color.colorWhite));
        requestTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        recommendsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        recommendsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showRecommends() {
        // show selected ui and hide orders ui
        requestUi.setVisibility(View.GONE);
        offeringUI.setVisibility(View.GONE);
        recommendsUi.setVisibility(View.VISIBLE);

        recommendsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        recommendsTv.setBackgroundResource(R.drawable.shape_rect04);

        requestTv.setTextColor(getResources().getColor(R.color.colorWhite));
        requestTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        offringTv.setTextColor(getResources().getColor(R.color.colorWhite));
        offringTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }


    private CircleImageView profile;
    private EditText nameEt, emailEt, professionEt;

    private void openBottomSheetForProfile() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.edit_profile_layout, null);

        profile = view.findViewById(R.id.e_profileIv);
        nameEt = view.findViewById(R.id.nameEt);
        emailEt = view.findViewById(R.id.emailEt);
        professionEt = view.findViewById(R.id.professionEt);
        Button updateBtn = view.findViewById(R.id.updateBtn);

        //set view
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        professionEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfessionListDialog();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String email = emailEt.getText().toString();
                String profession = professionEt.getText().toString();


                if (name.isEmpty()) {
                    nameEt.setError("Name is empty");
                } else if (email.isEmpty()) {
                    emailEt.setError("Name is empty");
                } else if (profession.isEmpty()) {
                    professionEt.setError("Name is empty");
                } else {
                    nameTv.setText(name);
                    s_emailTv.setText(email);
                    professionTv.setText(profession);

                        try {
                            Glide.with(getApplicationContext()).load(productImgUri).into(profile_img);

                        }
                        catch (Exception e){
                            profile_img.setImageResource(R.drawable.ic_person_grey);
                        }
                    bottomSheetDialog.dismiss();

                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImagePickDialog();
            }
        });

        //show dialog
        bottomSheetDialog.show();


    }

    private void openProfessionListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your Profession")
                .setItems(Categories.Professions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Categories.Professions[which];
                        professionEt.setText(category);
                    }
                })
                .show();
    }

    private void showImagePickDialog() {
        // options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //Camera Clicked
                            if (checkCameraPermission()) {
                                // cameraPermission allowed
                                pickFromCamera();
                            } else {
                                // cameraPermission not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //Gallery Clicked
                            if (checkStoragePermission()) {
                                // Storage Permission allowed
                                pickFromGallery();
                            } else {
                                // Storage Permission not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }
                }).show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        productImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, productImgUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void detectLocation() {
        Toast.makeText(getApplicationContext(), "please wait...", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private boolean checkLocationPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                locationPermissions, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();
    }

    private void findAddress() {
        // find country state , city house no near landmark
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); //Complete Address
            String City = addresses.get(0).getLocality();
            String State = addresses.get(0).getAdminArea();
            String Country = addresses.get(0).getCountryName();
            
            pickLocation.setText(address);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // gps location disabled
        Toast.makeText(this, "Please turn on Location...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //permission allowed
                        detectLocation();
                    } else {
                        //permission Denied
                        Toast.makeText(getApplicationContext(), "Location permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission allowed
                        pickFromCamera();
                    } else {
                        //permission Denied
                        Toast.makeText(getApplicationContext(), "Camera permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //permission allowed
                        pickFromGallery();
                    } else {
                        //permission Denied
                        Toast.makeText(getApplicationContext(), "Storage permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                productImgUri = data.getData();
                //set to imageView
                profile.setImageURI(productImgUri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageView
                profile.setImageURI(productImgUri);
                profile_img.setImageURI(productImgUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
