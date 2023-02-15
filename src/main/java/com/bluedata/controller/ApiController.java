package com.bluedata.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	@GetMapping("/check_domain")
	public int jsoup_test(@RequestParam(value = "domain", required = false) String domain) {
		URL url = null;
		HttpURLConnection conn = null;

		InputStream in = null;
		InputStreamReader reader = null;
		BufferedReader br = null;

		char[] buf = new char[512];
		StringBuffer sb = new StringBuffer();

		int code = 0;

		try {
			url = new URL(domain);
			conn = (HttpURLConnection) url.openConnection(); // 해당 url로 connection 객체 얻어옴

			if (conn != null) {
				conn.setConnectTimeout(2000); // 2초 이내 서버와 연결 수립이 안되면 예외 발생

				conn.setRequestMethod("GET");
				conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				conn.setUseCaches(false);

				conn.connect(); // request 발생

				int responseCode = conn.getResponseCode();
				code = responseCode;

				if (responseCode == HttpURLConnection.HTTP_OK) {
					in = conn.getInputStream();
					reader = new InputStreamReader(in, "utf-8");
					br = new BufferedReader(reader);

					int cnt;
					while ((cnt = br.read(buf)) != -1) {
						sb.append(buf, 0, cnt);
					}
				} else {
					return 0;
				}

			} else {
				return 0;
			}

		} catch (Exception e) {
		}

		return code;
	}

	@SuppressWarnings("resource")
	@GetMapping("/user/download/{filename}")
	public ResponseEntity<Object> download(@PathVariable("filename") String filename, HttpServletResponse response) {
		try {
			final DefaultResourceLoader loader = new DefaultResourceLoader();

			loader.getResource("file:resources/static/download/" + filename);

			String root = loader.getResource("file:src/main/resources/static/download/sample.xlsx").getFile()
					.getAbsolutePath();

			File file = new File(root);

			response.setHeader("Content-Disposition", "attachment;filename=" + file.getName()); // 다운로드 되거나 로컬에 저장되는 용도로

			FileInputStream fileInputStream = new FileInputStream(file); // 파일 읽어오기
			OutputStream out = response.getOutputStream();

			int read = 0;
			byte[] buffer = new byte[1024];

			while ((read = fileInputStream.read(buffer)) != -1) { // 1024바이트씩 계속 읽으면서 outputStream에 저장, -1이 나오면 더이상 읽을
				out.write(buffer, 0, read);
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}