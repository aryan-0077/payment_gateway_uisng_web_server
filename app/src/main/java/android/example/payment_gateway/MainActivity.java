package android.example.payment_gateway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnPay;
    ProgressBar progressBar;
    Dialog loadingDialog;
    Dialog paymentMethodDialog;
    ImageButton patym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         //Loading ICON here ...
//        progressBar = (ProgressBar)findViewById(R.id.spinKit);
//        FadingCircle fadingCircle = new FadingCircle();
//        progressBar.setIndeterminateDrawable(fadingCircle);
//
//        // loading dialog
//        loadingDialog = new Dialog(MainActivity.this);
//        loadingDialog.setContentView(R.layout.loading);
//        loadingDialog.setCancelable(false);
//        loadingDialog.getWindow().setBackgroundDrawable(
//                new ColorDrawable(0));
//        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.
//                WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        //loadingDialog.show();
//        // loading dialog


        paymentMethodDialog = new Dialog(MainActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        patym = paymentMethodDialog.findViewById(R.id.
                patym);

//        Drawable d = new ColorDrawable(Color.BLACK);
//        d.setAlpha(130);
//        paymentMethodDialog.getWindow().setBackgroundDrawable(d);

        paymentMethodDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.
               WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);



        btnPay = (Button)findViewById(R.id.btnPay);
        patym = paymentMethodDialog.findViewById(R.id.
                patym);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"Pay Now",
//                        Toast.LENGTH_SHORT).show();
                paymentMethodDialog.show();

            }
        });

//        patym.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paymentMethodDialog.dismiss();
//                Intent i = getPackageManager().getLaunchIntentForPackage("net.one97.paytm");
//                startActivity(i);
//            }
//        });



        patym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethodDialog.dismiss();
                //loadingDialog.show();

                // runtime permissions
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }

                final String M_id = "WzQosX92181788577644";
    // Not a comment
    //            String cust_id = FirebaseAuth.getInstance().getUid();
                final String order_id = UUID.randomUUID().toString().substring(0,28);
                String url = "https://paytm0077.000webhostapp.com/Paytm/generateChecksum.php";
                final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("CHECKSUMHASH")){
                                String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
                                PaytmPGService paytmPGService = PaytmPGService.getStagingService();

                                HashMap<String, String> paramMap = new HashMap<String,String>();
                                paramMap.put( "MID" , M_id);
                                paramMap.put( "ORDER_ID" ,order_id);
                                paramMap.put( "CUST_ID" , "7");
                                paramMap.put( "CHANNEL_ID" , "WAP");
                                paramMap.put( "TXN_AMOUNT" , "100");
                                paramMap.put( "WEBSITE" , "WEBSTAGING");
                                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                                paramMap.put( "CALLBACK_URL", callBackUrl);
                                paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);

                                PaytmOrder order = new PaytmOrder(paramMap);

                                paytmPGService.initialize(order,null);
                                paytmPGService.startPaymentTransaction(MainActivity.this, true,
                                        true, new PaytmPaymentTransactionCallback() {
                                            @Override
                                            public void onTransactionResponse(Bundle inResponse) {
                                                Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void networkNotAvailable() {
                                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void clientAuthenticationFailed(String inErrorMessage) {
                                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void someUIErrorOccurred(String inErrorMessage) {
                                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void onBackPressedCancelTransaction() {
                                                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();

                                            }

                                            @Override
                                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                                Toast.makeText(getApplicationContext(), " Transaction Cancelled " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            // loadingDialog.dismiss();
                        Toast.makeText(MainActivity.this ,
                                "Something Went Wrong" ,
                                Toast.LENGTH_LONG);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , M_id);
                        paramMap.put( "ORDER_ID" ,order_id);
                        paramMap.put( "CUST_ID" , "7");
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , "100");
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CALLBACK_URL", callBackUrl);

                        return paramMap;
                    }
                };

                requestQueue.add(stringRequest);

            }
        }); 


    }

    @Override
    protected void onPause() {
        super.onPause();
        //loadingDialog.dismiss();
    }
}
