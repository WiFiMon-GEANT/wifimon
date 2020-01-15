    @RequestMapping(value = "/secure/Subnets/a_b_c_d_xx")
    public String elasticsearchSubnets_a_b_c_d_xx(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;
	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"ENTER_KIBANA_VISUALIZATION_LINK_HERE";
	model.addAttribute("classActiveSettingsSubnets", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchSubnets_a_b_c_d_xx";
}
