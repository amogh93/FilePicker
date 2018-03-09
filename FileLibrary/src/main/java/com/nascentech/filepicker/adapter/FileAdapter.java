package com.nascentech.filepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nascentech.filepicker.R;
import com.nascentech.filepicker.data.TempStorage;
import com.nascentech.filepicker.model.FileStructure;

import java.util.List;

/**
 * Created by Amogh on 13-12-2017.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FilesViewHolder>
{
    private List<FileStructure> file_list;
    private Context context;

    public FileAdapter(List<FileStructure> list)
    {
        file_list=list;
    }
    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_layout, parent, false);
        context=itemView.getContext();
        return new FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilesViewHolder holder, int position)
    {
        FileStructure file=file_list.get(position);
        holder.dirName.setText(file.getName());
        holder.dirCreationDate.setText(file.getCreationDate());
        if(file.isDirectory())
        {
            holder.dirImage.setImageResource(R.drawable.ic_folder_new);
        }
        else
        {
            holder.dirImage.setImageResource(R.drawable.ic_file_new);
        }

        if(TempStorage.isLongClicked && !file.isDirectory())
        {
            holder.selected.setVisibility(View.VISIBLE);
            if(file.isSelected())
            {
                holder.selected.setChecked(true);
            }
            else
            {
                holder.selected.setChecked(false);
            }
        }
        else
        {
            holder.selected.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount()
    {
        if(file_list.size()>0)
        {
            return file_list.size();
        }
        return  0;
    }

    public class FilesViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView dirImage;
        public CheckBox selected;
        public TextView dirName,dirCreationDate;
        public RelativeLayout relativeLayout;

        public FilesViewHolder(View view)
        {
            super(view);
            dirImage=view.findViewById(R.id.dirImage);
            selected=view.findViewById(R.id.selected);
            dirName=view.findViewById(R.id.dirName);
            dirCreationDate=view.findViewById(R.id.dirCreationDate);
            relativeLayout=view.findViewById(R.id.name_layout);
            selected.setVisibility(View.INVISIBLE);

            selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    int position=getAdapterPosition();

                    if(isChecked)
                    {
                        file_list.get(position).setSelected(true);
                    }
                    else
                    {
                        file_list.get(position).setSelected(false);
                    }
                }
            });
        }
    }
}
