package rent.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import rent.WhereIsMyHomeException;
import rent.model.dto.HomeDeal;
import rent.model.dto.HomePageBean;
import rent.model.dto.NaverMap;
import rent.model.service.HomeService;
import rent.model.service.HomeServiceImpl;

public class HomeInfoView {

	/** model들 */
	private HomeService homeService;
	
	/** main 화면 */
	private JFrame frame;

	/** 주택 목록 화면 */
	// JFrame owner;

	/** 창 닫기 버튼 */
	// private JButton closeBt;

	/** 주택 이미지 표시 Panel */
	private JLabel imgL;
	private JLabel[] homeInfoL;
	
	private JLabel mapL;

	/** 조회 조건 */
	private JCheckBox[] chooseC;
	private JComboBox<String> findC;
	private JTextField wordTf;
	private JButton searchBt;
	private JButton sortingBt;

	/** 조회 내용 표시할 table */
	private DefaultTableModel homeModel;
	private JTable homeTable;
	private JScrollPane homePan;
	private String[] title = { "번호", "동", "아파트이름", "거래금액", "거래종류" };

	/** 검색 조건 */
	private String key;

	/** 검색할 단어 */
	private String word;
	private boolean[] searchType = { true, true, true, true };
	private String[] choice = { "all", "dong", "name" };

	/** 화면에 표시하고 있는 주택 */
	private HomeDeal curHome;
	
	public static boolean click_chck=false;
	
	public HomeDeal getCurHome() {
		return curHome;
	}
	public void setMapL(ImageIcon icon) {
		mapL.setIcon(icon);
	}
	private void showHomeInfo(int code) {

		curHome = homeService.search(code);
		System.out.println(curHome);

		String path = "./img";
		File folder = new File(path);
		File[] fileList = folder.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i].getName();
			String dong = curHome.getDong();
			String aptName = curHome.getAptName();
			
			if (fileName.contains(aptName) || aptName.contains(fileName.substring(0, fileName.length() - 4))) {
				curHome.setImg(fileName);
			}

		}

		// foodInfoL[0].setText(""+curfood.getCode());
		homeInfoL[0].setText(curHome.getAptName());
		homeInfoL[1].setText("" + curHome.getDealAmount());
		String rent = curHome.getRentMoney();
		if (rent == null) {
			homeInfoL[2].setText("없음");
		} else {
			homeInfoL[2].setText(curHome.getRentMoney());
		}
		homeInfoL[3].setText("" + curHome.getBuildYear());
		homeInfoL[4].setText(curHome.getArea() + "");
		homeInfoL[5].setText(
				String.format("%d년 %d월 %d일", curHome.getDealYear(), curHome.getDealMonth(), curHome.getDealDay()));
		homeInfoL[6].setText(curHome.getDong());
		homeInfoL[7].setText(curHome.getJibun());

		// System.out.println("###############" + curHome.getImg());

		ImageIcon icon = null;
		if (curHome.getImg() != null && curHome.getImg().trim().length() != 0) {
			icon = new ImageIcon("img/" + curHome.getImg());
			System.out.println("#####" + icon.toString() + "####");
		} else {
			icon = new ImageIcon("./img/다세대주택.jpg");
		}

		imgL.setIcon(icon);
		NaverMap naverMap = new NaverMap(this);
		naverMap.showMap();
	}

	public HomeInfoView() {
		/* Service들 생성 */
		homeService = new HomeServiceImpl();

		/* 메인 화면 설정 */
		frame = new JFrame("WhereIsMyHome -- 아파트 거래 정보");

		setMain();

		frame.setSize(1200, 800);
		frame.setResizable(true);
		frame.setVisible(true);
		showHomeInfo(1);
	}

	/** 메인 화면인 주택 목록을 위한 화면 셋팅하는 메서드 */
	public void setMain() {

		/* 왼쪽 화면을 위한 설정 */
		JPanel left = new JPanel(new BorderLayout());
		JPanel leftCenter = new JPanel(new GridLayout(1, 2));
		JPanel leftR = new JPanel(new GridLayout(8, 2));
		JPanel LeftBottom = new JPanel(new GridLayout(1,1));
		
		leftR.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		String[] info = { "주택명", "거래금액", "월세금액", "건축연도", "전용면적", "거래일", "법정동", "지번" };
		int size = info.length;
		JLabel infoL[] = new JLabel[size];
		homeInfoL = new JLabel[size];
		for (int i = 0; i < size; i++) {
			infoL[i] = new JLabel(info[i]);
			homeInfoL[i] = new JLabel("");
			leftR.add(infoL[i]);
			leftR.add(homeInfoL[i]);
		}
		imgL = new JLabel();
		mapL = new JLabel();
//		ImageIcon icon2 = new ImageIcon();
//		mapL.setIcon(icon2);
		
		
		
		leftCenter.add(imgL);
		leftCenter.add(leftR);
		LeftBottom.add(mapL);
		
		
		left.add(new JLabel("아파트 거래 정보", JLabel.CENTER), "North");
		left.add(leftCenter, "Center");
		left.add(LeftBottom, "South");
		
		
		
		
		/* 오른쪽 화면을 위한 설정 */
		JPanel right = new JPanel(new BorderLayout());
		JPanel rightTop = new JPanel(new GridLayout(4, 2));
		JPanel rightTop1 = new JPanel(new GridLayout(1, 4));
		String[] chooseMeg = { "아파트 매매", "아파트 전월세", "주택 매매", "주택 전월세" };
		chooseC = new JCheckBox[chooseMeg.length];
		for (int i = 0, len = chooseMeg.length; i < len; i++) {
			chooseC[i] = new JCheckBox(chooseMeg[i], true);
			rightTop1.add(chooseC[i]);
		}

		JPanel rightTop2 = new JPanel(new GridLayout(1, 3));
		String[] item = { "---all---", "동", "아파트 이름" };
		findC = new JComboBox<String>(item);
		wordTf = new JTextField();
		searchBt = new JButton("검색");
		sortingBt= new JButton("가격 정렬");

		rightTop2.add(findC);
		rightTop2.add(wordTf);
		rightTop2.add(searchBt);
		rightTop2.add(sortingBt);

		rightTop.add(new Label(""));
		rightTop.add(rightTop1);
		rightTop.add(rightTop2);
		rightTop.add(new Label(""));

		JPanel rightCenter = new JPanel(new BorderLayout());
		homeModel = new DefaultTableModel(title, 20);
		homeTable = new JTable(homeModel);
		homePan = new JScrollPane(homeTable);
		homeTable.setColumnSelectionAllowed(true);
		rightCenter.add(new JLabel("거래 내역", JLabel.CENTER), "North");
		rightCenter.add(homePan, "Center");

		right.add(rightTop, "North");
		right.add(rightCenter, "Center");

		JPanel mainP = new JPanel(new GridLayout(1, 2));

		mainP.add(left);
		mainP.add(right);

		mainP.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		frame.add(mainP, "Center");

		/* 이벤트 연결 */

		homeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = homeTable.getSelectedRow();
				System.out.println("선택된 row : " + row);
				System.out.println("선택된 row의 column 값 :" + homeModel.getValueAt(row, 0));
				int code = Integer.parseInt(((String) homeModel.getValueAt(row, 0)).trim());
				showHomeInfo(code);
			}
		});

		// complete code #01
		// 아래의 코드를 참조하여 아래 라인을 uncomment 하고 searchBt.addActionList() 를 Lambda 표현식으로
		// 바꾸세요.
		searchBt.addActionListener((buttonHandler) -> {
			
			searchHomes();
		});
		
		sortingBt.addActionListener((buttonHandler )->{
			click_chck=!click_chck;
			searchHomes(1)
			;
		
	
		
		});
			
//		// 참조코드 시작 - 위 코드를 완성 후 삭제 또는 comment 처리하세요.
//		ActionListener buttonHandler = new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				searchHomes();
//			}
//		};
//		
//		searchBt.addActionListener( buttonHandler );
//		// 참조코드 종료

		showHomes();
	}

	/** 검색 조건에 맞는 주택 정보 검색 */
	private void searchHomes() {
		for (int i = 0, size = chooseC.length; i < size; i++) {
			if (chooseC[i].isSelected()) {
				searchType[i] = true;
			} else {
				searchType[i] = false;
			}
		}
		word = wordTf.getText().trim();
		key = choice[findC.getSelectedIndex()];
		System.out.println("word:" + word + " key:" + key);
		showHomes();
	}
	private void searchHomes(int a) {
		for (int i = 0, size = chooseC.length; i < size; i++) {
			if (chooseC[i].isSelected()) {
				searchType[i] = true;
			} else {
				searchType[i] = false;
			}
		}
		word = wordTf.getText().trim();
		key = choice[findC.getSelectedIndex()];
		System.out.println("word:" + word + " key:" + key);
		showHomes(a);
	}
	
	

	/**
	 * 주택 목록을 갱신하기 위한 메서드
	 */
	public void showHomes() {
		HomePageBean bean = new HomePageBean();
		bean.setSearchType(searchType);
		if (key != null) {
			if (key.equals("dong")) {
				bean.setDong(word);
			} else if (key.equals("name")) {
				bean.setAptname(word);
			}
		}

		List<HomeDeal> deals = homeService.searchAll(bean);
		
		
		if (deals != null) {
			int i = 0;
			String[][] data = new String[deals.size()][5];
			for (HomeDeal deal : deals) {
				data[i][0] = "" + deal.getNo();
				data[i][1] = deal.getDong();
				data[i][2] = deal.getAptName();
				data[i][3] = deal.getDealAmount();
				data[i++][4] = deal.getType();
			}
			homeModel.setDataVector(data, title);
		}
		
		
		
		
		
		
		
	}
	
	@SuppressWarnings("unchecked")
	public void showHomes(int a) {
		
		HomePageBean bean = new HomePageBean();
		
		bean.setSearchType(searchType);
		if (key != null) {
			if (key.equals("dong")) {
				bean.setDong(word);
			} else if (key.equals("name")) {
				bean.setAptname(word);
			}
		}
		
		List<HomeDeal> deals = homeService.searchAll(bean);
		
		Collections.sort((List<HomeDeal>) deals);
		
		if (deals != null) {
			int i = 0;
			String[][] data = new String[deals.size()][5];
	
			for (HomeDeal deal : deals) {
				data[i][0] = "" + deal.getNo();
				data[i][1] = deal.getDong();
				data[i][2] = deal.getAptName();
				data[i][3] = deal.getDealAmount();
				
				data[i++][4] = deal.getType();
			}
			
			homeModel.setDataVector(data, title);
		}
		
		
		
		
		
		
	}
	
	
	
	
	
	
//	public static void main(String[] args) {
//		new HomeInfoView();
//	}

}
