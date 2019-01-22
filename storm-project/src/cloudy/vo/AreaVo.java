package cloudy.vo;

import java.text.DecimalFormat;

public class AreaVo {

	private String beijing;
	private String shanghai;
	private String guangzhou;
	private String chengdu;
	private String shenzhen;

	public String getBeijing() {
		return beijing;
	}

	public void setBeijing(String beijing) {
		this.beijing = beijing;
	}

	public String getShanghai() {
		return shanghai;
	}

	public void setShanghai(String shanghai) {
		this.shanghai = shanghai;
	}

	public String getGuangzhou() {
		return guangzhou;
	}

	public void setGuangzhou(String guangzhou) {
		this.guangzhou = guangzhou;
	}

	public String getChengdu() {
		return chengdu;
	}

	public void setChengdu(String chengdu) {
		this.chengdu = chengdu;
	}

	public String getShenzhen() {
		return shenzhen;
	}

	public void setShenzhen(String shenzhen) {
		this.shenzhen = shenzhen;
	}

	public void setData(String areaId, String amt) {
		if (areaId.equals("1")) {
			this.setBeijing(amt);
		} else if (areaId.equals("2")) {
			this.setShanghai(amt);
		} else if (areaId.equals("3")) {
			this.setGuangzhou(amt);
		} else if (areaId.equals("4")) {
			this.setShenzhen(amt);
		} else if (areaId.equals("5")) {
			this.setChengdu(amt);
		}
	}

	@Override
	public String toString() {
		return "[" + getFmtPoint(beijing) + ","
				+ getFmtPoint(shanghai) + "," 
				+ getFmtPoint(guangzhou) + ","
				+ getFmtPoint(shenzhen) + "," 
				+ getFmtPoint(chengdu) + "]";
	}

	public String getFmtPoint(String data) {
		DecimalFormat format = new DecimalFormat("#");
		if (data != null) {
			return format.format(Double.parseDouble(data));
		}
		return null;
	}

}
