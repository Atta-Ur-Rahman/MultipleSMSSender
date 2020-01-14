package com.example.mycontacts.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.R;
import com.example.mycontacts.dataBase.ContactCurd;
import com.example.mycontacts.dataModel.GroupDataModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMessageActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private boolean validGroup = false;
    private String msgGroup;
    @BindView(R.id.sentMessageGroup)
    TextView sendMessageGroup;
    @BindView(R.id.edit_TextGroup)
    EditText msgTextGroup;
    ContactCurd contactCurd;
    List<GroupDataModel> groupDataModels = new ArrayList<>();
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        ButterKnife.bind(this);
        contactCurd = new ContactCurd(this);

        sharedPreferences = getSharedPreferences("abcd", Context.MODE_PRIVATE);
        String groupName = sharedPreferences.getString("ide", "");

        groupDataModels = contactCurd.getGroupContact(groupName, this);

        phone = groupDataModels.toString();

        sendMessageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValidGroup();
                sentdSMSGroup(phone, msgGroup);
            }
        });
        customActionBar();
    }

    private boolean isValidGroup() {
        validGroup = true;
        msgGroup = msgTextGroup.getText().toString();
        if (msgGroup.isEmpty()) {
            msgTextGroup.setError("ERROR");
        } else {
            msgTextGroup.setError(null);
        }
        return validGroup;
    }

    private void sentdSMSGroup(String phone, String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(phone, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ErrVar) {
                Toast.makeText(getApplicationContext(), ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
            }
        }
    }

    public void customActionBar() {
        sharedPreferences = getSharedPreferences("abcd", Context.MODE_PRIVATE);
        String groupName = sharedPreferences.getString("ide", "");

        ActionBar mActionBar = ((AppCompatActivity) GroupMessageActivity.this).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setTitle(groupName);
        mActionBar.setElevation(0);

        mActionBar.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.deleteGroupMenu:

                sharedPreferences = getSharedPreferences("abcd", Context.MODE_PRIVATE);
                String groupName = sharedPreferences.getString("ide", "");
                contactCurd.deleteGroup(groupName);
                finish();
                break;
            case R.id.contactsMenu:
                Intent i = new Intent(GroupMessageActivity.this, GroupContactActivity.class);
                startActivity(i);
                break;
        }
        return false;
    }

}
