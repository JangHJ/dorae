package com.multi.dorae;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class ApiParsing {

	// 싱글톤으로 만든 dao 찾기
	@Autowired
	PlayDAO dao;

	@Autowired
	StageDAO dao2;

	// xml에서 태그 값을 읽어오는 함수
	private static String getTagValue(String tag, Element eElement) {
		Node nValue = null;

		NodeList x = eElement.getElementsByTagName(tag);
		Node test = x.item(0);
		NodeList t = null;
		if (test != null) {
			t = test.getChildNodes();
			if ((Node) t.item(0) != null) {
				nValue = (Node) t.item(0);
			}
		}
		if (nValue == null)
			return null;
		return nValue.getNodeValue();
	}

	// API 파싱(play 테이블 추가)
	// "공연" db를 불러와서 공연id만 뽑은 후
	// 그 id 리스트를 "공연상세"의 요청변수에 삽입해 공연상세목록을 불러오기
	public void apiPlay()
			throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException, SQLException {
		// Stringbuilder를 이용하여 url 조건 설정
//		StringBuilder urlBuilder = null;
//		urlBuilder = new StringBuilder("http://kopis.or.kr/openApi/restful/pblprfr");
//		urlBuilder.append("?" + URLEncoder.encode("service", "UTF-8") + "=e2622608ee6c4fa2a38d5f75a26a7e50"); /* Service Key */
//		urlBuilder.append("&" + URLEncoder.encode("stdate", "UTF-8") + "=20180601"); /* 시작일 */
//		urlBuilder.append("&" + URLEncoder.encode("eddate", "UTF-8") + "=20230230"); /* 종료일 */
//		urlBuilder.append("&" + URLEncoder.encode("cpage", "UTF-8") + "=1"); /* 출력할 페이지 수 */
//		urlBuilder.append("&" + URLEncoder.encode("rows", "UTF-8") + "=10000"); /* 출력할 데이터 수 */
//		urlBuilder.append("&" + URLEncoder.encode("signgucode", "UTF-8") + "=11"); /* 지역 코드 */
//		urlBuilder.append("&" + URLEncoder.encode("shcate","UTF-8") + "=AAAA"); /*장르 코드(연극)*/
//		urlBuilder.append("&" + URLEncoder.encode("shcate", "UTF-8") + "=AAAB"); /* 장르 코드(뮤지컬) */
//		URL url = new URL(urlBuilder.toString());
//		String parsingUrl = "";
//		parsingUrl = url.toString();

		// 20200601~20230427까지 공연중
		// 서울 특별시 종로구
		// 공연 데이터 중 100개만 불러옴
		String parsingUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr?service=e2622608ee6c4fa2a38d5f75a26a7e50&stdate=20200601&eddate=20230427&cpage=1&rows=100&prfstate=02&signgucode=11&signgucodesub=1111&kidstate=";
		
		System.out.println(parsingUrl);

		// playVO 사용
		// PlayVO bag = new PlayVO();

		// 1. 빌더 팩토리 생성.
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		// 2. 빌더 팩토리로부터 빌더 생성
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		// 3. 빌더를 통해 XML 문서를 파싱해서 Document 객체로 가져온다.
		Document doc = dBuilder.parse(parsingUrl);

		// 문서 구조 안정화
		doc.getDocumentElement().normalize();

		// XML 데이터 중 <db> 태그의 내용을 가져온다.
		NodeList nList = doc.getElementsByTagName("db");
		System.out.println("length: " + nList.getLength());

		// 공연 id 목록을 저장할 리스트 생성
		List<String> idList = new ArrayList<String>();

		// api의 공연id 목록을 idList에 넣어줌
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				idList.add(getTagValue("mt20id", eElement));
			}
		}

		// idList 잘 들어갔는지 확인
		if (idList != null) {
			try {
				for (String id : idList) {
					System.out.println(id);
				}

			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		for (String id : idList) {
			String parsingUrl2 = "http://www.kopis.or.kr/openApi/restful/pblprfr/" + id
					+ "?service=e2622608ee6c4fa2a38d5f75a26a7e50";
			System.out.println(parsingUrl2);

			// playVO 사용
			PlayVO bag = new PlayVO();

			// 1. 빌더 팩토리 생성.
			DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();

			// 2. 빌더 팩토리로부터 빌더 생성
			DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();

			// 3. 빌더를 통해 XML 문서를 파싱해서 Document 객체로 가져온다.
			Document doc2 = dBuilder2.parse(parsingUrl2);

			// 문서 구조 안정화
			doc2.getDocumentElement().normalize();

			// XML 데이터 중 <db> 태그의 내용을 가져온다.
			NodeList nList2 = doc2.getElementsByTagName("db");
			System.out.println("length: " + nList2.getLength());
			int result = 0;
			for (int i = 0; i < nList2.getLength(); i++) {

				Node nNode = nList2.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String playId = getTagValue("mt20id", eElement);
					bag.setPlay_id(playId);
					bag.setStage_id(getTagValue("mt10id", eElement));
					bag.setPlay_name(getTagValue("prfnm", eElement));
					bag.setPlay_start(transformDate(getTagValue("prfpdfrom", eElement)));
					bag.setPlay_end(transformDate(getTagValue("prfpdto", eElement)));
					bag.setStage_name(getTagValue("fcltynm", eElement));
					bag.setCasting(getTagValue("prfcast", eElement));
					bag.setCrew(getTagValue("prfcrew", eElement));
					bag.setRuntime(getTagValue("prfruntime", eElement));
					bag.setPlay_age(getTagValue("prfage", eElement));
					bag.setEnterprise(getTagValue("entrpsnm", eElement));
					bag.setPrice(getTagValue("pcseguidance", eElement));
					bag.setPoster(getTagValue("poster", eElement));
					bag.setContent(getTagValue("sty", eElement));
					bag.setGenre_name(getTagValue("genrenm", eElement));
					bag.setState(getTagValue("prfstate", eElement));
					bag.setOpenrun(getTagValue("openrun", eElement));
					bag.setImage(getTagValue("styurl", eElement));
					bag.setPlay_time(getTagValue("dtguidance", eElement));

					// System.out.println(getTagValue("prfnm", eElement));
					// System.out.println(getTagValue("dtguidance", eElement));
					// System.out.println(getTagValue("styurl", eElement));

					if (playId != null) {
						try {
							System.out.println(getTagValue("prfnm", eElement));
							result = dao.insert(bag);

						} catch (Exception e) {
							System.out.println(e.toString());
						}
					}
				}
			}

			System.out.println("처리 개수" + nList.getLength());
			if (result > 0) {
				System.out.println("db 추가 성공");
			} else {
				System.out.println("db 추가 실패");
			}
		}
	}

	// API 파싱해서 DB에 placeDetail 테이블 추가
	public void apiStage()
			throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException, SQLException {

		// api url에 붙여줄 stage_id 리스트를 play 테이블에서 불러온다(공연에 있는 공연장만 불러옴, 전부 x)
		List<String> stageList = new ArrayList<String>();
		try {
			stageList = dao.listStageId();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		// stage 아이디 List에 잘 들어갔는지 확인
		if (stageList != null) {
			try {
				for (String id : stageList) {
					System.out.println(id);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		for (String id : stageList) {
			String parsingUrl = "http://www.kopis.or.kr/openApi/restful/prfplc/" + id
					+ "?service=55f2e69c1e6146dab5fe824a31328f70";
			// String
			// parsingUrl="http://www.kopis.or.kr/openApi/restful/prfplc/FC001247?service=e2622608ee6c4fa2a38d5f75a26a7e50";
			System.out.println(parsingUrl);
			// stageVO 사용
			StageVO bag = new StageVO();

			// 1. 빌더 팩토리 생성.
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			// 2. 빌더 팩토리로부터 빌더 생성
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			// 3. 빌더를 통해 XML 문서를 파싱해서 Document 객체로 가져온다.
			Document doc = dBuilder.parse(parsingUrl);

			// 문서 구조 안정화
			doc.getDocumentElement().normalize();

			// XML 데이터 중 <db> 태그의 내용을 가져온다.
			NodeList nList = doc.getElementsByTagName("db");
			System.out.println("length: " + nList.getLength());
			int result = 0;
			for (int i = 0; i < nList.getLength(); i++) {

				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String placeId = getTagValue("mt10id", eElement);
					bag.setStage_id(placeId);
					bag.setStage_name(getTagValue("fcltynm", eElement));
					bag.setStage_cnt(getTagValue("mt13cnt", eElement));
					bag.setKind(getTagValue("fcltychartr", eElement));
					bag.setOpen_year(getTagValue("opende", eElement));
					bag.setSeat_cnt(getTagValue("seatscale", eElement));
					bag.setTel(getTagValue("telno", eElement));
					bag.setWebsite(getTagValue("relateurl", eElement));
					bag.setAddress(getTagValue("adres", eElement));
					bag.setLatitude(getTagValue("la", eElement));
					bag.setLongitude(getTagValue("lo", eElement));

					System.out.println(bag);
					if (placeId != null) {
						try {
							//System.out.println("placeid가 있어!");
							result = dao2.insert(bag);

						} catch (Exception e) {
							System.out.println(e.toString());
						}
					}
				}
			}

			System.out.println("처리 개수" + nList.getLength());
			if (result > 0) {
				System.out.println("db 추가 성공");
			} else {
				System.out.println("db 추가 실패");
			}
		}
	}

	// 날짜가 yyyymmdd 형식으로 입력되었을 경우 Date로 변경
	public static Date transformDate(String date) {
		SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyy.mm.dd");

		// Date로 변경하기 위해서는 날짜 형식을 yyyy-mm-dd로 변경해야 한다.
		SimpleDateFormat afterFormat = new SimpleDateFormat("yyyy-mm-dd");

		java.util.Date tempDate = null;

		try {
			// 현재 yyyymmdd로된 날짜 형식으로 java.util.Date객체를 만든다.
			tempDate = beforeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// java.util.Date를 yyyy-mm-dd 형식으로 변경하여 String로 반환한다.
		String transDate = afterFormat.format(tempDate);

		// 반환된 String 값을 Date로 변경한다.
		Date d = Date.valueOf(transDate);

		return d;
	}

//	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, SQLException, ClassNotFoundException
//	{
//		System.out.println("시작");
//
//		apiPlay();
//		// apiPlayDetail();
//		// apiPlace();
//
//	}
}
