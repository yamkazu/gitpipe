class UrlMappings {

	static mappings = {
        // singup
        "/signup/created"(controller: "signup", action: "created")
        "/signup"(controller: "signup") {
            action = [GET: "form", POST: "create"]
        }

        // login
        "/login/$action?"(controller: "login")
        "/logout/$action?"(controller: "logout")

		"/"(view: "index")
		"500"(view:'/error')
	}
}
