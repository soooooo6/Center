package com.example.center;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.os.Environment;

public class ZipFolder {
	List<String> fileList;
	
	// ������ ��ġ�� �������� ����
	private static final String OUTPUT_ZIP_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/npki.zip";
	// ������ ���� ��ġ ����
	private static final String SOURCE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NPKI";
	
	ZipFolder(){
		fileList = new ArrayList<String>();
	}
	
	public static void main( String[] args ){
		ZipFolder ZipFolder = new ZipFolder();
		ZipFolder.generateFileList(new File(SOURCE_FOLDER));
		ZipFolder.zipIt(OUTPUT_ZIP_FILE);
	}
	
	public void zipIt(String zipFile){
		byte[] buffer = new byte[1024];
		try{
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			System.out.println("ZipFile : " + zipFile);
			
			for(String file : this.fileList){
				System.out.println("File �߰� : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				
				FileInputStream in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
				
				int len;
				while((len = in.read(buffer)) > 0){
					zos.write(buffer, 0, len);
				}
				in.close();
			}
			zos.closeEntry();
			zos.close();
			System.out.println("���� �Ϸ�");
		} catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void generateFileList(File node){
		if(node.isFile()){
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}
		
		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename));
			}
		}
	}
	
	private String generateZipEntry(String file){
		return file.substring(SOURCE_FOLDER.length()+1, file.length());
	}

}
