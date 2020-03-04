    @RequestMapping(value = "/secure/HWProbes/HWProbex")
    public String elasticsearchHWProbe1(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"INSERT_KIBANA_DASHBOARD_LINK_HERE";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbesx";
    }
