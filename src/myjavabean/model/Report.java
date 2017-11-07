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
	private String[] adj1; // 大规模的评价词用于第一段
	private String[] adj2; // 用于第二段
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
				"表现差强人意",
				"表现比较一般"
		};
		adj2 = new String[] {
				"等方面也存在问题", 
				"等方面也略显不足", 
				"也有上升空间", 
				"等方面也有待完善", 
				"也有待提升", 
				"也有待改善", 
				"也尚需改进",
				"表现也差强人意",
				"表现也比较一般"
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
	
	public String getReport() { // 入口
		Map<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>(); // 品牌和所有属性的映射
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
		} // 品牌和所有属性的映射
		String reportContent = ""; // 整理报告
		for (String key : tempMap.keySet()) { // 遍历所有品牌词
			if (carTypeList.data.contains(key.trim())) {
				ArrayList<String> tempList = tempMap.get(key); // 品牌词key对应的属性集
				System.out.println("\t" + key + " " + tempList);
				String carItemStr = key + "&&"; // carType
				String pos_sent = getSentWithShorting(key); // shorting
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
				carItem.setCarType(key);
				carItem.setShortings(tempList);
				carItemStr += "&&" + getSummary(carItem); // summary
				reportContent += carItemStr + "|";
//				tempMap.remove(key);
			}
		}
		if (reportContent.equals("")) { // 没有生成报告
			return "null";
		}
		else {
			return reportContent.substring(0, reportContent.length() - 1);
		} // 整理报告
	}

	public String getSentWithShorting(String carType) {
		String sentNeeded = "";
		boolean getIt = false;
		for (String sent : this.pos_sents) {
			if (sent.contains(carType)) {
				getIt = true;
				sentNeeded += sent + "。";
			}
		}
		if (getIt) {
			return sentNeeded;
		}
		else {
			return null;
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

	public String getSummary(CarItem carItem) { // 报告总结
		Set<String> cSet = new HashSet<String>(); // 属性集合 （使用Set以便去重）
		String selectSql = null;
		for (int i = 0; i < carItem.getShortings().size(); i++) { // 遍历所有属性
			String shorting = carItem.getShortings().get(i); // 查询所有属性词的归类
			selectSql = "select * from report_info "
					+ "where object='" + shorting.trim() + "';";
			dbHelper.init();
			ResultSet rs = dbHelper.selectSql(selectSql);
			try {
				if (rs.next()) {
					String c = rs.getString(3).trim(); // classification
					while (true) {
						selectSql = "select * from report_info "
								+ "where object='" + c + "';";
						rs = dbHelper.selectSql(selectSql);
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
				} // 没有查询结果则放弃该元组，继续处理下一个元组（下一次for循环）
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// 查询所有属性词的归类
		}
		dbHelper.close();
		if (cSet.contains("保养/维护")) { // 处理四个特殊属性
			carItem.setHasClean(true);
			cSet.remove("保养/维护");
			carItem.shortings.remove("保养/维护");
			carItem.setCleanDiscussion(cleanDiscussionAdj[ (int) (Math.random() * 4) ] + "增加了" + carItem.getCarType() + "的成本");
			System.out.println("\t" + carItem.getCleanDiscussion());
		}
		if (cSet.contains("销量")) { // 报告用语固定
			carItem.hasSale = true;
			cSet.remove("销量");
			carItem.shortings.remove("销量");
			System.out.println("\t" + carItem.saleDiscussion);
		}
		if (cSet.contains("价格")) { // 报告用语固定
			carItem.hasPrice = true;
			cSet.remove("价格");
			carItem.shortings.remove("价格");
		}
		if (cSet.contains("性价比")) {
			carItem.hasCostP= true;
			cSet.remove("性价比");
			carItem.shortings.remove("性价比");
			carItem.costPerformance = costPerformanceAdj[ (int) (Math.random() * 2) ];
		} // 处理四个特殊属性
		carItem.cList.addAll(cSet);
		System.out.println("[Report-cSet] " + cSet);
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
		String summary = buildSummaryContent(carItem);
		return summary;
	}

	private String buildSummaryContent(CarItem carItem) {
		List<String> list = carItem.cList; // 所有属性词的归类（不重复）
		if (list.isEmpty()) { // 没有归类信息，则直接使用原属性词
			System.out.println("yes");
			list = carItem.shortings;
		}
		String summary = this.headStmts[ (int) (Math.random() * 4) ] + carItem.carType; // 第一段开始
		summary += "的" + list.get(0);
		int splitIndex = list.size() * 2 / 3;
		for (int i = 1; i < splitIndex; i++) {
			summary += "、" + list.get(i);
		}
		int firstIndex = (int) (Math.random() * adj1.length); // 第一段的评价词
		summary += adj1[ firstIndex ]; // 第一段的评价词
		if (list.size() > 1) {
			summary += "，" + list.get(splitIndex); // 第二段开始
			for (int i = splitIndex + 1; i < list.size(); i++) {
				summary += "、" + list.get(i);
			}
			int secondIndex = (int) (Math.random() * adj1.length); // 第二段的评价词
			while (secondIndex == firstIndex) {
				secondIndex = (int) (Math.random() * adj1.length);
			}
			summary += adj2[ secondIndex ]; // 第二段的评价词
		}
		if (carItem.hasClean || carItem.hasPrice || carItem.hasCostP || carItem.hasSale) {
//			System.out.println("\t[catItem.cList]" + carItem.cList);
			summary += "。此外";
			if (carItem.hasClean) {
				summary += "，" + carItem.cleanDiscussion;
				System.out.println("[clean]\t" + carItem.cleanDiscussion);
			}
			if (carItem.hasPrice) {
				summary += "，" + carItem.priceDiscussion;
				System.out.println("[price]\t" + carItem.priceDiscussion);
			}
			if (carItem.hasCostP) {
				summary += "，" + carItem.costPerformance;
				System.out.println("[cost]\t" + carItem.costPerformance);
			}
			if (carItem.hasSale) {
				summary += "，" + carItem.saleDiscussion;
				System.out.println("[sale]\t" + carItem.saleDiscussion);
			}
		}
		try {
			summary += "。" + getTailStmt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return summary;
	}
	
	private String getTailStmt() {
		return tailStmts[ (int) (Math.random() * tailStmts.length) ];
	}
	
	public class CarItem {
		public String carType;
		public ArrayList<String> shortings;
		public ArrayList<String> cList;
		
		public String cleanDiscussion; // 保养/维护费用
		public String saleDiscussion; // 销量不容乐观 （固定）
		public String priceDiscussion; // 价格不满意 （固定）
		public String costPerformance; // 性价比
		
		public boolean hasClean;
		public boolean hasSale;
		public boolean hasPrice;
		public boolean hasCostP;
		
		public CarItem() {
			cleanDiscussion = "";
			saleDiscussion = "销量不容乐观";
			priceDiscussion = "用户对价格不太满意";
			costPerformance = "";

			hasClean = false;
			hasSale = false;
			hasPrice = false;
			hasCostP = false;
			
			cList = new ArrayList<String>();
		}

		public String getCarType() {
			return carType;
		}

		public void setCarType(String carType) {
			this.carType = carType;
		}

		public ArrayList<String> getShortings() {
			return shortings;
		}

		public void setShortings(ArrayList<String> shortings) {
			this.shortings = shortings;
		}

		public ArrayList<String> getcList() {
			return cList;
		}

		public void setcList(ArrayList<String> cList) {
			this.cList = cList;
		}

		public String getCleanDiscussion() {
			return cleanDiscussion;
		}

		public void setCleanDiscussion(String cleanDiscussion) {
			this.cleanDiscussion = cleanDiscussion;
		}

		public String getSaleDiscussion() {
			return saleDiscussion;
		}

		public void setSaleDiscussion(String saleDiscussion) {
			this.saleDiscussion = saleDiscussion;
		}

		public String getPriceDiscussion() {
			return priceDiscussion;
		}

		public void setPriceDiscussion(String priceDiscussion) {
			this.priceDiscussion = priceDiscussion;
		}

		public String getCostPerformance() {
			return costPerformance;
		}

		public void setCostPerformance(String costPerformance) {
			this.costPerformance = costPerformance;
		}

		public boolean isHasClean() {
			return hasClean;
		}

		public void setHasClean(boolean hasClean) {
			this.hasClean = hasClean;
		}

		public boolean isHasSale() {
			return hasSale;
		}

		public void setHasSale(boolean hasSale) {
			this.hasSale = hasSale;
		}

		public boolean isHasPrice() {
			return hasPrice;
		}

		public void setHasPrice(boolean hasPrice) {
			this.hasPrice = hasPrice;
		}

		public boolean isHasCostP() {
			return hasCostP;
		}

		public void setHasCostP(boolean hasCostP) {
			this.hasCostP = hasCostP;
		}

	}
	
	static class carTypeList {
		public static ArrayList<String> data = new ArrayList<String>(Arrays.asList(
				"奥迪", "奔驰", "宝马", "比亚迪", "byd", "本田", "丰田", "大众", "别克", "奇瑞"
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
