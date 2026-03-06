package com.herethere.withus.deeplink;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeepLinkController {

	@GetMapping("/invite")
	public String invite(@RequestParam(required = false) String code, Model model) {
		model.addAttribute("code", code);
		return "invite";
	}
}
