package com.nascentech.filepicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nascentech.filepicker.adapter.FileAdapter;
import com.nascentech.filepicker.data.TempStorage;
import com.nascentech.filepicker.listeners.RecyclerTouchListener;
import com.nascentech.filepicker.model.FileStructure;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FilePickerActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private FileAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<FileStructure> file_list=new ArrayList<>();
    private List<File> parent=new ArrayList<>();
    private final static String PATH = Environment.getExternalStorageDirectory().toString();
    private TextView rowCount,stackPath;
    private Menu mMenu;
    private static final int REQUEST_CODE_STORAGE=66;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        rowCount=findViewById(R.id.rowCount);
        stackPath=findViewById(R.id.path_stack);
        mRecyclerView=findViewById(R.id.files_recycler_view);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),mRecyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                final FileStructure file=file_list.get(position);
                if(!TempStorage.isLongClicked)
                {
                    if(file.isDirectory())
                    {
                        loadRecyclerView(new File(file.getPath()));
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position)
            {
                if(file_list.get(position).isDirectory())
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(FilePickerActivity.this);
                    builder.setTitle("Not allowed");
                    builder.setMessage("Folder selection is not allowed, only files can be selected");
                    builder.setPositiveButton("CLOSE",null);
                    builder.setCancelable(false);
                    builder.create().show();
                }
                else
                {
                    TempStorage.isLongClicked=true;
                    TempStorage.noStack=true;
                    mMenu.getItem(0).setVisible(true);
                    mMenu.getItem(1).setVisible(true);

                    file_list.get(position).setSelected(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }));

        mAdapter=new FileAdapter(file_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(FilePickerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(FilePickerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            }
            else
            {
                loadRecyclerView(new File(PATH));
            }
        }
        else
        {
            loadRecyclerView(new File(PATH));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.select_files)
        {
            int selected_files_count=0;

            for(FileStructure structure:file_list)
            {
                if(structure.isSelected())
                {
                    selected_files_count++;
                }
            }
            if(selected_files_count==0)
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Select at least one file to proceed");
                builder.setCancelable(false);
                builder.setPositiveButton("CLOSE",null);
                builder.create().show();
            }
            else
            {
                TempStorage.isLongClicked=false;
                TempStorage.noStack=false;
                HashMap<String,String> fileMap=new HashMap<>();
                for(FileStructure f:file_list)
                {
                    if(f.isSelected())
                    {
                        fileMap.put(f.getName(),f.getPath());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("fileMap",fileMap);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else if(item.getItemId()==R.id.select_all)
        {
            try
            {
                for (FileStructure f:file_list)
                {
                    if(!f.isDirectory())
                    {
                        f.setSelected(true);
                    }
                }
            }
            finally
            {
                mAdapter.notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        mMenu=menu;
        getMenuInflater().inflate(R.menu.select_menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        return true;
    }

    private void populateList(File file)
    {
        file_list.clear();
        if(!TempStorage.noStack)
        {
            parent.add(file);
        }
        File[] dirs=file.listFiles();
        List<FileStructure> dir=new ArrayList<>();
        List<FileStructure> files=new ArrayList<>();
        try
        {
            for(File f:dirs)
            {
                if(!f.getName().startsWith("."))
                {
                    Date latModificationDate=new Date(f.lastModified());
                    DateFormat formater = DateFormat.getDateTimeInstance();
                    String date = formater.format(latModificationDate);

                    if(f.isDirectory())
                    {
                        File[] sub_items=f.listFiles();
                        FileStructure fileStructure=new FileStructure(f.getName(),sub_items.length+" items, "+date,f.getAbsolutePath(),true);
                        dir.add(fileStructure);
                    }
                    else
                    {
                        FileStructure fileStructure=new FileStructure(f.getName(),date,f.getAbsolutePath(),false);
                        files.add(fileStructure);
                    }
                }
            }
            Collections.sort(dir);
            Collections.sort(files);

            file_list.addAll(dir);
            file_list.addAll(files);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(file_list.size()==0)
            {
                rowCount.setVisibility(View.VISIBLE);
                rowCount.setText("No files to show");
            }
            else
            {
                rowCount.setVisibility(View.GONE);
            }
        }
    }

    private void loadRecyclerView(File file)
    {
        try
        {
            populateList(file);
        }
        finally
        {
            mAdapter.notifyDataSetChanged();
        }
        String stack="";
        int i=0;
        if(parent.size()<5)
        {
            for(File f:parent)
            {
                if(i==0)
                {
                    stack+="sdcard";
                }
                else
                {
                    stack+="/"+f.getName();
                }
                i++;
            }
        }
        else
        {
            stack=".../"+parent.get(parent.size()-1).getName();
        }
        stackPath.setText(stack);
    }

    @Override
    public void onBackPressed()
    {
        if(TempStorage.isLongClicked)
        {
            TempStorage.isLongClicked=false;
            TempStorage.noStack=false;
            mMenu.getItem(0).setVisible(false);
            mMenu.getItem(1).setVisible(false);
            if(parent.size()>1)
            {
                File open_file=parent.get(parent.size()-1);
                parent.remove(parent.size()-1);
                loadRecyclerView(open_file);
            }
            else
            {
                parent.clear();
                loadRecyclerView(new File(PATH));
            }
        }
        else
        {
            if(parent.size()>1)
            {
                File f=parent.get(parent.size()-2);
                parent.remove(parent.size()-1);
                parent.remove(parent.size()-1);
                loadRecyclerView(f);
            }
            else
            {
                finish();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadRecyclerView(new File(PATH));
                }
                else
                {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        TempStorage.isLongClicked=false;
        super.onDestroy();
    }
}
