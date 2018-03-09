# FilePicker
Android File Selection Library (Android API 21+)<br /><br />

## USAGE INSTRUCTIONS
* Add jitpack repository to your root build.gradle file
```
allprojects 
{
	repositories 
	{	
		...
		maven { url 'https://jitpack.io' }
	}
}
```
* Add the dependency
```
dependencies 
{
	compile 'com.github.amogh93:FilePicker:2aa41268e6'
}
```
* Start FilePickerActivity
```
private static final int FILE_PICKER=41;
startActivityForResult(new Intent(this,FilePickerActivity.class),FILE_PICKER);
```
* Get list of selected files
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
    if (requestCode == FILE_PICKER)
    {
        if (resultCode == RESULT_OK)
        {
	    StringBuilder result=new StringBuilder();
            HashMap<String,String> file_map=(HashMap)data.getSerializableExtra("fileMap");
            for(Map.Entry m:file_map.entrySet())
            {
                result.append("File name: "+m.getKey()+", path: "+m.getValue()+"\n");
            }
	}
    }
}
```

## FEATURES
* Auto permission check for storage
* Multiple file selection
* Select all button
