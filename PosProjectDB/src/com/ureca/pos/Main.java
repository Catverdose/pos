package com.ureca.pos;

import com.ureca.pos.model.service.PosService;
import com.ureca.pos.model.service.PosServiceImp;
import com.ureca.pos.view.PosUI;

public class Main {
	public static void main(String[] args) {
		PosService service = new PosServiceImp();
		PosUI ui = new PosUI();
		ui.setModel(service);
		ui.open();
	}
}
