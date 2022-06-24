package com.aplikasi_ekostkarawang.Pemesanan;

import android.annotation.SuppressLint;

import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;

import java.util.ArrayList;

public class DataCustomer {

    public static CustomerDetails customerDetails(){
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName("Firman");
        customerDetails.setPhone("081373614049");
        customerDetails.setEmail("ekostkarawang@gmail.com");
        return customerDetails;
    }

    @SuppressLint("WrongConstant")
    public static TransactionRequest transactionRequest(String ID, int Harga, int Jumlah, String Nama){
        TransactionRequest transactionRequest = new TransactionRequest(System.currentTimeMillis() + " ", 20000);
        transactionRequest.setCustomerDetails(customerDetails());
        ItemDetails itemDetails = new ItemDetails(ID, Harga, Jumlah, Nama);

        ArrayList<ItemDetails> arrayList = new ArrayList<>();
        arrayList.add(itemDetails);
        transactionRequest.setItemDetails(arrayList);

        CreditCard creditCard = new CreditCard();
        creditCard.setSaveCard(false);
        creditCard.setAuthentication(CreditCard.MIGS);
        creditCard.setBank(BankType.MANDIRI);
        transactionRequest.setCreditCard(creditCard);

        return transactionRequest;
    }

}