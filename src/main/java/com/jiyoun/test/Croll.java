package com.jiyoun.test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Croll {
	
	private static Scanner input = new Scanner(System.in);
	private final String gitUrl = "https://github.com";	
	private boolean isOff = false;
	public void startCrolling(String userId) {
		
		String url = urlMake(gitUrl, userId);

		while(true) {
			folderFind(findProject(url));
			if (isOff) {
				break;
			}
		}
		input.close();
	}

	//url을 만들어주는 메서드
	private String urlMake(String url, String value) {
		url += "/" + value;
		return url;
	}
	
	//링크로 파싱하여 document로 가져오는 메서드
	private Document connectJsoup(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	//프로젝트 선택화면 불러오기
	public String findProject(String url) {
		String tempurl = urlMake(url, "?tab=repositories");
		//tempurl을 쓰는 이유는 해당 프로젝트를 불러오는 url은 단 한번만 사용되기 때문.
		Document doc = connectJsoup(tempurl);
		Elements element = doc.select("div#user-repositories-list");

		for (Element el : element.select("li").select("h3")) { //프로젝트 제목들을 출력
			System.out.println(el.text());
		}
		
		//여기서는 임의로 이동하는 경로를 Scanner로 입력을 받는다.
		url = urlMake(url, input.next());
		return url;
	}
	
	public String folderFind(String url) {
		Document doc = connectJsoup(url);
		Elements element = doc.select("table.files.js-navigation-container.js-active-navigation-container");

		int number = 1;
		List<CrollVo> result = new LinkedList<CrollVo>();

		System.out.println("============================================================");
		
		for (Element el : element.select("tr.js-navigation-item")) { 
			boolean isDirectory = false;
			String source = el.select("td.content").text();
			String type = el.select("td.icon").select("svg").attr("aria-label");

			if (type.equals("directory")) {
				isDirectory = true;
			}
			
			System.out.println(number + "\t"+ source +"\t" + isDirectory);
			result.add(new CrollVo(number++, source, isDirectory));
		}

		System.out.println("============================================================");
		
		int inputIndex = input.nextInt();
		CrollVo crollResult = result.get(inputIndex-1);
		if (crollResult.getNumber() == inputIndex) {
			
			if (crollResult.isDirectory()) {
				System.out.println(url);
				String dire = url.split("https://github.com/jeongjiyoun/")[1] + "/";
				String projectName = dire.split("/")[0];
				System.out.println(projectName + ": projectName");
				url = "https://github.com/jeongjiyoun/" + projectName;
				url += "/tree";
				url += "/master/";
				url += dire.split(projectName)[0];
				System.out.println(url);
				url += crollResult.getSource();
				System.out.println(url);
				folderFind(url);
			} else {
				String dire = url.split("https://github.com/jeongjiyoun/")[1];
				SourceLink(dire,"master",crollResult.getSource());
			}
		}
		
		return url;
	}
	
	public void SourceLink(String directory, String branchName, String FileName) {
		String url = "https://raw.githubusercontent.com/";
		url += "jeongjiyoun";
		url += "/" + directory;
//		url += "/" + branchName;
		url += "/" + FileName;
		source(url);
	}
	
	private void source(String url) {
		url = url.split("/tree/")[0] + "/" + url.split("/tree/")[1];
//		url = "https://raw.githubusercontent.com/jeongjiyoun/chieUniversity/master/java/com/university/chie/controller/AdminController.java";
		Document doc = connectJsoup(url);
		//pre 태그의 내용을 긁어온다
		Elements element = doc.select("body");

		System.out.println("============================================================");

		for (Element el : element) { // 하위 뉴스 기사들을 for문 돌면서 출력
			System.out.println(el.wholeText());
		}

		System.out.println("============================================================");

	}


}
