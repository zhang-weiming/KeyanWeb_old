package myjavabean.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myjavabean.util.DBHelper;

public class Report {	
	private ArrayList<String> data;
	private String[] pos_sents;
//	private HashMap<String, String> map;
//	private String reportContent;
	private String[] adj1; // 大规模的评价词
//	private String[] adj2; // 差强人意|表现一般
	private String[] cleanDiscussionAdj; // 保养/维护费用 不低|高
	private String[] costPerformanceAdj; // 性价比 不高|低
	private String[] headStmts;
	private String[] tailStmts;
	
	
	private DBHelper dbHelper;
	
	public Report(ArrayList<String> data, String[] pos_sents) {
		this.data = data;
		this.pos_sents = pos_sents;
		adj1 = new String[] {
				"等方面存在问题", 
				"等方面略显不足", 
				"有上升空间", 
				"等方面有待完善", 
				"有待提升", 
				"有待改善", 
				"尚需改进",
				"差强人意",
				"表现一般"
		};
//		adj2 = new String[] {
//				"差强人意",
//				"表现一般"
//		};
		cleanDiscussionAdj = new String[] {
				"不低的保养费用",
				"较高的保养费用",
				"不低的维护费用",
				"较高的维护费用"
		};
		costPerformanceAdj = new String[] {
				"性价比不高增加了",
				"性价比较低增加了"
		};
		try {
			headStmts = new String[] {
					"通过分析发现，",
					"从一部分质量问题反馈来看，用户认为",
					"从本软件目前收到的信息情况来看，部分车主认为",
					"车无完车，不少车主反馈"
			};
			tailStmts = new String[] {
					"如果可以对这些方面加以重视，同时对车主反馈的小毛病进行改进，相信用户会更加满意。",
					"如果厂家可以重视，并且在日后进行改进，相信其会有更好的表现。",
					"如何解决这些问题，应成为日后厂家的努力方向。",
					"希望厂家能够尽快找到对策。",
					"希望厂家重视消费者的诉求，提高产品竞争力。"
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
//		map = new HashMap<String, String>();
		dbHelper = new DBHelper();
	}
	
	public String getReport() {
		Map<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < data.size(); i++) {
			String[] parts = data.get(i).split(" ");
			String head = parts[0];
			String tail = parts[1];
			if (tempMap.containsKey(head)) {
				ArrayList<String> tempList = tempMap.get(head);
				tempList.add(tail);
				tempMap.put(head, tempList);
//				System.out.println("[true]" + head + " " + tempList);
			}
			else {
				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(tail);
				tempMap.put(head, tempList);
//				System.out.println("[false]" + head + " " + tempList);
			}
		}
		String returnData = "";
		for (String key : tempMap.keySet()) {
			if (carTypeList.data.contains(key.trim())) {
				ArrayList<String> tempList = tempMap.get(key);
				System.out.println(key + " " + tempList);
				String carItemStr = key + "&&"; // carType
				String pos_sent = getSentWithShorting(tempList); // shorting
				if (pos_sent == null) {
					carItemStr += tempList.get(0); // shorting
					for (int i = 1; i < tempList.size(); i++) {
						carItemStr += "&" + tempList.get(i);
					} // shorting
				}
				else {
					carItemStr += pos_sent;
				} // shorting
				CarItem carItem = new CarItem(); // summary
				carItem.carType = key;
				carItem.shortings = tempList;
				carItemStr += "&&" + getSummary(carItem); // summary
				returnData += carItemStr + "|";
//				tempMap.remove(key);
			}
		}
		if (returnData.equals("")) {
			return "null";
		}
		else {
			return returnData.substring(0, returnData.length() - 1);
		}
	}
	
	public String getSentWithShorting(ArrayList<String> tempList) {
		for (String sent : this.pos_sents) {
			boolean getIt = true;
			for (int i = 0; i < tempList.size(); i++) {
				if (!sent.contains(tempList.get(i))) {
					getIt = false;
					break;
				}
			}
			if (getIt) {
				return sent;
			}
		}
		return null;
	}

	// 报告总结
	public String getSummary(CarItem carItem) {
//		System.out.println("11111");
//		Map<String, String> map = new HashMap<String, String>();
		Set<String> cSet = new HashSet<String>();
		String selectSql = null;
		for (int i = 0; i < carItem.shortings.size(); i++) { // 遍历所有元组
//			System.out.println("22222");
			String shorting = carItem.shortings.get(i);
			selectSql = "select * from report_info "
					+ "where object='" + shorting.trim() + "';";
			dbHelper.init();
			ResultSet rs = dbHelper.selectSql(selectSql);
//			System.out.println("33333");
			try {
				if (rs.next()) {
//					System.out.println("44444");
					String c = rs.getString(3).trim(); // classification
					while (true) {
						selectSql = "select * from report_info "
								+ "where object='" + c + "';";
						rs = dbHelper.selectSql(selectSql);
//						System.out.println("55555");
						if (rs.next()) {
							if (rs.getString(3).trim().equals(c)) { // 跳出循环，不再继续查询
								break;
							}
							else {
								c = rs.getString(3).trim();
							}
						}
						else { // 没有查询结果，不再继续查询
							break;
						}
					}
					cSet.add(c);
//					if (map.containsKey(c)) {
//						map.put(c, map.get(c) + "|" + parts[0]);
//						this.map.put(c, map.get(c) + "|" + parts[0]);
//					}
//					else {
//						map.put(c, parts[0]);
//						this.map.put(c, parts[0]);
//					}
//					break; // 结束对该词的查询
				} // 没有查询结果则放弃该元组，继续处理下一个元组（下一次for循环）
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dbHelper.close();
		if (cSet.contains("保养/维护")) {
			carItem.hasClean = true;
			carItem.cleanObject = carItem.carType + "的成本";
			cSet.remove("保养/维护");
			carItem.cleanDiscussion = cleanDiscussionAdj[ (int) (Math.random() * 4) ];
		}
		if (cSet.contains("销量")) {
			carItem.hasClean = true;
			cSet.remove("销量");
		}
		if (cSet.contains("价格")) {
			carItem.hasPrice = true;
			cSet.remove("价格");
		}
		if (cSet.contains("性价比")) {
			carItem.hasCostP= true;
			cSet.remove("性价比");
			carItem.costPerformance = costPerformanceAdj[ (int) (Math.random() * 2) ];
		}
		carItem.cList.addAll(cSet);
		System.out.println("[Report] " + cSet);
//		switch ( (int) (Math.random() * 4) ) { // 四个模板：主要是开头不同，结尾采用随机挑选。
//			case 0:
//				return buildReport1(carItem);
//			case 1:
//				return buildReport2(carItem);
//			case 2:
//				return buildReport3(carItem);
//			case 3:
//				return buildReport4(carItem);
//			default:
//		}
		return buildReport(carItem);
//		return null;
	}

	private String buildReport(CarItem carItem) {
		List<String> list = carItem.cList;
		if (list.isEmpty()) {
			list = carItem.shortings;
		}
//		String reportContent = "通过分析发现，" + carItem.carType; // 第一段开始
		String reportContent = this.headStmts[ (int) (Math.random() * 4) ] + carItem.carType; // 第一段开始
		reportContent += "的" + list.get(0);
		int splitIndex = list.size() * 2 / 3;
		for (int i = 1; i < splitIndex; i++) {
			reportContent += "、" + list.get(i);
		}
		int firstIndex = (int) (Math.random() * adj1.length); // 第一段的评价词
		reportContent += adj1[ firstIndex ]; // 第一段的评价词
		if (list.size() > 1) {
			reportContent += "，" + list.get(splitIndex); // 第二段开始
			for (int i = splitIndex + 1; i < list.size(); i++) {
				reportContent += "、" + list.get(i);
			}
			int secondIndex = (int) (Math.random() * adj1.length); // 第二段的评价词
			while (secondIndex == firstIndex) {
				secondIndex = (int) (Math.random() * adj1.length);
			}
			reportContent += "也" + adj1[ secondIndex ]; // 第二段的评价词
		}
		else {
//			reportContent += "。";
		}
		if (carItem.hasClean || carItem.hasPrice || carItem.hasCostP || carItem.hasSale) {
			reportContent += "。此外";
			if (carItem.hasClean) {
				reportContent += "，" + carItem.cleanDiscussion + carItem.cleanObject;
			}
			if (carItem.hasPrice) {
				carItem.reportContent += "，" + carItem.priceDiscussion;
			}
			if (carItem.hasCostP) {
				carItem.reportContent += "，" + carItem.costPerformance;
			}
			if (carItem.hasSale) {
				carItem.reportContent += "，" + carItem.saleDiscussion;
			}
		}
		try {
			reportContent += "。" + getTailStmt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportContent;
	}

	/*
	// 报告总结
	public String getSummary(ArrayList<String> data) {
//		System.out.println("11111");
		String reportContent = null;
		Map<String, String> map = new HashMap<String, String>();
		String selectSql = null;
		for (int i = 0; i < data.size(); i++) { // 遍历所有元组
//			System.out.println("22222");
			String[] parts = data.get(i).split(" ");
			selectSql = "select * from report_info "
					+ "where object='" + parts[1].trim() + "';";
			dbHelper.init();
			ResultSet rs = dbHelper.selectSql(selectSql);
//			System.out.println("33333");
			try {
				if (rs.next()) {
//					System.out.println("44444");
					String c = rs.getString(3).trim(); // classification
					while (true) {
						selectSql = "select * from report_info "
								+ "where object='" + c + "';";
						rs = dbHelper.selectSql(selectSql);
//						System.out.println("55555");
						if (rs.next()) {
							if (rs.getString(3).trim().equals(c)) { // 跳出循环，不再继续查询
								break;
							}
							else {
								c = rs.getString(3).trim();
							}
						}
						else { // 没有查询结果，不再继续查询
							break;
						}
					}
					if (map.containsKey(c)) {
						map.put(c, map.get(c) + "|" + parts[0]);
						this.map.put(c, map.get(c) + "|" + parts[0]);
					}
					else {
						map.put(c, parts[0]);
						this.map.put(c, parts[0]);
					}
					break; // 结束对该词的查询
				} // 没有查询结果则放弃该元组，继续处理下一个元组（下一次for循环）
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (map.containsKey("保养/维护")) {
			hasClean = true;
			cleanObject = map.get("保养/维护") + "的成本";
			map.remove("保养/维护");
			cleanDiscussion = cleanDiscussionAdj[ (int) (Math.random() * 4) ];
		}
		if (map.containsKey("销量")) {
			hasClean = true;
			map.remove("销量");
		}
		if (map.containsKey("价格")) {
			hasPrice = true;
			map.remove("价格");
		}
		if (map.containsKey("性价比")) {
			hasCostP= true;
			map.remove("性价比");
			costPerformance = costPerformanceAdj[ (int) (Math.random() * 2) ];
		}
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(map.keySet());
//		System.out.println("[Report] " + map.keySet());
		switch ( (int) (Math.random() * 4) ) { // 四个模板：主要是开头不同，结尾采用随机挑选。
			case 0:
				reportContent = buildReport1(list);
				break;
			case 1:
				reportContent = buildReport2(list);
				break;
			case 2:
				reportContent = buildReport3(list);
				break;
			case 3:
				reportContent = buildReport4(list);
				break;
			default:
				break;
		}
		dbHelper.close();
		return reportContent;
	}

	private String buildReport1(ArrayList<String> list) {
		String reportContent = "通过分析发现，"; // 第一段开始
		String[] tempHeads = map.get( list.get(0) ).split("\\|");
		for (String h : tempHeads) {
			reportContent += h + "、";
		}
		reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(0);
		int splitIndex = list.size() * 2 / 3;
		for (int i = 1; i < splitIndex; i++) {
			String tail = list.get(i);
			String[] heads = map.get(tail).split("\\|");
			for (String h : heads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
		}
		int firstIndex = (int) (Math.random() * adj1.length); // 第一段的评价词
		reportContent += adj1[ firstIndex ]; // 第一段的评价词
		if (list.size() > 1) {
			reportContent += "，";
			tempHeads = map.get( list.get(splitIndex) ).split("\\|");
			for (String h : tempHeads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(splitIndex); // 第二段开始
			for (int i = splitIndex + 1; i < list.size(); i++) {
				String tail = list.get(i);
				String[] heads = map.get(tail).split("\\|");
				for (String h : heads) {
					reportContent += h + "、";
				}
				reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
			}
			int secondIndex = (int) (Math.random() * adj1.length);
			while (secondIndex == firstIndex) {
				secondIndex = (int) (Math.random() * adj1.length);
			}
			reportContent += "也" + adj1[ secondIndex ] + "。"; // 第二段的评价词
		}
		else {
//			reportContent += "。";
		}
		if (hasClean || hasPrice || hasCostP || hasSale) {
			reportContent += "。此外";
			if (hasClean) {
				reportContent += "，" + cleanDiscussion + cleanObject;
			}
			if (hasPrice) {
				reportContent += "，" + priceDiscussion;
			}
			if (hasCostP) {
				reportContent += "，" + costPerformance;
			}
			if (hasSale) {
				reportContent += "，" + saleDiscussion;
			}
		}
//		if (reportContent.endsWith("此外")) reportContent += "。";
		try {
			reportContent += "。" + getTailStmt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportContent;
	}

	private String buildReport2(ArrayList<String> list) {
		String reportContent = "从一部分质量问题反馈来看，用户认为"; // 第一段开始
		String[] tempHeads = map.get( list.get(0) ).split("\\|");
		for (String h : tempHeads) {
			reportContent += h + "、";
		}
		reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(0);
		int splitIndex = list.size() * 2 / 3;
		for (int i = 1; i < splitIndex; i++) {
			String tail = list.get(i);
			String[] heads = map.get(tail).split("\\|");
			for (String h : heads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
		}
		int firstIndex = (int) (Math.random() * adj1.length); // 第一段的评价词
		reportContent += adj1[ firstIndex ]; // 第一段的评价词
		if (list.size() > 1) {
			reportContent += "，";
			tempHeads = map.get( list.get(splitIndex) ).split("\\|");
			for (String h : tempHeads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(splitIndex); // 第二段开始
			for (int i = splitIndex + 1; i < list.size(); i++) {
				String tail = list.get(i);
				String[] heads = map.get(tail).split("\\|");
				for (String h : heads) {
					reportContent += h + "、";
				}
				reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
			}
			int secondIndex = (int) (Math.random() * adj1.length);
			while (secondIndex == firstIndex) {
				secondIndex = (int) (Math.random() * adj1.length);
			}
			reportContent += "也" + adj1[ secondIndex ] + "。"; // 第二段的评价词
		}
		if (hasClean || hasPrice || hasCostP || hasSale) {
			reportContent += "。此外";
			if (hasClean) {
				reportContent += "，" + cleanDiscussion + cleanObject;
			}
			if (hasPrice) {
				reportContent += "，" + priceDiscussion;
			}
			if (hasCostP) {
				reportContent += "，" + costPerformance;
			}
			if (hasSale) {
				reportContent += "，" + saleDiscussion;
			}
		}
		try {
			reportContent += "。" + getTailStmt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportContent;
	}

	private String buildReport3(ArrayList<String> list) {
		String reportContent = "从本软件目前收到的信息情况来看，部分车主认为"; // 第一段开始
		String[] tempHeads = map.get( list.get(0) ).split("\\|");
		for (String h : tempHeads) {
			reportContent += h + "、";
		}
		reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(0);
		int splitIndex = list.size() * 2 / 3;
		for (int i = 1; i < splitIndex; i++) {
			String tail = list.get(i);
			String[] heads = map.get(tail).split("\\|");
			for (String h : heads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
		}
		int firstIndex = (int) (Math.random() * adj1.length); // 第一段的评价词
		reportContent += adj1[ firstIndex ]; // 第一段的评价词
		if (list.size() > 1) {
			reportContent += "，";
			tempHeads = map.get( list.get(splitIndex) ).split("\\|");
			for (String h : tempHeads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(splitIndex); // 第二段开始
			for (int i = splitIndex + 1; i < list.size(); i++) {
				String tail = list.get(i);
				String[] heads = map.get(tail).split("\\|");
				for (String h : heads) {
					reportContent += h + "、";
				}
				reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
			}
			int secondIndex = (int) (Math.random() * adj1.length);
			while (secondIndex == firstIndex) {
				secondIndex = (int) (Math.random() * adj1.length);
			}
			reportContent += "也" + adj1[ secondIndex ] + "。"; // 第二段的评价词
		}
		if (hasClean || hasPrice || hasCostP || hasSale) {
			reportContent += "。此外";
			if (hasClean) {
				reportContent += "，" + cleanDiscussion + cleanObject;
			}
			if (hasPrice) {
				reportContent += "，" + priceDiscussion;
			}
			if (hasCostP) {
				reportContent += "，" + costPerformance;
			}
			if (hasSale) {
				reportContent += "，" + saleDiscussion;
			}
		}
		try {
			reportContent += "。" + getTailStmt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportContent;
	}

	private String buildReport4(ArrayList<String> list) {
		String reportContent = "车无完车，不少车主反馈"; // 第一段开始
		String[] tempHeads = map.get( list.get(0) ).split("\\|");
		for (String h : tempHeads) {
			reportContent += h + "、";
		}
		reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(0);
		int splitIndex = list.size() * 2 / 3;
		for (int i = 1; i < splitIndex; i++) {
			String tail = list.get(i);
			String[] heads = map.get(tail).split("\\|");
			for (String h : heads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
		}
		int firstIndex = (int) (Math.random() * adj1.length); // 第一段的评价词
		reportContent += adj1[ firstIndex ]; // 第一段的评价词
		if (list.size() > 1) {
			reportContent += "，";
			tempHeads = map.get( list.get(splitIndex) ).split("\\|");
			for (String h : tempHeads) {
				reportContent += h + "、";
			}
			reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + list.get(splitIndex); // 第二段开始
			for (int i = splitIndex + 1; i < list.size(); i++) {
				String tail = list.get(i);
				String[] heads = map.get(tail).split("\\|");
				for (String h : heads) {
					reportContent += h + "、";
				}
				reportContent = reportContent.substring(0, reportContent.length() - 1) + "的" + tail;
			}
			int secondIndex = (int) (Math.random() * adj1.length);
			while (secondIndex == firstIndex) {
				secondIndex = (int) (Math.random() * adj1.length);
			}
			reportContent += "也" + adj1[ secondIndex ] + "。"; // 第二段的评价词
		}
		if (hasClean || hasPrice || hasCostP || hasSale) {
			reportContent += "。此外";
			if (hasClean) {
				reportContent += "，" + cleanDiscussion + cleanObject;
			}
			if (hasPrice) {
				reportContent += "，" + priceDiscussion;
			}
			if (hasCostP) {
				reportContent += "，" + costPerformance;
			}
			if (hasSale) {
				reportContent += "，" + saleDiscussion;
			}
		}
		try {
			reportContent += "。" + getTailStmt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportContent;
	}
	*/
	
	private String getTailStmt() {
		return tailStmts[ (int) (Math.random() * tailStmts.length) ];
	}
	
	public class CarItem {
		public String carType;
		public String shorting;
		public String summary;
		
		public String reportContent;
		public ArrayList<String> shortings;
		public ArrayList<String> cList;
		
		private String cleanObject;
		private String cleanDiscussion; // 保养/维护费用
		private String saleDiscussion; // 销量不容乐观
		private String priceDiscussion; // 价格不满意
		private String costPerformance; // 性价比
		
		private boolean hasClean;
		private boolean hasSale;
		private boolean hasPrice;
		private boolean hasCostP;
		
		public CarItem() {
			reportContent = "";
			saleDiscussion = "销量不容乐观";
			priceDiscussion = "用户对价格不太满意";

			hasClean = false;
			hasSale = false;
			hasPrice = false;
			hasCostP = false;
			
			cList = new ArrayList<String>();
		}
	}
	
	static class carTypeList {
		public static ArrayList<String> data = new ArrayList<String>(Arrays.asList(
				"奇瑞", "宝马", "比亚迪", "奥迪", "byd", "别克"
		));
//		public static ArrayList<String> data = new ArrayList<String>() {{
//			add("奇瑞".trim());
//			add("宝马".trim());
//			add("比亚迪".trim());
//			add("奥迪".trim());
//			add("byd".trim());
//			add("别克".trim());
//		}};
	}
	
//	public static void main(String[] args) {
////		for (int i = 0; i < carTypeList.list.size(); i++) {
////			System.out.println(carTypeList.list.get(i));
////		}
//		System.out.println(carTypeList.data.contains("奇瑞"));
//	}
}
