package com.aplikasi_ekostkarawang.Pemesanan;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aplikasi_ekostkarawang.Beranda.AdapterDataKost;
import com.aplikasi_ekostkarawang.Beranda.ArrayDataKost;
import com.aplikasi_ekostkarawang.Beranda.Selengkapnya.ActivitySelengkapnya;
import com.aplikasi_ekostkarawang.Pesan.ActivityPesan;
import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.BillingAddress;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.ShippingAddress;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class ActivityPemesanan extends AppCompatActivity implements TransactionFinishedCallback {
    ImageView imgPemesanan;
    TextView txtNamaKostPemesanan, txtDeskripsiKostPemesanan, txtAlamatKostPemesanan, txtLihatPetaPemesanan, txtNomorPemesanan, txtNamaPemesan, txtTanggalMasukPemesanan,
            txtStatusPembayaran, txtMetodePembayaran, txtNomorPembayaran, txtSewaPerPemesanan, txtNamaKamarPemesanan, txtHargaKostPemesanan, txtPetunjukPembayaran;
    LinearLayout LLTanggalMasukPemesanan, LLMetodePembayaran, LLNomorPembayaran, LLPetunjukPembayaran;
    Button btnChatPemilikKostPemesanan, btnLihatKostSelengkapnya, btnBatalkanPemesanan;

    DatabaseReference databaseReference;
    SharedPreferences SP;
    Calendar calendar;

    int PosisiPemesanan, Hari, Bulan, Tahun;
    LatLng lokasiTerakhir;
    String Alasan_Pembatalan;
    ArrayList<ArrayPemesanan> arrayPemesanan    = new ArrayList<>();
    ArrayList<ArrayDataKost> arrayDataKost      = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemesanan);

        imgPemesanan                = findViewById(R.id.imgPemesanan);
        txtNamaKostPemesanan        = findViewById(R.id.txtNamaKostPemesanan);
        txtDeskripsiKostPemesanan   = findViewById(R.id.txtDeskripsiKostPemesanan);
        txtAlamatKostPemesanan      = findViewById(R.id.txtAlamatKostPemesanan);
        txtLihatPetaPemesanan       = findViewById(R.id.txtLihatPetaPemesanan);
        txtNomorPemesanan           = findViewById(R.id.txtNomorPemesanan);
        txtNamaPemesan              = findViewById(R.id.txtNamaPemesan);
        txtTanggalMasukPemesanan    = findViewById(R.id.txtTanggalMasukPemesanan);
        txtStatusPembayaran         = findViewById(R.id.txtStatusPembayaran);
        txtMetodePembayaran         = findViewById(R.id.txtMetodePembayaran);
        txtNomorPembayaran          = findViewById(R.id.txtNomorPembayaran);
        txtSewaPerPemesanan         = findViewById(R.id.txtSewaPerPemesanan);
        txtNamaKamarPemesanan       = findViewById(R.id.txtNamaKamarPemesanan);
        txtHargaKostPemesanan       = findViewById(R.id.txtHargaKostPemesanan);
        txtPetunjukPembayaran       = findViewById(R.id.txtPetunjukPembayaran);
        LLTanggalMasukPemesanan     = findViewById(R.id.LLTanggalMasukPemesanan);
        LLMetodePembayaran          = findViewById(R.id.LLMetodePembayaran);
        LLNomorPembayaran           = findViewById(R.id.LLNomorPembayaran);
        LLPetunjukPembayaran        = findViewById(R.id.LLPetunjukPembayaran);
        btnChatPemilikKostPemesanan = findViewById(R.id.btnChatPemilikKostPemesanan);
        btnLihatKostSelengkapnya    = findViewById(R.id.btnLihatKostSelengkapnya);
        btnBatalkanPemesanan        = findViewById(R.id.btnBatalkanPemesanan);

        PosisiPemesanan             = Objects.requireNonNull(getIntent().getExtras()).getInt("PosisiPemesanan");
        arrayPemesanan              = AdapterPemesanan.arrayPemesanan;
        SP                          = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        lokasiTerakhir              = new LatLng(SP.getFloat("Latitude", 0), SP.getFloat("Longitude", 0));

        calendar                    = Calendar.getInstance();
        Hari                        = calendar.get(Calendar.DAY_OF_MONTH);
        Bulan                       = calendar.get(Calendar.MONTH);
        Tahun                       = calendar.get(Calendar.YEAR);

        Collections.shuffle(arrayPemesanan.get(PosisiPemesanan).getArrayFotoKost());
        Glide.with(ActivityPemesanan.this).asBitmap().load(arrayPemesanan.get(PosisiPemesanan).getArrayFotoKost().get(0)).into(imgPemesanan);

        txtNamaKostPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getNama_Kost());
        txtAlamatKostPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getAlamat_Kost());
        txtNomorPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getNomor_Pemesanan());
        txtNamaPemesan.setText(arrayPemesanan.get(PosisiPemesanan).getNama_Pemesan());
        txtStatusPembayaran.setText(arrayPemesanan.get(PosisiPemesanan).getStatus_Pemesanan());
        txtSewaPerPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getSewa_Per());
        txtNamaKamarPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getNama_Kamar());
        txtHargaKostPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getHarga_Kost());

        AdapterDataKost.arrayDataKost.clear();
        txtLihatPetaPemesanan.setEnabled(false);
        btnLihatKostSelengkapnya.setEnabled(false);

        PetunjukPembayaran();
        mengambilDataKost();
        tanggalMasukKost();
        metodePembayaranKost();
        pilihMetodePembayaran();
        batalkanPemesananKost();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Pemesanan_Kost);
        PushDownAnim.setPushDownAnimTo(txtLihatPetaPemesanan, btnChatPemilikKostPemesanan, btnLihatKostSelengkapnya, btnBatalkanPemesanan);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void PetunjukPembayaran(){
        if(arrayPemesanan.get(PosisiPemesanan).getStatus_Pemesanan().equals("Pesananan di Batalkan")){
            txtStatusPembayaran.setTextColor(btnBatalkanPemesanan.getTextColors());
            LLTanggalMasukPemesanan.setEnabled(false);
            LLMetodePembayaran.setEnabled(false);

            btnBatalkanPemesanan.setVisibility(View.GONE);
            txtPetunjukPembayaran.setEnabled(false);
            txtPetunjukPembayaran.setText(getString(R.string.Pesananan_Kost_Anda_Sudah_di_Batalkan));
        }
        else{
            txtPetunjukPembayaran.setEnabled(false);

            if(arrayPemesanan.get(PosisiPemesanan).getStatus_Pemesanan().equals("Transaksi Selesai")){
                txtPetunjukPembayaran.setText(getString(R.string.Anda_dapat_Mengunduh_Bukti_Pembayarannya) + "\n"  + getString(R.string.Unduh_Bukti_Pembayaran));

                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
                databaseReference.orderByChild("Nomor_Pemesanan").equalTo(arrayPemesanan.get(PosisiPemesanan).getNomor_Pemesanan())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    txtPetunjukPembayaran.setEnabled(true);
                                    txtPetunjukPembayaran.setLinksClickable(true);
                                    txtPetunjukPembayaran.setMovementMethod(LinkMovementMethod.getInstance());
                                    txtPetunjukPembayaran.setText(getString(R.string.Anda_dapat_Mengunduh_Bukti_Pembayarannya) + "\n"  +
                                            Html.fromHtml("<b><a href=\"" + dataSnapshot.child("Bukti_Pembayaran").getValue() + "\">" +
                                                    getString(R.string.Unduh_Bukti_Pembayaran) + "</a></b>"));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
            }
            else{
                txtPetunjukPembayaran.setText(getString(R.string.Pesanan_Anda_Akan_di_Batalkan_Otomatis_Sehari_Setelah_Tanggal_Masuk) + "\n" + getString(R.string.Anda_dapat_Mengubah_Tanggal_Masuk));
            }
        }
    }

    public void mengambilDataKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(arrayPemesanan.get(PosisiPemesanan).getID_Kost());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Nama_Kost            = String.valueOf(snapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue());
                String Deskripsi_Kost       = String.valueOf(snapshot.child("Data_Umum_Kost").child("Deskripsi_Kost").getValue());
                String Total_Kamar          = String.valueOf(snapshot.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Total_Kamar").getValue());
                String Sisa_Kamar           = String.valueOf(snapshot.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Sisa_Kamar").getValue());
                String Mayoritas_Penghuni   = String.valueOf(snapshot.child("Data_Umum_Kost").child("Mayoritas_Penghuni").getValue());
                String Harga_Kost_Hari      = String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Hari").getValue());
                String Harga_Kost_Bulan     = String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Bulan").getValue());
                String Harga_Kost_Tahun     = String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Tahun").getValue());
                String Peraturan_Kost       = String.valueOf(snapshot.child("Data_Umum_Kost").child("Peraturan_Kost").getValue());
                String Pemilik              = String.valueOf(snapshot.child("Pemilik").getValue());
                String Kecamatan            = String.valueOf(snapshot.child("Lokasi_Kost").child("Kecamatan").getValue());
                String Desa                 = String.valueOf(snapshot.child("Lokasi_Kost").child("Desa").getValue());
                String Alamat_Kost          = String.valueOf(snapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue());
                String Latitude             = String.valueOf(snapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Latitude").getValue());
                String Longitude            = String.valueOf(snapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Longitude").getValue());
                LatLng latLng               = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
                double jarak                = Jarak(lokasiTerakhir, latLng);
                String Jarak                = String.format(Locale.getDefault(), "%.2f", jarak) + getString(R.string.km);

                ArrayList<String> Nama_Kamar        = new ArrayList<>();
                ArrayList<String> Fasilitas_Bersama = new ArrayList<>();
                ArrayList<String> Fasilitas_Sekitar = new ArrayList<>();
                ArrayList<String> Dekat_Dengan      = new ArrayList<>();
                ArrayList<String> Foto_Kost         = new ArrayList<>();
                ArrayList<String> Video_Kost        = new ArrayList<>();

                for(DataSnapshot DS : snapshot.child("Data_Umum_Kost").child("Nama_Kamar").getChildren()){
                    Nama_Kamar.add(String.valueOf(DS.getValue()));
                }

                for(DataSnapshot DS : snapshot.child("Fasilitas_Bersama").getChildren()){
                    Fasilitas_Bersama.add(String.valueOf(DS.getValue()));
                }

                for(DataSnapshot DS : snapshot.child("Fasilitas_Sekitar").getChildren()){
                    Fasilitas_Sekitar.add(String.valueOf(DS.getValue()));
                }

                for(DataSnapshot DS : snapshot.child("Dekat_Dengan").getChildren()){
                    Dekat_Dengan.add(String.valueOf(DS.getValue()));
                }

                for(DataSnapshot DS : snapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                    Foto_Kost.add(String.valueOf(DS.getValue()));
                }

                for(DataSnapshot DS : snapshot.child("Foto_Video_Kost").child("Video").getChildren()){
                    Video_Kost.add(String.valueOf(DS.getValue()));
                }

                txtDeskripsiKostPemesanan.setText(Deskripsi_Kost);
                arrayDataKost.add(new ArrayDataKost(Nama_Kost, Deskripsi_Kost, Total_Kamar, Sisa_Kamar, Mayoritas_Penghuni, Harga_Kost_Hari,
                        Harga_Kost_Bulan, Harga_Kost_Tahun, Peraturan_Kost, Pemilik, Kecamatan, Desa, Alamat_Kost, Jarak, Nama_Kamar, Fasilitas_Bersama,
                        Fasilitas_Sekitar, Dekat_Dengan, Foto_Kost, Video_Kost));
                AdapterDataKost.arrayDataKost.addAll(arrayDataKost);

                lihatPetaKost(Latitude, Longitude);
                chatPemilikKost();
                lihatKostSelengkapnya();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private double Jarak(LatLng lokasiTerdekat, LatLng cariLokasi){
        double longDiff = lokasiTerdekat.longitude - cariLokasi.longitude;
        double Jarak    = Math.sin(Math.toRadians(lokasiTerdekat.latitude)) * Math.sin(Math.toRadians(cariLokasi.latitude)) +
                Math.cos(Math.toRadians(lokasiTerdekat.latitude)) * Math.cos(Math.toRadians(cariLokasi.latitude)) *
                        Math.cos(Math.toRadians(longDiff));
        Jarak           = Math.acos(Jarak);
        Jarak           = Math.toDegrees(Jarak);
        Jarak           = Jarak * 60 * 1.1515;
        Jarak           = Jarak * 1.609344;

        return Jarak;
    }

    public void lihatPetaKost(final String Latitude, final String Longitude){
        txtLihatPetaPemesanan.setEnabled(true);
        txtLihatPetaPemesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lokasi       = "http://maps.google.com/maps?q=loc:" + Double.parseDouble(Latitude) + "," + Double.parseDouble(Longitude) + " (" + "" + ")";
                Intent LokasiKost   = new Intent(Intent.ACTION_VIEW, Uri.parse(lokasi));

                LokasiKost.setPackage("com.google.android.apps.maps");
                startActivity(LokasiKost);
            }
        });
    }

    public void chatPemilikKost(){
        btnChatPemilikKostPemesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityPemesanan.this, ActivityPesan.class).putExtra("Posisi", 0));
            }
        });
    }

    public void lihatKostSelengkapnya(){
        btnLihatKostSelengkapnya.setEnabled(true);
        btnLihatKostSelengkapnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityPemesanan.this, ActivitySelengkapnya.class).putExtra("Posisi", 0));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void tanggalMasukKost(){
        txtTanggalMasukPemesanan.setText(arrayPemesanan.get(PosisiPemesanan).getTanggal_Masuk() + " >");
        LLTanggalMasukPemesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog DPD = new DatePickerDialog(ActivityPemesanan.this, android.R.style.ThemeOverlay_Material_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Hari    = dayOfMonth;
                        Bulan   = month;
                        Tahun   = year;

                        calendar.set(year, month, dayOfMonth);

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
                        databaseReference.orderByChild("Nomor_Pemesanan").equalTo(arrayPemesanan.get(PosisiPemesanan).getNomor_Pemesanan())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            dataSnapshot.child("Tanggal_Masuk")
                                                    .getRef().setValue(new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(calendar.getTime()));
                                            txtTanggalMasukPemesanan.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy >",
                                                    Locale.getDefault()).format(calendar.getTime()));
                                            arrayPemesanan.get(PosisiPemesanan).setTanggal_Masuk(new SimpleDateFormat("EEEE, dd MMMM yyyy",
                                                    Locale.getDefault()).format(calendar.getTime()));
                                            new AdapterPemesanan(ActivityPemesanan.this, arrayPemesanan).notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });

                    }
                }, Tahun, Bulan, Hari);

                DPD.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                DPD.updateDate(Tahun, Bulan, Hari);
                DPD.setCanceledOnTouchOutside(false);
                DPD.show();
            }
        });
    }

    public void metodePembayaranKost(){
        if(arrayPemesanan.get(PosisiPemesanan).getMetode_Pembayaran().equals("")){
            txtMetodePembayaran.setText(R.string.Pilih_Metode_Pembayaran_);
            txtNomorPembayaran.setText("");
            LLNomorPembayaran.setVisibility(View.GONE);
        }
        else{
            txtMetodePembayaran.setText(arrayPemesanan.get(PosisiPemesanan).getMetode_Pembayaran());
            txtNomorPembayaran.setText(arrayPemesanan.get(PosisiPemesanan).getNomor_Pembayaran());
            LLNomorPembayaran.setVisibility(View.VISIBLE);
        }

        LLMetodePembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MidtransSDK.getInstance().setTransactionRequest(transactionRequest(arrayPemesanan.get(PosisiPemesanan).getNomor_Pemesanan(),
                        Integer.parseInt(arrayPemesanan.get(PosisiPemesanan).getHarga_Kost().replace("Rp. ", "").replace(".", "")),
                        1, arrayPemesanan.get(PosisiPemesanan).getNama_Pemesan()));
                MidtransSDK.getInstance().startPaymentUiFlow(ActivityPemesanan.this);
            }
        });
    }

    public void pilihMetodePembayaran(){
        SdkUIFlowBuilder
                .init()
                .setContext(ActivityPemesanan.this)
                .setMerchantBaseUrl("https://ekostkarawang.herokuapp.com/index.php/")
                .setClientKey("SB-Mid-client-beg78mpnlQ32SwP7")
                .setTransactionFinishedCallback(ActivityPemesanan.this)
                .setColorTheme(new CustomColorTheme("#283F31", "#283F31", "#283F31"))
                .setUIkitCustomSetting(uiKitCustomSetting())
                .enableLog(true)
                .setLanguage("id")
                .buildSDK();
    }

    public UIKitCustomSetting uiKitCustomSetting(){
        UIKitCustomSetting uiKitCustomSetting = new UIKitCustomSetting();
        uiKitCustomSetting.setEnabledAnimation(true);
        uiKitCustomSetting.setSkipCustomerDetailsPages(true);

        return uiKitCustomSetting;
    }

    public CustomerDetails customerDetails(){
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName(SP.getString("NamaAkun", ""));
        customerDetails.setPhone(SP.getString("NoHPAkun", ""));
        customerDetails.setEmail(SP.getString("EmailAkun", ""));
        customerDetails.setCustomerIdentifier(SP.getString("MasukSebagai", ""));

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setAddress("Jl. Abimanyu 6 Perumnas Bumi Teluk Jambe Timur");
        shippingAddress.setCity("Karawang");
        shippingAddress.setPostalCode("41361");
        shippingAddress.setPhone("081373614049");
        customerDetails.setShippingAddress(shippingAddress);

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setAddress("Jl. Abimanyu 6 Perumnas Bumi Teluk Jambe Timur");
        billingAddress.setCity("Karawang");
        billingAddress.setPostalCode("41361");
        billingAddress.setPhone("081373614049");
        customerDetails.setBillingAddress(billingAddress);

        return customerDetails;
    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("deprecation")
    public TransactionRequest transactionRequest(String Nomor_Pemesanan, int Harga_Kost, int Jumlah, String Nama_Pemesan){
        long ID_Pembayaran                      = System.currentTimeMillis();
        double Total_Amount                     = Harga_Kost * Jumlah;
        TransactionRequest transactionRequest   = new TransactionRequest( ID_Pembayaran + " ", Total_Amount);

        transactionRequest.setCustomerDetails(customerDetails());
        transactionRequest.setPromoEnabled(true);

        ItemDetails itemDetails             = new ItemDetails(Nomor_Pemesanan, Harga_Kost, Jumlah, Nama_Pemesan);
        ArrayList<ItemDetails> arrayList    = new ArrayList<>();

        arrayList.add(itemDetails);
        transactionRequest.setItemDetails(arrayList);

        CreditCard creditCard = new CreditCard();
        creditCard.setSaveCard(false);
        creditCard.setChannel(CreditCard.MIGS);
        creditCard.setAuthentication(CreditCard.RBA);
        creditCard.setBank(BankType.MANDIRI);
        transactionRequest.setCreditCard(creditCard);

        return transactionRequest;
    }

    @Override
    public void onTransactionFinished(final TransactionResult transactionResult) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
        databaseReference.orderByChild("Nomor_Pemesanan").equalTo(arrayPemesanan.get(PosisiPemesanan).getNomor_Pemesanan())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(transactionResult.getResponse() != null){
                                txtMetodePembayaran.setText(transactionResult.getResponse().getPaymentType().toUpperCase().replace("-", " "));
                                txtNomorPembayaran.setText(transactionResult.getResponse().getOrderId());
                                LLNomorPembayaran.setVisibility(View.VISIBLE);

                                dataSnapshot.child("Nomor_Pembayaran").getRef().setValue(txtNomorPembayaran.getText().toString());
                                dataSnapshot.child("Metode_Pembayaran").getRef().setValue(txtMetodePembayaran.getText().toString());

                                switch(transactionResult.getStatus()){
                                    case TransactionResult.STATUS_SUCCESS :
                                        dataSnapshot.child("Status_Pemesanan").getRef().setValue("Transaksi Selesai");
                                        dataSnapshot.child("Bukti_Pembayaran").getRef().setValue(transactionResult.getResponse().getPdfUrl());
                                        Toast.makeText(ActivityPemesanan.this,
                                                getString(R.string.Transaksi_Selesai) + "\n" + getString(R.string.Anda_dapat_Mengunduh_Bukti_Pembayarannya), Toast.LENGTH_SHORT).show();
                                        txtStatusPembayaran.setText(getString(R.string.Transaksi_Selesai));
                                        break;
                                    case TransactionResult.STATUS_PENDING :
                                        dataSnapshot.child("Status_Pemesanan").getRef().setValue("Transaksi di Tunda");
                                        Toast.makeText(ActivityPemesanan.this, R.string.Transaksi_di_Tunda, Toast.LENGTH_SHORT).show();
                                        txtStatusPembayaran.setText(getString(R.string.Transaksi_di_Tunda));
                                        break;
                                    case TransactionResult.STATUS_FAILED :
                                        dataSnapshot.child("Status_Pemesanan").getRef().setValue("Transaksi Gagal");
                                        Toast.makeText(ActivityPemesanan.this, R.string.Transaksi_Gagal, Toast.LENGTH_SHORT).show();
                                        txtStatusPembayaran.setText(getString(R.string.Transaksi_Gagal));
                                        break;
                                }
                                transactionResult.getResponse().getValidationMessages();
                            }
                            else if(transactionResult.isTransactionCanceled()){
                                dataSnapshot.child("Status_Pemesanan").getRef().setValue("Menunggu Pembayaran");
                                Toast.makeText(ActivityPemesanan.this, R.string.Menunggu_Pembayaran, Toast.LENGTH_SHORT).show();
                                txtStatusPembayaran.setText(getString(R.string.Menunggu_Pembayaran));
                            }
                            else{
                                dataSnapshot.child("Status_Pemesanan").getRef().setValue("Transaksi Gagal");
                                Toast.makeText(ActivityPemesanan.this, R.string.Transaksi_Gagal, Toast.LENGTH_SHORT).show();
                                txtStatusPembayaran.setText(getString(R.string.Transaksi_Gagal));
                            }

                            arrayPemesanan.get(PosisiPemesanan).setTanggal_Masuk(new SimpleDateFormat("EEEE, dd MMMM yyyy",
                                    Locale.getDefault()).format(calendar.getTime()));
                            arrayPemesanan.get(PosisiPemesanan).setStatus_Pemesanan(txtStatusPembayaran.getText().toString());
                            arrayPemesanan.get(PosisiPemesanan).setMetode_Pembayaran(txtMetodePembayaran.getText().toString());
                            arrayPemesanan.get(PosisiPemesanan).setNomor_Pembayaran(txtNomorPembayaran.getText().toString());
                            new AdapterPemesanan(ActivityPemesanan.this, arrayPemesanan).notifyDataSetChanged();
                            PetunjukPembayaran();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public void batalkanPemesananKost(){
        btnBatalkanPemesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog BSD = new BottomSheetDialog(ActivityPemesanan.this);
                BSD.setContentView(R.layout.bottom_batalkan_pesananan);
                BSD.setCanceledOnTouchOutside(true);
                BSD.show();

                final RadioGroup RGAlasanBatalkanPesanan        = BSD.findViewById(R.id.RGAlasanBatalkanPesanan);
                final RadioButton RBAlasanKamarTidakSesuai      = BSD.findViewById(R.id.RBAlasanKamarTidakSesuai);
                final RadioButton RBAlasanFasilitasTidakSesuai  = BSD.findViewById(R.id.RBAlasanFasilitasTidakSesuai);
                final RadioButton RBAlasanTidakSesuaiDeskripsi  = BSD.findViewById(R.id.RBAlasanTidakSesuaiDeskripsi);
                final RadioButton RBAlasanLainnya               = BSD.findViewById(R.id.RBAlasanLainnya);
                final EditText txtAlasanLainnya                 = BSD.findViewById(R.id.txtAlasanLainnya);
                final Button btnKonfirmasiBatalkan              = BSD.findViewById(R.id.btnKonfirmasiBatalkan);

                PushDownAnim.setPushDownAnimTo(Objects.requireNonNull(btnKonfirmasiBatalkan));
                Objects.requireNonNull(RGAlasanBatalkanPesanan).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Objects.requireNonNull(txtAlasanLainnya).setEnabled(false);
                        txtAlasanLainnya.getText().clear();
                        btnKonfirmasiBatalkan.setEnabled(true);
                        btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(ActivityPemesanan.this, R.color.colorPrimary));

                        if(Objects.requireNonNull(RBAlasanKamarTidakSesuai).isChecked()){
                            Alasan_Pembatalan = "Kamar Tidak Sesuai";
                        }
                        else if(Objects.requireNonNull(RBAlasanFasilitasTidakSesuai).isChecked()){
                            Alasan_Pembatalan = "Fasilitas Tidak Sesuai";
                        }
                        else if(Objects.requireNonNull(RBAlasanTidakSesuaiDeskripsi).isChecked()){
                            Alasan_Pembatalan = "Tidak Sesuai Deskripsi";
                        }
                        else if(Objects.requireNonNull(RBAlasanLainnya).isChecked()){
                            Alasan_Pembatalan = "Alasan Lainnya";

                            txtAlasanLainnya.setEnabled(true);
                            txtAlasanLainnya.requestFocus();
                            btnKonfirmasiBatalkan.setEnabled(false);
                            btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(ActivityPemesanan.this, R.color.abuabu));
                        }
                    }
                });

                Objects.requireNonNull(txtAlasanLainnya).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(txtAlasanLainnya.getText().toString().isEmpty()){
                            btnKonfirmasiBatalkan.setEnabled(false);
                            btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(ActivityPemesanan.this, R.color.abuabu));
                        }
                        else{
                            btnKonfirmasiBatalkan.setEnabled(true);
                            btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(ActivityPemesanan.this, R.color.colorPrimary));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });

                btnKonfirmasiBatalkan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
                        databaseReference.orderByChild("Nomor_Pemesanan").equalTo(arrayPemesanan.get(PosisiPemesanan).getNomor_Pemesanan())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            dataSnapshot.child("Status_Pemesanan").getRef().setValue("Pesananan di Batalkan");
                                            dataSnapshot.child("Alasan_Pembatalan").getRef().setValue(Alasan_Pembatalan + txtAlasanLainnya.getText().toString());

                                            txtStatusPembayaran.setText(R.string.Pesananan_di_Batalkan);
                                            txtStatusPembayaran.setTextColor(btnBatalkanPemesanan.getTextColors());
                                            LLTanggalMasukPemesanan.setEnabled(false);
                                            LLMetodePembayaran.setEnabled(false);
                                            btnBatalkanPemesanan.setVisibility(View.GONE);

                                            arrayPemesanan.get(PosisiPemesanan).setStatus_Pemesanan(txtStatusPembayaran.getText().toString());
                                            new AdapterPemesanan(ActivityPemesanan.this, arrayPemesanan).notifyDataSetChanged();
                                            PetunjukPembayaran();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                        BSD.dismiss();
                    }
                });
            }
        });
    }
}