package com.pathdx.utils;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import com.pathdx.constant.Constants;
import com.pathdx.constant.FolderNames;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class GoogleCloudStorageUtil.
 */
public class GoogleCloudStorageUtil {

	/** The log. */
	Logger log = LoggerFactory.getLogger(GoogleCloudStorageUtil.class);

	/** The storage. */
	private Storage storage = null;
	
	/** The Constant WHITESPACES_STRING. */
	private static final String WHITESPACES_STRING = "[\\s]";


	@Value("${projectId}")
	private String projectId;
	/**
	 * Instantiates a new google cloud storage util.
	 */
	public GoogleCloudStorageUtil() {
		storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	}

	/**
	 * Upload file.
	 *
	 * @param folderName the folder name
	 * @param filePart the file part
	 * @param bucketName the bucket name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String uploadFile(FolderNames folderName, Part filePart, final String bucketName) throws IOException {
		DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.DATE_FORMAT_GCS_UTIL);
		DateTime dt = DateTime.now(DateTimeZone.UTC);
		String dtString = dt.toString(dtf);
		String filename=filePart.getSubmittedFileName().trim().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		final String fileName = folderName.toString().toLowerCase()+ "/"+dtString + filename;
		Pattern patt = Pattern.compile(WHITESPACES_STRING); 
		Matcher mat = patt.matcher(fileName); 
		mat.replaceAll("-");

		InputStream is = filePart.getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] readBuf = new byte[4096];
		while (is.available() > 0) {
			int bytesRead = is.read(readBuf);
			os.write(readBuf, 0, bytesRead);
		}

		BlobInfo blobInfo =
				storage.create(
						BlobInfo
						.newBuilder(bucketName, fileName)
						.build(),
						os.toByteArray());
		return blobInfo.getSelfLink();
	}

	/**
	 * Delete file.
	 *
	 * @param blobName the blob name
	 * @param bucketName the bucket name
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean deleteFile(String blobName, String bucketName) throws IOException {
		BlobId blobId = BlobId.of(bucketName, blobName);
		return storage.delete(blobId);
	}


	public List<URL> getListOfOjectInBucket(String bucketName, String folderPath){
		Page<Blob> blobs =
				storage.list(
						bucketName,
						Storage.BlobListOption.prefix(folderPath),
						Storage.BlobListOption.currentDirectory());
	/*	for (Blob blob : blobs.iterateAll()) {
		//	System.out.println(blob.getName());
			//System.out.println(blob.signUrl(30000,TimeUnit.MILLISECONDS));
		}*/
		List<URL> strings = new ArrayList<>();
		int i = 0 ;
		if(folderPath.contains("clinical_image") || folderPath.contains("grossing_image")||folderPath.contains("annotated_image")){
			for (Blob blob : blobs.iterateAll()) {
				String path = blob.getName();
				if(i>0){
					strings.add(blob.signUrl(2000000, TimeUnit.MILLISECONDS));
					System.out.println("Parent Dir : " + blob.getSelfLink());
				}
				i++;
			}
		}else {
			for (Blob blob : blobs.iterateAll()) {
				String path = blob.getName();
				//			if(i>0){
				strings.add(blob.signUrl(2000000, TimeUnit.MILLISECONDS));
				// Gets the path of the object
				System.out.println("Parent Dir : " + blob.getSelfLink());
				//}
				i++;
			}
		}
		return strings ;
	}

	public List<String> getListOfFilesInBucket(String bucketName, String folderPath){
		Page<Blob> blobs =
				storage.list(
						bucketName,
						Storage.BlobListOption.prefix(folderPath),
						Storage.BlobListOption.currentDirectory());
	/*	for (Blob blob : blobs.iterateAll()) {
		//	System.out.println(blob.getName());
			//System.out.println(blob.signUrl(30000,TimeUnit.MILLISECONDS));
		}*/
		List<String> strings = new ArrayList<>();
		int i = 0 ;
		for (Blob blob : blobs.iterateAll()) {
			String path = blob.getName();
//			if(i>0){
			strings.add(blob.getSelfLink());
			// Gets the path of the object
			System.out.println("Parent Dir : "+blob.getSelfLink());
			//}
			i++;
		}

		return strings ;
	}

	public static boolean isDir(String path){
		return path.endsWith("/");
	}

	public static String getParentDir(String path){
		return path.substring(0, path.lastIndexOf("/") + 1);
	}

	/**
	 * Generate signed url.
	 *
	 * @param bucketName the bucket name
	 * @param objectName the object name
	 * @return the url
	 * @throws StorageException the storage exception
	 */
	public URL generateSignedUrl(String bucketName, String objectName){
		try {
			objectName=getObjectSelfLink(objectName);
			Map<String, String> extensionHeaders = new HashMap<>();
			extensionHeaders.put("Content-Type", "application/octet-stream");
			return
					storage.signUrl(
							BlobInfo.newBuilder(bucketName.trim(), objectName.trim()).build(),
							10000,
							TimeUnit.MINUTES,
							Storage.SignUrlOption.httpMethod(HttpMethod.GET),
							Storage.SignUrlOption.withV4Signature());
		} catch (UnsupportedEncodingException e) {
			log.error("Error while generating signed url : ",e);
		}
		return null;
	}


	/**
	 * Read file.
	 *
	 * @param blobName the blob name
	 * @param bucketName the bucket name
	 * @return the string
	 */
	public byte[] readFile(String blobName, String bucketName) {
		try {

			if(null != blobName && !blobName.isEmpty()) {

				blobName= getObjectSelfLink(blobName);
				Blob blob = storage.get(bucketName, blobName);
				//ReadChannel r = blob.reader();
				//Blob blob = storage.get(bucketName, blobName);
				return blob.getContent();
			}else {
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Error while reading from signed url : ",e);
		}
		return null;
	}

	/**
	 * check file exists.
	 *
	 * @param blobName the blob name
	 * @param bucketName the bucket name
	 * @return the string
	 */
	public boolean isFileExists(String blobName, String bucketName) {
		boolean existFlag = false;
		try {

			if(null != blobName && !blobName.isEmpty()) {

				blobName= getObjectSelfLink(blobName);
				Blob blob = storage.get(bucketName, blobName);
				existFlag = blob.exists();
			}else {
				return existFlag;
			}
		} catch (Exception e) {
			log.info("file not found: ",e.getMessage());
			return existFlag;
		}
		return existFlag;
	}

	/**
	 * Upload file.
	 *
	 * @param folderName the folder name
	 * @param file the file
	 * @param bucketName the bucket name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String uploadFile(FolderNames folderName, MultipartFile file, String bucketName) throws IOException {

		DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.DATE_FORMAT_GCS_UTIL);
		DateTime dt = DateTime.now(DateTimeZone.UTC);
		String dtString = dt.toString(dtf);
		final String fileName = folderName.toString().toLowerCase()+ "/"+dtString + file.getOriginalFilename().trim();
		Pattern patt = Pattern.compile(WHITESPACES_STRING); 
		Matcher mat = patt.matcher(fileName); 
		mat.replaceAll("-");

		InputStream is = file.getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] readBuf = new byte[10_240];
		while (is.available() > 0) {
			int bytesRead = is.read(readBuf);
			os.write(readBuf, 0, bytesRead);
		}

		BlobInfo blobInfo =
				storage.create(
						BlobInfo
						.newBuilder(bucketName, fileName)
						.build(),
						os.toByteArray());
		return blobInfo.getSelfLink();
	}

	public String uploadToStorage(MultipartFile file, BlobInfo blobInfo) throws IOException {
		// For small files:
		if (file.getSize() < 1000000) {
			storage.create(blobInfo, file.getBytes());
			return blobInfo.getSelfLink();
		}

		// For big files:
		// When content is not available or large (1MB or more) it is recommended to write it in chunks via the blob's channel writer.
		try (WriteChannel writer = storage.writer(blobInfo)) {

			byte[] buffer = new byte[10_240];
			try (InputStream input = file.getInputStream()) {
				int limit;
				while ((limit = input.read(buffer)) >= 0) {
					writer.write(ByteBuffer.wrap(buffer, 0, limit));
				}
			}

		}
		return blobInfo.getSelfLink();
	}

	
	/**
	 * Upload file.
	 *
	 * @param folderName the folder name
	 * @param file the file
	 * @param bucketName the bucket name
	 * @param originalFilename the original filename
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String uploadFile(FolderNames folderName, InputStream file, String bucketName, String originalFilename) throws IOException {

		DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.DATE_FORMAT_GCS_UTIL);
		DateTime dt = DateTime.now(DateTimeZone.UTC);
		String dtString = dt.toString(dtf);
		final String fileName = folderName.toString().toLowerCase()+ "/"+dtString + originalFilename.trim();
		Pattern patt = Pattern.compile(WHITESPACES_STRING); 
		Matcher mat = patt.matcher(fileName); 
		mat.replaceAll("-");

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] readBuf = new byte[4096];
		while (file.available() > 0) {
			int bytesRead = file.read(readBuf);
			os.write(readBuf, 0, bytesRead);
		}

		BlobInfo blobInfo =
				storage.create(
						BlobInfo
						.newBuilder(bucketName, fileName)
						//						.setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
						.build(),
						os.toByteArray());
		return blobInfo.getSelfLink();
	}
	
	/**
	 * Upload pdf file.
	 *
	 * @param stream the stream
	 * @param bucketName the bucket name
	 * @param folderName the folder name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String uploadPdfFile(ByteArrayOutputStream stream, final String bucketName, String folderName) throws IOException {
		//DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.DATE_FORMAT_GCS_UTIL);
		//DateTime dt = DateTime.now(DateTimeZone.UTC);
		//String dtString = dt.toString(dtf);

		//final String fileName = folderName.toString().toLowerCase()+ "/"+dtString + name.trim() + ".pdf";
		final String fileName = folderName;
		Pattern patt = Pattern.compile(WHITESPACES_STRING); 
		Matcher mat = patt.matcher(fileName); 
		mat.replaceAll("-");

		BlobInfo blobInfo =
				storage.create(
						BlobInfo
						.newBuilder(bucketName, fileName)
						.build(),
						stream.toByteArray());
		return blobInfo.getSelfLink();
	}



	/**
	 * Upload pdf file.
	 *
	 * @param bucketName the bucket name
	 * @param folderName the folder name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String uploadFilewithType(byte[] byteArray, final String bucketName, String folderName, String contentType) throws IOException {
		final String fileName = folderName;
		Pattern patt = Pattern.compile(WHITESPACES_STRING); 
		Matcher mat = patt.matcher(fileName); 
		mat.replaceAll("-");

		BlobInfo blobInfo =
				storage.create(
						BlobInfo
						.newBuilder(bucketName, fileName)
						.setContentType(contentType)
						.build(),
						byteArray);
		return blobInfo.getSelfLink();
	}

	/**
	 * Upload pdf file.
	 *
	 * @param name the name
	 * @param bucketName the bucket name
	 * @param folderName the folder name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String uploadCSVFilewithType(byte[] byteArray, String name, final String bucketName, FolderNames folderName, String contentType) throws IOException {
		final String fileName = name.trim()+".csv";
		Pattern patt = Pattern.compile(WHITESPACES_STRING);
		Matcher mat = patt.matcher(fileName);
		mat.replaceAll("-");

		BlobInfo blobInfo =
				storage.create(
						BlobInfo
								.newBuilder(bucketName, fileName)
								.setContentType(contentType)
								.build(),
						byteArray);
		return blobInfo.getSelfLink();
	}
	
	
	/**
	 * Gets the object self link.
	 *
	 * @param objectName the object name
	 * @return the object self link
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private String getObjectSelfLink(String objectName) throws UnsupportedEncodingException {
		if(objectName.contains("/o/")) {
			objectName=objectName.split("/o/")[1];
			if(objectName.indexOf('?')>-1){
				objectName=objectName.split("\\?")[0];
			}

		}
		objectName=URLDecoder.decode( objectName, "UTF-8" );
		return objectName;

	}


}
