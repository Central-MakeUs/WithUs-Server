package com.herethere.withus.deeplink;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeepLinkController {

	@GetMapping("/invite")
	public String invite() {
		return "invite";
	}
}
