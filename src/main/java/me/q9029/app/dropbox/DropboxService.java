package me.q9029.app.dropbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.SearchMatch;
import com.dropbox.core.v2.files.UploadUploader;

public class DropboxService {

	private DbxClientV2 dbxClient;

	public DropboxService(String accessToken, String clientIdentifier, String locale) {

		DbxRequestConfig reqConfig = DbxRequestConfig.newBuilder(clientIdentifier).withUserLocale(locale).build();

		this.dbxClient = new DbxClientV2(reqConfig, accessToken);
	}

	public DropboxService(String accessToken) {

		String version = DropboxService.class.getPackage().getImplementationVersion();
		String clientIdentifier = "DropboxService/" + (version != null ? version : "unversioned");

		String locale = Locale.getDefault().toString();

		DbxRequestConfig reqConfig = DbxRequestConfig.newBuilder(clientIdentifier).withUserLocale(locale).build();

		this.dbxClient = new DbxClientV2(reqConfig, accessToken);
	}

	public List<String> search(String path, String query) {

		try {
			List<SearchMatch> matchList = dbxClient.files().search(path, query).getMatches();

			List<String> pathList = new ArrayList<>();
			for (SearchMatch match : matchList) {
				pathList.add(match.getMetadata().getPathDisplay());
			}

			return pathList;

		} catch (DbxException e) {
			throw new RuntimeException("", e);
		}
	}

	public void upload(File file, String path) {

		try {
			try (UploadUploader uploader = dbxClient.files().upload(path)) {
				uploader.uploadAndFinish(new FileInputStream(file));
			}

		} catch (DbxException | IOException e) {
			throw new RuntimeException("", e);
		}
	}

	public byte[] download(String path) {

		try {
			byte[] byteArray = null;

			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				dbxClient.files().downloadBuilder(path).download(os);
				byteArray = os.toByteArray();
			}

			return byteArray;

		} catch (DbxException | IOException e) {
			throw new RuntimeException("", e);
		}
	}
}
