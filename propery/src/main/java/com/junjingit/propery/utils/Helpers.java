/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.junjingit.propery.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Config;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some helper functions for the download manager
 */
public class Helpers {

	public static Random sRandom = new Random(SystemClock.uptimeMillis());

	/** Regex used to parse content-disposition headers */
	private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern
			.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"");

	private Helpers() {
	}

	/*
	 * Parse the Content-Disposition HTTP Header. The format of the header is
	 * defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html This
	 * header provides a filename for content that is going to be downloaded to
	 * the file system. We only support the attachment type.
	 */
	private static String parseContentDisposition(String contentDisposition) {
		try {
			Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
			if (m.find()) {
				return m.group(1);
			}
		} catch (IllegalStateException ex) {
			// This function is defined as returning null when it can't parse
			// the header
		}
		return null;
	}

	/**
	 * Exception thrown from methods called by generateSaveFile() for any fatal
	 * error.
	 */
	public static class GenerateSaveFileError extends Exception {
		private static final long serialVersionUID = 4293675292408637112L;

		int mStatus;
		String mMessage;

		public GenerateSaveFileError(int status, String message) {
			mStatus = status;
			mMessage = message;
		}
	}




	/**
	 * @return the root of the filesystem containing the given path
	 */
	public static File getFilesystemRoot(String path) {
		File cache = Environment.getDownloadCacheDirectory();
		if (path.startsWith(cache.getPath())) {
			return cache;
		}
		File external = Environment.getExternalStorageDirectory();
		if (path.startsWith(external.getPath())) {
			return external;
		}
		throw new IllegalArgumentException(
				"Cannot determine filesystem root for " + path);
	}
	public static boolean isExternalMediaMounted() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// No SD card found.
			Log.d(Constants.TAG, "no external storage");
			return false;
		}
		return true;
	}

	/**
	 * @return the number of bytes available on the filesystem rooted at the
	 *         given File
	 */
	public static long getAvailableBytes(File root) {
		StatFs stat = new StatFs(root.getPath());
		// put a bit of margin (in case creating the file grows the system by a
		// few blocks)
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}

	private static String chooseFilename(String url, String hint,
			String contentDisposition, String contentLocation, int destination) {
		String filename = null;

		// First, try to use the hint from the application, if there's one
		if (filename == null && hint != null && !hint.endsWith("/")) {
			if (Constants.LOGVV) {
				Log.v(Constants.TAG, "getting filename from hint");
			}
			int index = hint.lastIndexOf('/') + 1;
			if (index > 0) {
				filename = hint.substring(index);
			} else {
				filename = hint;
			}
		}

		// If we couldn't do anything with the hint, move toward the content
		// disposition
		if (filename == null && contentDisposition != null) {
			filename = parseContentDisposition(contentDisposition);
			if (filename != null) {
				if (Constants.LOGVV) {
					Log.v(Constants.TAG,
							"getting filename from content-disposition");
				}
				int index = filename.lastIndexOf('/') + 1;
				if (index > 0) {
					filename = filename.substring(index);
				}
			}
		}

		// If we still have nothing at this point, try the content location
		if (filename == null && contentLocation != null) {
			String decodedContentLocation = Uri.decode(contentLocation);
			if (decodedContentLocation != null
					&& !decodedContentLocation.endsWith("/")
					&& decodedContentLocation.indexOf('?') < 0) {
				if (Constants.LOGVV) {
					Log.v(Constants.TAG,
							"getting filename from content-location");
				}
				int index = decodedContentLocation.lastIndexOf('/') + 1;
				if (index > 0) {
					filename = decodedContentLocation.substring(index);
				} else {
					filename = decodedContentLocation;
				}
			}
		}

		// If all the other http-related approaches failed, use the plain uri
		if (filename == null) {
			String decodedUrl = Uri.decode(url);
			if (decodedUrl != null && !decodedUrl.endsWith("/")
					&& decodedUrl.indexOf('?') < 0) {
				int index = decodedUrl.lastIndexOf('/') + 1;
				if (index > 0) {
					if (Constants.LOGVV) {
						Log.v(Constants.TAG, "getting filename from uri");
					}
					filename = decodedUrl.substring(index);
				}
			}
		}

		// Finally, if couldn't get filename from URI, get a generic filename
		if (filename == null) {
			if (Constants.LOGVV) {
				Log.v(Constants.TAG, "using default filename");
			}
			filename = Constants.DEFAULT_DL_FILENAME;
		}

		filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_");

		return filename;
	}

	private static String chooseExtensionFromMimeType(String mimeType,
			boolean useDefaults) {
		String extension = null;
		if (mimeType != null) {
			extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
					mimeType);
			if (extension != null) {
				if (Constants.LOGVV) {
					Log.v(Constants.TAG, "adding extension from type");
				}
				extension = "." + extension;
			} else {
				if (Constants.LOGVV) {
					Log.v(Constants.TAG, "couldn't find extension for "
							+ mimeType);
				}
			}
		}
		if (extension == null) {
			if (mimeType != null && mimeType.toLowerCase().startsWith("text/")) {
				if (mimeType.equalsIgnoreCase("text/html")) {
					if (Constants.LOGVV) {
						Log.v(Constants.TAG, "adding default html extension");
					}
					extension = Constants.DEFAULT_DL_HTML_EXTENSION;
				} else if (useDefaults) {
					if (Constants.LOGVV) {
						Log.v(Constants.TAG, "adding default text extension");
					}
					extension = Constants.DEFAULT_DL_TEXT_EXTENSION;
				}
			} else if("application/vnd.ms-word".equals(mimeType)) {
				extension = ".doc";
			} else if (useDefaults) {
				if (Constants.LOGVV) {
					Log.v(Constants.TAG, "adding default binary extension");
				}
				extension = Constants.DEFAULT_DL_BINARY_EXTENSION;
			}
		}
		return extension;
	}

	private static String chooseExtensionFromFilename(String mimeType,
			int destination, String filename, int dotIndex) {
		String extension = null;
		if (mimeType != null) {
			// Compare the last segment of the extension against the mime type.
			// If there's a mismatch, discard the entire extension.
			int lastDotIndex = filename.lastIndexOf('.');
			String typeFromExt = MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension(
							filename.substring(lastDotIndex + 1));
			if (typeFromExt == null || !typeFromExt.equalsIgnoreCase(mimeType)) {
				extension = chooseExtensionFromMimeType(mimeType, false);
				if (extension != null) {
					if (Constants.LOGVV) {
						Log.v(Constants.TAG, "substituting extension from type");
					}
				} else {
					if (Constants.LOGVV) {
						Log.v(Constants.TAG, "couldn't find extension for "
								+ mimeType);
					}
				}
			}
		}
		if (extension == null) {
			if (Constants.LOGVV) {
				Log.v(Constants.TAG, "keeping extension");
			}
			extension = filename.substring(dotIndex);
		}
		return extension;
	}



	/**
	 * Checks whether the filename looks legitimate
	 */
	public static boolean isFilenameValid(String filename) {
		filename = filename.replaceFirst("/+", "/"); // normalize leading
		// slashes
		return filename.startsWith(Environment.getDownloadCacheDirectory()
				.toString())
				|| filename.startsWith(Environment
						.getExternalStorageDirectory().toString());
	}

	/**
	 * Checks whether this looks like a legitimate selection parameter
	 */
	public static void validateSelection(String selection,
			Set<String> allowedColumns) {
		try {
			if (selection == null || selection.length() == 0) {
				return;
			}
			Lexer lexer = new Lexer(selection, allowedColumns);
			parseExpression(lexer);
			if (lexer.currentToken() != Lexer.TOKEN_END) {
				throw new IllegalArgumentException("syntax error");
			}
		} catch (RuntimeException ex) {
			if (Constants.LOGV) {
				Log.d(Constants.TAG, "invalid selection [" + selection
						+ "] triggered " + ex);
			} else if (Config.LOGD) {
				Log.d(Constants.TAG, "invalid selection triggered " + ex);
			}
			throw ex;
		}

	}

	// expression <- ( expression ) | statement [AND_OR ( expression ) |
	// statement] *
	// | statement [AND_OR expression]*
	private static void parseExpression(Lexer lexer) {
		for (;;) {
			// ( expression )
			if (lexer.currentToken() == Lexer.TOKEN_OPEN_PAREN) {
				lexer.advance();
				parseExpression(lexer);
				if (lexer.currentToken() != Lexer.TOKEN_CLOSE_PAREN) {
					throw new IllegalArgumentException(
							"syntax error, unmatched parenthese");
				}
				lexer.advance();
			} else {
				// statement
				parseStatement(lexer);
			}
			if (lexer.currentToken() != Lexer.TOKEN_AND_OR) {
				break;
			}
			lexer.advance();
		}
	}

	// statement <- COLUMN COMPARE VALUE
	// | COLUMN IS NULL
	private static void parseStatement(Lexer lexer) {
		// both possibilities start with COLUMN
		if (lexer.currentToken() != Lexer.TOKEN_COLUMN) {
			throw new IllegalArgumentException(
					"syntax error, expected column name");
		}
		lexer.advance();

		// statement <- COLUMN COMPARE VALUE
		if (lexer.currentToken() == Lexer.TOKEN_COMPARE) {
			lexer.advance();
			if (lexer.currentToken() != Lexer.TOKEN_VALUE) {
				throw new IllegalArgumentException(
						"syntax error, expected quoted string");
			}
			lexer.advance();
			return;
		}

		// statement <- COLUMN IS NULL
		if (lexer.currentToken() == Lexer.TOKEN_IS) {
			lexer.advance();
			if (lexer.currentToken() != Lexer.TOKEN_NULL) {
				throw new IllegalArgumentException(
						"syntax error, expected NULL");
			}
			lexer.advance();
			return;
		}

		// didn't get anything good after COLUMN
		throw new IllegalArgumentException("syntax error after column name");
	}

	/**
	 * A simple lexer that recognizes the words of our restricted subset of SQL
	 * where clauses
	 */
	private static class Lexer {
		public static final int TOKEN_START = 0;
		public static final int TOKEN_OPEN_PAREN = 1;
		public static final int TOKEN_CLOSE_PAREN = 2;
		public static final int TOKEN_AND_OR = 3;
		public static final int TOKEN_COLUMN = 4;
		public static final int TOKEN_COMPARE = 5;
		public static final int TOKEN_VALUE = 6;
		public static final int TOKEN_IS = 7;
		public static final int TOKEN_NULL = 8;
		public static final int TOKEN_END = 9;

		private final String mSelection;
		private final Set<String> mAllowedColumns;
		private int mOffset = 0;
		private int mCurrentToken = TOKEN_START;
		private final char[] mChars;

		public Lexer(String selection, Set<String> allowedColumns) {
			mSelection = selection;
			mAllowedColumns = allowedColumns;
			mChars = new char[mSelection.length()];
			mSelection.getChars(0, mChars.length, mChars, 0);
			advance();
		}

		public int currentToken() {
			return mCurrentToken;
		}

		public void advance() {
			char[] chars = mChars;

			// consume whitespace
			while (mOffset < chars.length && chars[mOffset] == ' ') {
				++mOffset;
			}

			// end of input
			if (mOffset == chars.length) {
				mCurrentToken = TOKEN_END;
				return;
			}

			// "("
			if (chars[mOffset] == '(') {
				++mOffset;
				mCurrentToken = TOKEN_OPEN_PAREN;
				return;
			}

			// ")"
			if (chars[mOffset] == ')') {
				++mOffset;
				mCurrentToken = TOKEN_CLOSE_PAREN;
				return;
			}

			// "?"
			if (chars[mOffset] == '?') {
				++mOffset;
				mCurrentToken = TOKEN_VALUE;
				return;
			}

			// "=" and "=="
			if (chars[mOffset] == '=') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length && chars[mOffset] == '=') {
					++mOffset;
				}
				return;
			}

			// ">" and ">="
			if (chars[mOffset] == '>') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length && chars[mOffset] == '=') {
					++mOffset;
				}
				return;
			}

			// "<", "<=" and "<>"
			if (chars[mOffset] == '<') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length
						&& (chars[mOffset] == '=' || chars[mOffset] == '>')) {
					++mOffset;
				}
				return;
			}

			// "!="
			if (chars[mOffset] == '!') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length && chars[mOffset] == '=') {
					++mOffset;
					return;
				}
				throw new IllegalArgumentException(
						"Unexpected character after !");
			}

			// columns and keywords
			// first look for anything that looks like an identifier or a
			// keyword
			// and then recognize the individual words.
			// no attempt is made at discarding sequences of underscores with no
			// alphanumeric
			// characters, even though it's not clear that they'd be legal
			// column names.
			if (isIdentifierStart(chars[mOffset])) {
				int startOffset = mOffset;
				++mOffset;
				while (mOffset < chars.length
						&& isIdentifierChar(chars[mOffset])) {
					++mOffset;
				}
				String word = mSelection.substring(startOffset, mOffset);
				if (mOffset - startOffset <= 4) {
					if (word.equals("IS")) {
						mCurrentToken = TOKEN_IS;
						return;
					}
					if (word.equals("OR") || word.equals("AND")) {
						mCurrentToken = TOKEN_AND_OR;
						return;
					}
					if (word.equals("NULL")) {
						mCurrentToken = TOKEN_NULL;
						return;
					}
				}
				if (mAllowedColumns.contains(word)) {
					mCurrentToken = TOKEN_COLUMN;
					return;
				}
				throw new IllegalArgumentException(
						"unrecognized column or keyword");
			}

			// quoted strings
			if (chars[mOffset] == '\'') {
				++mOffset;
				while (mOffset < chars.length) {
					if (chars[mOffset] == '\'') {
						if (mOffset + 1 < chars.length
								&& chars[mOffset + 1] == '\'') {
							++mOffset;
						} else {
							break;
						}
					}
					++mOffset;
				}
				if (mOffset == chars.length) {
					throw new IllegalArgumentException("unterminated string");
				}
				++mOffset;
				mCurrentToken = TOKEN_VALUE;
				return;
			}

			// anything we don't recognize
			throw new IllegalArgumentException("illegal character: "
					+ chars[mOffset]);
		}

		private static final boolean isIdentifierStart(char c) {
			return c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
		}

		private static final boolean isIdentifierChar(char c) {
			return c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
					|| (c >= '0' && c <= '9');
		}
	}


	
	public static Uri getContentUri(String authority) {
		return Uri.parse("content://" + authority + "/my_downloads");
	}

	public static Uri getAllDonwloadContentUri(String authority) {
		return Uri.parse("content://" + authority + "/all_downloads");
	}

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

	/**
	 * Returns application cache directory. Cache directory will be created on SD card
	 * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
	 * Android defines cache directory on device's file system.
	 *
	 * @param context Application context
	 * @return Cache {@link File directory}.<br />
	 * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
	 * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
	 */
	public static File getCacheDirectory(Context context) {
		return getCacheDirectory(context, true);
	}

	/**
	 * Returns application cache directory. Cache directory will be created on SD card
	 * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
	 * on device's file system depending incoming parameters.
	 *
	 * @param context        Application context
	 * @param preferExternal Whether prefer external location for cache
	 * @return Cache {@link File directory}.<br />
	 * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
	 * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
	 */
	public static File getCacheDirectory(Context context, boolean preferExternal) {
		File appCacheDir = null;
		String externalStorageState;
		try {
			externalStorageState = Environment.getExternalStorageState();
		} catch (NullPointerException e) { // (sh)it happens (Issue #660)
			externalStorageState = "";
		}
		if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		if (appCacheDir == null) {
			String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
			Log.d(Constants.TAG, "Can't define system cache directory! '%s' will be used." + cacheDirPath);
			appCacheDir = new File(cacheDirPath);
		}
		return appCacheDir;
	}

	/**
	 * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
	 * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on device's file system.
	 *
	 * @param context Application context
	 * @return Cache {@link File directory}
	 */
	public static File getIndividualCacheDirectory(Context context) {
		return getIndividualCacheDirectory(context, Constants.DEFAULT_DL_SUBDIR);
	}

	/**
	 * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
	 * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on device's file system.
	 *
	 * @param context Application context
	 * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
	 * @return Cache {@link File directory}
	 */
	public static File getIndividualCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = getCacheDirectory(context);
		File individualCacheDir = new File(appCacheDir, cacheDir);
		if (!individualCacheDir.exists()) {
			if (!individualCacheDir.mkdir()) {
				individualCacheDir = appCacheDir;
			}
		}
		return individualCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				Log.d(Constants.TAG, "Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.d(Constants.TAG, "Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}
}
