package com.ureca.pos.util;
import com.ureca.pos.model.dao.PosDaoJang;

import com.ureca.pos.model.dao.PosDao;

public class PosFactory {
//	private static final EmployeeDao dao = new EmployeeDaoMemory();
//	private static final EmployeeDao dao = new EmployeeDaoFile();
	private static final PosDao dao = new PosDaoJang();
	public static PosDao getPosDao() {
		return dao;
	}
}
