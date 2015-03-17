package com.Object;

public class DiemCao {
	private int diem;
	private String ThoiDiem;

	public int getDiem() {
		return diem;
	}

	public void setDiem(int diem) {
		this.diem = diem;
	}

	public String getThoiDiem() {
		return ThoiDiem;
	}

	public void setThoiDiem(String thoiDiem) {
		ThoiDiem = thoiDiem;
	}

	@Override
	public String toString() {
		return ThoiDiem + diem;
	}
}
