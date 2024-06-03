# Xmit
- view the contents of xmit files
- view xmit files [stored as PDS members](resources/embedded.md)
- view [compressed xmit files](resources/compressed.md) without decompressing
- selectable [codepages](resources/view.md) and [fonts](resources/fonts.md)
- [filter/search](resources/filter.md) PDS members
- [extract](resources/extract.md) individual PDS members
- view AWS tape files

## Sample screens
### MacOS
![Mac](resources/xmit-osx.png?raw=true "Mac")
### Windows
![Windows](resources/xmit-win.png?raw=true "Windows")
### Linux
![Linux](resources/xmit-linux.png?raw=true "Linux")

## Installation
- Download and install Java >= 17
- Download [XmitApp](https://github.com/GianoniLorenzo/Xmit/releases) (CLI or GUI). 

#### Launch CLI app

```
Usage: xmit-cli.jar source <options>
 -d,--dataset <arg>   Dataset argument
 -dir,--directory     Extract the whole directory
 -m,--members <arg>   Members argument
 -o,--output <arg>    Output folder

```

##### Export all members of an xmit file

```
java -jar xmit-cli.jar <file.xmit> -o <output-dir>
```

#### Launch GUI App on MacOS - Linux - Windows 

```
java -jar /path/to/xmit-gui.jar
```  

### First execution
Read the helpful message.  
<img src="resources/xmit-folder1.png" alt="alert" width="500"/>  
Specify the location of your xmit files. Note that this must be a FOLDER, not a file. The specified folder may contain subfolders, these will all appear in the file tree within the application.  
<img src="resources/xmit-folder2.png" alt="file dialog" width="800"/>  
This will remain the xmit file folder until you change it.  
