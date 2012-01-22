class UrlMappings {

	static mappings = {
        // sing up
        "/signup/created"(controller: "signup", action: "created")
        "/signup"(controller: "signup") {
            action = [GET: "form", POST: "create"]
        }

        // login
        "/login/$action?"(controller: "login")
        "/logout/$action?"(controller: "logout")

        // account
        "/account/admin/password"(controller: "account") {
            action = "updatePassword"
        }
        "/account/admin"(controller: "account") {
            action = [GET: "showAdmin"]
        }
        "/account"(controller: "account") {
            action = [GET: "showAccount", POST: "updateAccount"]
        }

        // repository
        name repository_tree: "/$username/$project/tree/$ref/$path**"(controller: "repository", action: "tree")
        name repository_blob: "/$username/$project/blob/$ref/$path**"(controller: "repository", action: "blob")
        "/$username/$project"(controller: "repository", action: "show")
        "/repository"(controller: "repository") {
            action = [GET: "form", POST: "create"]
        }

        // user
        "/$username"(controller: "user", action: "show")

        // other
		"/"(view: "index")
		"500"(view:'/error')
	}
}
